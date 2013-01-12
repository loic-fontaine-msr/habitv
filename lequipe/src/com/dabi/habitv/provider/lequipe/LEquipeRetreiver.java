package com.dabi.habitv.provider.lequipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public class LEquipeRetreiver {

	private LEquipeRetreiver() {

	}

	public static Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO category) {
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

	private static void findEpisodeByUrl(final CategoryDTO category, final Set<EpisodeDTO> episodeList, final String pageUrl) {
		try {
			final Connection con = Jsoup.connect(pageUrl).userAgent(LEquipeConf.USER_AGENT).timeout(15000);
			Elements elementsByClass = con.get().getElementsByClass("content");
			if (elementsByClass.isEmpty()) {
				elementsByClass = con.get().getElementsByClass("results");
				for (final Element aResult : elementsByClass.get(0).children()) {
					final String hRef = aResult.attr("href");
					final String name = aResult.getElementsByClass("title").text();
					episodeList.add(new EpisodeDTO(category, name, hRef));
				}
			} else {
				final Elements divResults = elementsByClass.get(0).child(0).children();
				for (final Element divResult : divResults) {
					if (divResult.children().size() > 0) {
						final Element aResult = divResult.child(0);
						final String hRef = aResult.attr("href");
						final String name = aResult.child(2).text();
						episodeList.add(new EpisodeDTO(category, name, hRef));
					}
				}
			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
	}

	public static String findDownloadlink(final String url) {
		final String originalUrl = LEquipeConf.VIDEO_HOME_URL + url;
		final String htmlContent = RetrieverUtils.getUrlContent(originalUrl);
		Pattern pattern = Pattern.compile("<param name=\"flashVars\" value=\"language_code=fr&amp;playerKey=(.*)&amp[&amp]*;suffix=&amp;sig=(.*)\">");
		Matcher matcher = pattern.matcher(htmlContent);
		// lancement de la recherche de toutes les occurrences
		boolean hasMatched = matcher.find();
		// si recherche fructueuse
		String sig = null;
		String playerKey = null;
		if (hasMatched) {
			playerKey = matcher.group(1);
			sig = matcher.group(2);
		} else {
			pattern = Pattern.compile("\\{\\\'sig\\\'\\:\\\'(.*)\\\',\\\'playerkey\\\':\\\'(.*)\\\',\\\'vformat\\\'\\:\\\'");
			matcher = pattern.matcher(htmlContent);
			// lancement de la recherche de toutes les occurrences
			hasMatched = matcher.find();
			// si recherche fructueuse
			if (hasMatched) {
				playerKey = matcher.group(2);
				sig = matcher.group(1);
			} else {
				throw new TechnicalException("sig and player key not found");
			}
		}
		try {
			return findDownloadlinkBySigAndPlayerKey(sig, playerKey, originalUrl);
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
	}

	private static String findDownloadlinkBySigAndPlayerKey(final String sig, final String playerKey, final String originalUrl) throws IOException {
		final String url = "http://api.kewego.com/config/getStreamInit/";
		final URLConnection hc = (new URL(url)).openConnection();
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
		return "\"http://api.kewego.com/video/getStream/?appToken=" + token + "&sig=" + sig + "&format=w640&v=2749\"";
	}

	public static Set<CategoryDTO> findCategory() {
		try {
			final Set<CategoryDTO> categoryDTOs = new HashSet<>();
			final Connection con = Jsoup.connect(LEquipeConf.VIDEO_HOME_URL).userAgent(LEquipeConf.USER_AGENT).timeout(15000);

			final Elements aOngletsSport = con.get().select(".onglets_sports").get(0).children();
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
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
	}

	private static Collection<CategoryDTO> findSubCategories(final String categoryHref) {
		try {
			final Set<CategoryDTO> categoryDTOs = new HashSet<>();
			final Connection con = Jsoup.connect(LEquipeConf.VIDEO_HOME_URL + categoryHref).userAgent(LEquipeConf.USER_AGENT).timeout(15000);

			final Elements aOngletsSport = con.get().select("#k_sous").get(0).children();
			for (final Element aHref : aOngletsSport) {
				final String href = aHref.attr("href");
				if (href.length() > 1) {
					final String content = aHref.text();
					final CategoryDTO categoryDTO = new CategoryDTO(LEquipeConf.NAME, content, href, LEquipeConf.EXTENSION);
					categoryDTOs.add(categoryDTO);
				}

			}
			return categoryDTOs;
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
	}
}
