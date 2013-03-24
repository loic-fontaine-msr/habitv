package com.dabi.habitv.provider.tmc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.provider.BasePluginProvider;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class TMCPluginManager extends BasePluginProvider {

	@Override
	public String getName() {
		return TMCConf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodes = new HashSet<>();

		// http://videos.tmc.tv/miss-marple/video-integrale/
		// http://videos.tmc.tv/walker-texas-ranger/video-integrale/
		// http://videos.tmc.tv/suspect-numero-1/saison-3/video-integrale/

		final String category_url = "/" + category.getId() + "/";
		// System.out.println("category_url=" + category_url);
		Document doc = Jsoup.parse(getUrlContent(TMCConf.VIDEO_URL + category_url));
		final Elements select = doc.select(".accrocheTMC");
		for (final Element element : select) {
			// System.out.println("element=" + element.toString());
			final String video_integrale_url = element.attr("href");
			// System.out.println("video_integrale_url=" +
			// video_integrale_url);
			doc = Jsoup.parse(getUrlContent(TMCConf.VIDEO_URL + video_integrale_url));
			final Elements select2 = doc.select(".teaser");
			for (final Element element2 : select2) {
				// System.out.println("element2=" + element2.toString());
				final Element anchor = element2.child(1).child(0).child(0);
				// System.out.println("anchor" + anchor);
				final String url = anchor.attr("href");
				// System.out.println("url=" + url);
				final String title = anchor.ownText();
				// System.out.println("title=" + title);
				episodes.add(new EpisodeDTO(category, title, url));
			}
		}
		return episodes;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		// http://www.tmc.tv/liste-programme-tv/
		// http://www.tmc.tv/liste-programme-tv/index-848-UCA[yz0123].html
		// NOTE: The pages can be extracted from the initial page!
		final String[] pages = { "/liste-programme-tv/", "/liste-programme-tv/index-848-UCAy.html", "/liste-programme-tv/index-848-UCAz.html",
				"/liste-programme-tv/index-848-UCA0.html", "/liste-programme-tv/index-848-UCA1.html", "/liste-programme-tv/index-848-UCA2.html",
		"/liste-programme-tv/index-848-UCA3.html" };

		final Set<CategoryDTO> categories = new HashSet<>();

		for (final String page : pages) {
			final String url = TMCConf.HOME_URL + page;
			// System.out.println("url=" + url);
			final Document doc = Jsoup.parse(getUrlContent(url));
			final Elements select = doc.select(".prg");
			for (final Element element : select) {
				final Element anchor = element.child(0);
				// System.out.println("anchor=" + anchor.toString());
				final String attr = anchor.attr("onmousedown");
				final String[] strings = attr.split("\\|");
				if (strings.length == 3) {
					final Element image = element.child(0).child(0);
					final String name = image.attr("alt");
					// System.out.println("name=" + name);
					final String identifier = strings[1];
					// System.out.println("identifier=" + identifier);
					categories.add(new CategoryDTO(TMCConf.NAME, name, identifier, TMCConf.EXTENSION));
				}
			}
		}
		return categories;
	}

	private String getDownloader(final String url) {
		String downloaderName;
		if (url.startsWith(TMCConf.RTMPDUMP_PREFIX)) {
			downloaderName = TMCConf.RTMDUMP;
		} else {
			downloaderName = TMCConf.CURL;
		}
		return downloaderName;
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener,
			final EpisodeDTO episode) throws DownloadFailedException, NoSuchDownloaderException {
		final String mediaId = findFinalUrl(episode);

		String videoUrl = getUrlContent(buildUrlVideoInfo(mediaId, "web"));

		final String downloaderName = getDownloader(videoUrl);
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());

		if (TMCConf.RTMDUMP.equals(downloaderName)) {
			videoUrl = videoUrl.replace(",rtmpte", "");
			videoUrl = videoUrl.substring(0, videoUrl.lastIndexOf("?"));
			parameters.put(FrameworkConf.PARAMETER_ARGS, TMCConf.DUMP_CMD);
			pluginDownloader.download(videoUrl, downloadOuput, parameters, cmdProgressionListener, getProtocol2proxy());
			// pluginDownloader.download(videoUrl, downloadOuput, parameters,
			// cmdProgressionListener, downloaders.getProtocol2proxy());
		} else {
			pluginDownloader.download(videoUrl, downloadOuput, parameters, cmdProgressionListener, getProtocol2proxy());
			// pluginDownloader.download(videoUrl, downloadOuput, parameters,
			// cmdProgressionListener, downloaders.getProtocol2proxy());
		}
	}

	private static final Pattern MEDIAID_PATTERN = Pattern.compile("mediaId : (\\d*),");

	static String buildToken(final String id, final String timestamp, final String contextRoot) {
		// my $hexdate = sprintf("%x",time());
		// fill up triling zeroes
		// final String dateS = String.format("%x", timestamp);
		final String dateS = Long.toHexString(Long.valueOf(timestamp)).toLowerCase();
		final StringBuilder dateSC = new StringBuilder(dateS);
		for (int i = 0; i < (dateS.length() - 8); i++) {
			dateSC.append("0");
		}
		// $hexdate .= "0" x (length($hexdate) - 8);
		final String key = "9b673b13fa4682ed14c3cfa5af5310274b514c4133e9b3a81e6e3aba00912564";
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest((key + "/" + contextRoot + "/" + id + dateSC.toString()).getBytes());
		} catch (final NoSuchAlgorithmException e) {
			throw new TechnicalException(e);
		}
		final StringBuilder hashString = new StringBuilder();
		for (int i = 0; i < hash.length; i++) {
			final String hex = Integer.toHexString(hash[i]);
			if (hex.length() == 1) {
				hashString.append('0');
				hashString.append(hex.charAt(hex.length() - 1));
			} else {
				hashString.append(hex.substring(hex.length() - 2));
			}
		}
		return hashString.toString() + "/" + dateS;
	}

	public String findFinalUrl(final EpisodeDTO episode) {
		final String content = getUrlContent(TMCConf.VIDEO_URL + episode.getUrl());
		final String mainMediaId = findMediaId(content);
		return mainMediaId;
	}

	public String buildUrlVideoInfo(final String mediaId, final String contextRoot) {
		// http://www.wat.tv/get/web/9919167?token=f53ae02a07a1a468de31db84d2f12b3a/514dd0ac&domain=videos.tmc.tv&domain2=null&refererURL=%2Fwalker-texas-ranger%2Fsaison-4%2Fwalker-texas-ranger-saison-4-episode-11-le-meilleur-ami-de-l-homme-7886945-848.html&revision=4.1.151&synd=0&helios=1&context=swftmc&pub=1&country=FR&sitepage=tmc.tv%2Fvideos-tmc%2Fcatchup%2Fwalker-texas-ranger%2Fsaison-4%2Fint%2F20130322&lieu=tmc&playerContext=CONTEXT_TMC&getURL=1&version=WIN%2011,6,602,180
		// "http://www.wat.tv" - TMCConf.WAT_HOME
		// "/get/"
		// "web" - contextRoot
		// "/"
		// "9919167" - mediaId
		// "?token=" -
		// "f53ae02a07a1a468de31db84d2f12b3a/514dd0ac" - buildToken
		// "&domain=videos.tmc.tv&domain2=null&refererURL=%2Fwalker-texas-ranger%2Fsaison-4%2Fwalker-texas-ranger-saison-4-episode-11-le-meilleur-ami-de-l-homme-7886945-848.html&revision=4.1.151&synd=0&helios=1&context=swftmc&pub=1&country=FR&sitepage=tmc.tv%2Fvideos-tmc%2Fcatchup%2Fwalker-texas-ranger%2Fsaison-4%2Fint%2F20130322&lieu=tmc&playerContext=CONTEXT_TMC&getURL=1&version=WIN%2011,6,602,180"
		return TMCConf.WAT_HOME + "/get/" + contextRoot + "/" + mediaId + "?token=" + buildToken(mediaId, findTimeStamp(), contextRoot)
				+ "&country=FR&getURL=1&version=WIN 11,5,502,146";
	}

	private String findTimeStamp() {
		final String content = getUrlContent(TMCConf.WAT_HOME + "/servertime");
		return content.split("\\|")[0];
	}

	private static String findMediaId(final String content) {
		final Matcher matcher = MEDIAID_PATTERN.matcher(content);
		final boolean hasMatched = matcher.find();
		String ret = null;
		if (hasMatched) {
			ret = matcher.group(matcher.groupCount());
		} else {
			throw new TechnicalException("can't find mediaId");
		}
		return ret;
	}
}
