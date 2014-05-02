package com.dabi.habitv.provider.lequipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.api.plugin.api.PluginProviderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.utils.DownloadUtils;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.framework.plugin.utils.SoccerUtils;

public class LEquipePluginManager extends BasePluginWithProxy implements PluginDownloaderInterface, PluginProviderInterface {

	@Override
	public String getName() {
		return LEquipeConf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new HashSet<>();
		final String baseUrl = LEquipeConf.VIDEO_HOME_URL + category.getId();
		findEpisodeByUrl(category, episodeList, baseUrl);
		String pageUrl;
		for (int i = 2; i <= LEquipeConf.MAX_PAGE; i++) {
			pageUrl = baseUrl + "page/" + i + "/";
			findEpisodeByUrl(category, episodeList, pageUrl);
		}

		return episodeList;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categoryDTOs = new HashSet<>();
		final Document doc = Jsoup.parse(getUrlContent(LEquipeConf.VIDEO_HOME_URL));

		final Elements aOngletsSport = doc.select(".onglets_sports").get(0).children();
		for (final Element aHref : aOngletsSport) {
			final String href = aHref.attr("href");
			if (href.length() > 1) {
				final String content = aHref.text();
				final CategoryDTO categoryDTO = new CategoryDTO(LEquipeConf.NAME, content, href, LEquipeConf.EXTENSION);
				categoryDTO.addSubCategories(findSubCategories(href));
				categoryDTOs.add(categoryDTO);
			}

		}
		return categoryDTOs;
	}

