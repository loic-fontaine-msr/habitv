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

	private static final Pattern REF_PATTERN = Pattern.compile("<video lang=\"fr\" ref=\"([^\\\"]*)\"\\s/>");

	private static final Pattern QUALITY_PATTERN = Pattern.compile("<url quality=\"(\\w+)\">([^\\<]*)</url>");

	private static final Pattern LINK_TITLE_PATTERN = Pattern.compile("<a href=\"([^\\,]*),view,rss.xml\" class=\"rss\">([^\\<]*)</a>");

	private ArteRetreiver() {

	}

	public static Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO category, final InputStream inputStream) {
		final Set<EpisodeDTO> episodeList;
		try {
			final SyndFeedInput input = new SyndFeedInput();
			final SyndFeed feed = input.build(new XmlReader(inputStream, true, ArteConf.ENCODING));
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

	public static Set<CategoryDTO> findCategories(final String urlContent) {
		final Set<CategoryDTO> categoryDTOs = new HashSet<>();
		final Matcher matcher = LINK_TITLE_PATTERN.matcher(urlContent);
		String categoryName;
		String identifier;
		while (matcher.find()) {
			identifier = findShowIdentifier(matcher.group(1));
			categoryName = matcher.group(2);
			categoryDTOs.add(new CategoryDTO(ArteConf.NAME, categoryName, identifier, ArteConf.EXTENSION));
		}
		return categoryDTOs;
	}

	private static String findShowIdentifier(final String url) {
		final String[] subUrl = url.split(SEP);
		if (subUrl.length > 2) {
			return subUrl[subUrl.length - 2] + SEP + subUrl[subUrl.length - 1];
		}
		throw new TechnicalException("can't find show identifier");
	}

	public static String buildDownloadLink(final String url) throws DownloadFailedException {
		final String episodeId = findEpisodeIdentifier(url);
		final String xmlInfo = RetrieverUtils.getUrlContent(ArteConf.XML_INFO_URL.replace(ArteConf.ID_EPISODE_TOKEN, episodeId), ArteConf.ENCODING);
		Matcher matcher = REF_PATTERN.matcher(xmlInfo);
		final String xmlVideoInfoUrl;
		if (matcher.find()) {
			xmlVideoInfoUrl = matcher.group(matcher.groupCount());
		} else {
			throw new DownloadFailedException("can't find xml video info url");
		}
		final String xmlVideoInfo = RetrieverUtils.getUrlContent(xmlVideoInfoUrl, ArteConf.ENCODING);
		matcher = QUALITY_PATTERN.matcher(xmlVideoInfo);
		final Map<String, String> qualityToVideoUrl = new HashMap<>();
		String videoUrl;
		String quality;
		while (matcher.find()) {
			quality = matcher.group(1);
			videoUrl = matcher.group(2);
			qualityToVideoUrl.put(quality, videoUrl);
		}
		return findBestQuality(qualityToVideoUrl);
	}

	private static String findBestQuality(final Map<String, String> qualityToVideoUrl) throws DownloadFailedException {
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

	private static String findEpisodeIdentifier(final String url) throws DownloadFailedException {
		final String[] subUrl = url.split(SEP);
		if (subUrl.length > 1) {
			return subUrl[subUrl.length - 1].replace(".html", "");
		}
		throw new DownloadFailedException("Episode identifier not found");
	}
}
