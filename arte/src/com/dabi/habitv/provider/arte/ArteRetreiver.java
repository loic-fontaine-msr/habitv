package com.dabi.habitv.provider.arte;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

class ArteRetreiver {

	private static final String SEP = "/";

	private ArteRetreiver() {

	}

	public static Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO category, final InputStream inputStream) {
		final Set<EpisodeDTO> episodeList;
		try {
			final SyndFeedInput input = new SyndFeedInput();
			final SyndFeed feed = input.build(new XmlReader(inputStream, true, "UTF-8"));
			episodeList = convertFeedToEpisodeList(feed, category);
		} catch (IllegalArgumentException | FeedException | IOException e) {
			throw new TechnicalException(e);
		}
		return episodeList;
	}

	private static Set<EpisodeDTO> convertFeedToEpisodeList(final SyndFeed feed, final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new HashSet<EpisodeDTO>();
		final List<?> entries = feed.getEntries();
		for (final Object object : entries) {
			final SyndEntry entry = (SyndEntry) object;
			episodeList.add(new EpisodeDTO(category, entry.getTitle(), entry.getLink()));
		}
		return episodeList;
	}

	public static Set<CategoryDTO> findCategories(String urlContent) {
		Set<CategoryDTO> categoryDTOs = new HashSet<>();
		// compilation de la regex
		final Pattern pattern = Pattern.compile("<a href=\"([^\\,]*),view,rss.xml\" class=\"rss\">([^\\<]*)</a>");
		final Matcher matcher = pattern.matcher(urlContent);
		String categoryName;
		String identifier;
		while (matcher.find()) {
			identifier = findShowIdentifier(matcher.group(1));
			categoryName = matcher.group(2);
			categoryDTOs.add(new CategoryDTO(ArteConf.NAME, categoryName, identifier, ArteConf.EXTENSION));
		}
		return categoryDTOs;
	}

	private static String findShowIdentifier(String url) {
		String[] subUrl = url.split(SEP);
		if (subUrl.length > 2) {
			return subUrl[subUrl.length - 2] + SEP + subUrl[subUrl.length - 1];
		}
		return null;
	}

	public static String buildDownloadLink(String url) throws DownloadFailedException {
		String episodeId = findEpisodeIdentifier(url);
		String xmlInfo = RetrieverUtils.getUrlContent(ArteConf.XML_INFO_URL.replace(ArteConf.ID_EPISODE_TOKEN, episodeId));
		Pattern pattern = Pattern.compile("<video lang=\"fr\" ref=\"([^\\\"]*)\"\\s/>"); // FIXME
																							// static
		Matcher matcher = pattern.matcher(xmlInfo);
		final String xmlVideoInfoUrl;
		if (matcher.find()) {
			xmlVideoInfoUrl = matcher.group(matcher.groupCount());
		} else {
			throw new DownloadFailedException("can't find xml video info url");
		}
		String xmlVideoInfo = RetrieverUtils.getUrlContent(xmlVideoInfoUrl);
		pattern = Pattern.compile("<url quality=\"(\\w+)\">([^\\<]*)</url>");
		matcher = pattern.matcher(xmlVideoInfo);
		Map<String, String> qualityToVideoUrl = new HashMap<>();
		String videoUrl;
		String quality;
		while (matcher.find()) {
			quality = matcher.group(1);
			videoUrl = matcher.group(2);
			qualityToVideoUrl.put(quality, videoUrl);
		}
		return findBestQuality(qualityToVideoUrl);
	}

	private static String findBestQuality(Map<String, String> qualityToVideoUrl) throws DownloadFailedException {
		String videoUrl = qualityToVideoUrl.get("hd");
		if (videoUrl == null) {
			videoUrl = qualityToVideoUrl.get("sd");
			if (videoUrl == null) {
				videoUrl = qualityToVideoUrl.get("link");
				if (videoUrl == null && qualityToVideoUrl.size() > 0) {
					videoUrl = (new ArrayList<Map.Entry<String, String>>(qualityToVideoUrl.entrySet())).get(0).getValue();
				} else {
					throw new DownloadFailedException("no link found");
				}
			}
		}
		return videoUrl;
	}

	private static String findEpisodeIdentifier(String url) {
		String[] subUrl = url.split(SEP);
		if (subUrl.length > 1) {
			return subUrl[subUrl.length - 1].replace(".html", "");
		}
		return null;
	}
}