	@Override
	public void download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders, final CmdProgressionListener listener)
			throws DownloadFailedException {
		final String videoUrl = findDownloadlink(downloadParam.getDownloadInput());
		DownloadUtils.download(DownloadParamDTO.buildDownloadParam(downloadParam, videoUrl), downloaders, listener);

	}

	private void findEpisodeByUrl(final CategoryDTO category, final Set<EpisodeDTO> episodeList, final String pageUrl) {
		final Document doc = Jsoup.parse(getUrlContent(pageUrl));
		Elements elementsByClass = doc.getElementsByClass("content");
		if (elementsByClass.isEmpty()) {
			elementsByClass = doc.getElementsByClass("results");
			for (final Element aResult : elementsByClass.get(0).children()) {
				final String hRef = aResult.attr("href");
				final String name = aResult.getElementsByClass("title").text();
				episodeList.add(new EpisodeDTO(category, SoccerUtils.maskScore(name), hRef));
			}
		} else {
			final Elements divResults = elementsByClass.get(0).child(0).children();
			for (final Element divResult : divResults) {
				if (divResult.children().size() > 0) {
					final Element aResult = divResult.child(0);
					final String hRef = aResult.attr("href");
					final String name = aResult.child(2).text();
					episodeList.add(new EpisodeDTO(category, SoccerUtils.maskScore(name), hRef));
				}
			}
		}
	}

	public String findDownloadlink(final String url) {
		final String originalUrl = LEquipeConf.VIDEO_HOME_URL + url;
		final String htmlContent = getUrlContent(originalUrl);
		final Pattern pattern = Pattern.compile(".*<param name=\"flashVars\" value=\"([^\"]*)\".*");
		final Matcher matcher = pattern.matcher(htmlContent);
		// lancement de la recherche de toutes les occurrences
		final boolean hasMatched = matcher.find();
		// si recherche fructueuse
		String sig = null;
		String playerKey = null;
		if (hasMatched) {
			final String parameters = matcher.group(matcher.groupCount());
			final Map<String, String> params = buildParamsMap(parameters);
			sig = params.get("sig");
			playerKey = params.get("playerKey");
		} else {
			throw new TechnicalException("sig and player key not found");
		}
		try {
			return findDownloadlinkBySigAndPlayerKey(sig, playerKey, originalUrl);
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
	}

	// language_code=fr&playerKey=b905c4789fb0&configKey=29597e193d5d&suffix=&sig=86f34508368s&autostart=true"
	private Map<String, String> buildParamsMap(final String parameters) {
		StringBuilder paramNameBldr = new StringBuilder();
		StringBuilder paramValueBldr = new StringBuilder();
		String currentParam = null;
		final Map<String, String> params = new HashMap<String, String>();
		for (final char c : StringEscapeUtils.unescapeXml(parameters).toCharArray()) {
			switch (c) {
			case '=':
				currentParam = paramNameBldr.toString();
				paramNameBldr = new StringBuilder();
				break;
			case '&':
			case '?':
				params.put(currentParam, paramValueBldr.toString());
				currentParam = null;
				paramValueBldr = new StringBuilder();
				break;
			default:
				if (currentParam == null) {
					paramNameBldr.append(c);
				} else {
					paramValueBldr.append(c);
				}
				break;
			}
		}
		if (currentParam != null) {
			params.put(currentParam, paramValueBldr.toString());
		}
		return params;
	}

	private String findDownloadlinkBySigAndPlayerKey(final String sig, final String playerKey, final String originalUrl) throws IOException {
		final String url = "http://api.kewego.com/config/getStreamInit/";
		final Proxy httpProxy = getHttpProxy();
		final URLConnection hc;
		if (RetrieverUtils.useProxy(httpProxy)) {
			hc = (new URL(url)).openConnection(httpProxy);
		} else {
			hc = (new URL(url)).openConnection();
		}

		hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1");
		hc.setRequestProperty("X-KDORIGIN", URLEncoder.encode(originalUrl, "UTF-8"));
		String data = URLEncoder.encode("player_type", "UTF-8") + "=" + URLEncoder.encode("kp", "UTF-8");
		data += "&" + URLEncoder.encode("sig", "UTF-8") + "=" + URLEncoder.encode(sig, "UTF-8");
		data += "&" + URLEncoder.encode("playerKey", "UTF-8") + "=" + URLEncoder.encode(playerKey, "UTF-8");
		data += "&" + URLEncoder.encode("request_verbose", "UTF-8") + "=" + URLEncoder.encode("false", "UTF-8");
		data += "&" + URLEncoder.encode("language_code", "UTF-8") + "=" + URLEncoder.encode("fr", "UTF-8");
		hc.setDoOutput(true);
		final OutputStreamWriter wr = new OutputStreamWriter(hc.getOutputStream());
		wr.write(data);
		wr.flush();
		// Get the response
		final BufferedReader rd = new BufferedReader(new InputStreamReader(hc.getInputStream()));
		String line;
		final StringBuilder builder = new StringBuilder();
		while ((line = rd.readLine()) != null) {
			builder.append(line);
		}
		wr.close();
		rd.close();

		final Pattern pattern = Pattern.compile("<playerAppToken>(.*)</playerAppToken>");
		final Matcher matcher = pattern.matcher(builder.toString());
		// lancement de la recherche de toutes les occurrences
		final boolean hasMatched = matcher.find();
		// si recherche fructueuse
		String token = null;
		if (hasMatched) {
			token = matcher.group(matcher.groupCount());
		} else {
			throw new TechnicalException("can't find token");
		}
		return "\"http://api.kewego.com/video/getStream/?appToken=" + token + "&sig=" + sig + "&format=high&v=2749\"";
	}

	private Collection<CategoryDTO> findSubCategories(final String categoryHref) {
		final Set<CategoryDTO> categoryDTOs = new HashSet<>();
		final Document doc = Jsoup.parse(getUrlContent(LEquipeConf.VIDEO_HOME_URL + categoryHref));

		final Elements aOngletsSport = doc.select("#k_sous").get(0).children();
		for (final Element aHref : aOngletsSport) {
			final String href = aHref.attr("href");
			if (href.length() > 1) {
				final String content = aHref.text();
				final CategoryDTO categoryDTO = new CategoryDTO(LEquipeConf.NAME, content, href, LEquipeConf.EXTENSION);
				categoryDTOs.add(categoryDTO);
			}

		}
		return categoryDTOs;
	}

}
