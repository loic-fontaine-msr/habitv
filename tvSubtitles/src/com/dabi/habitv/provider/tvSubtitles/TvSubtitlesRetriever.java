package com.dabi.habitv.provider.tvSubtitles;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;

public final class TvSubtitlesRetriever {

	private static final Pattern EP_NUMBER_PATTERN = Pattern.compile(".*\\s+(\\d+x\\d+)\\s+.*");

	private TvSubtitlesRetriever() {

	}

	private static Set<EpisodeDTO> findEpisodeBySeason(final CategoryDTO category, final Source source) {
		final Set<EpisodeDTO> episodeList = new HashSet<>();
		try {
			for (final Segment segment : source.getAllStartTags("tr align=\"middle\" bgcolor=\"#ffffff\"")) {
				if (segment.getChildElements().size() > 0 && segment.getChildElements().get(0).getChildElements().size() > 0) {
					final String url = segment.getChildElements().get(0).getChildElements().get(1).getChildElements().get(0).getAttributeValue("href");
					episodeList.addAll(findReleaseByEpisode(category, url, false));
				}
			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
		return episodeList;

	}

	public static Set<EpisodeDTO> filterByEpNumberOnly(final Set<EpisodeDTO> episodeList) {
		final Set<EpisodeDTO> newEpisodeList = new HashSet<>();
		final Set<String> epNumberList = new HashSet<>();
		String epNumber;
		for (final EpisodeDTO episode : episodeList) {
			epNumber = findEpNumber(episode);
			if (!episodeList.contains(epNumber)) {
				newEpisodeList.add(new EpisodeDTO(episode.getCategory(), epNumber, episode.getUrl()));
				epNumberList.add(epNumber);
			}
		}
		return newEpisodeList;
	}

	private static String findEpNumber(final EpisodeDTO episode) {
		final Matcher matcher = EP_NUMBER_PATTERN.matcher(episode.getName());
		if (matcher.find()) {
			return matcher.group(matcher.groupCount());
		}
		return null;
	}

	public static Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO category) throws IOException {
		final Set<EpisodeDTO> episodeList = new HashSet<>();
		final URL url = new URL(TvSubtitlesConf.HOME_URL + "/" + category.getId());
		final URLConnection urlConn = url.openConnection();
		urlConn.setConnectTimeout(FrameworkConf.TIME_OUT_MS);
		urlConn.setReadTimeout(FrameworkConf.TIME_OUT_MS);
		urlConn.setRequestProperty("User-Agent", TvSubtitlesConf.USER_AGENT);

		final Source source = new Source(urlConn);
		final Segment seasonTabP = source.getAllStartTags("p class=\"description\"").get(0);
		final List<Element> seasonLinks = seasonTabP.getChildElements().get(0).getChildElements();
		if (!seasonLinks.isEmpty()) {
			for (final Element element : seasonLinks) {
				final String seasonUrl = element.getAttributeValue("href");
				if (seasonUrl != null) {
					episodeList.addAll(findEpisodeBySeason(category, new Source(new URL(TvSubtitlesConf.HOME_URL + "/" + seasonUrl))));
				}
			}
		}

		episodeList.addAll(findEpisodeBySeason(category, source));

		return episodeList;
	}

	public static Collection<EpisodeDTO> findReleaseByEpisode(final CategoryDTO category, final String episodeUrl, final boolean dlLink)
			throws MalformedURLException, IOException {

		final URL url = new URL(TvSubtitlesConf.HOME_URL + "/" + episodeUrl);
		final URLConnection urlConn = url.openConnection();
		urlConn.setConnectTimeout(FrameworkConf.TIME_OUT_MS);
		urlConn.setReadTimeout(FrameworkConf.TIME_OUT_MS);
		urlConn.setRequestProperty("User-Agent", TvSubtitlesConf.USER_AGENT);

		final Source source = new Source(urlConn);

		Integer previousRate = null;
		final Map<String, Integer> nameToRate = new HashMap<>();
		final Map<String, EpisodeDTO> nameToEpisode = new HashMap<>();
		if (source.getAllStartTags("div title").size() > 0) {
			for (final Segment segment2 : source.getAllStartTags("a href=")) {
				if (isLanguageBloc(segment2)) {
					final String language = getLanguage(segment2);
					final String epName = getEpName(segment2);
					final int rate = findRate(segment2);
					if (language != null && (language.contains(TvSubtitlesConf.LANGUAGE) || language.contains(TvSubtitlesConf.LANGUAGE2))) {
						final String dlUrl = segment2.getChildElements().get(0).getAttributeValue("href");

						previousRate = nameToRate.get(epName);

						if (previousRate == null || rate > previousRate) {
							nameToEpisode.put(epName, new EpisodeDTO(category, epName, (dlLink) ? dlUrl : episodeUrl));
							nameToRate.put(epName, rate);
						}
						previousRate = rate;
					}
				}
			}
		}
		return nameToEpisode.values();
	}

	private static boolean isLanguageBloc(final Segment segment2) {
		return segment2.getChildElements().size() > 0 && segment2.getChildElements().get(0).getChildElements().size() > 0
				&& segment2.getChildElements().get(0).getAllStartTags("div title").size() > 0;
	}

	private static String getEpName(final Segment segment2) {
		String epName = segment2.getChildElements().get(0).getChildElements().get(0).getChildElements().get(1).getContent().toString();
		epName = epName.substring(epName.lastIndexOf(">") + 1, epName.length());
		return epName;
	}

	private static String getLanguage(final Segment segment2) {
		return segment2.getChildElements().get(0).getChildElements().get(0).getAttributeValue("title");
	}

	public static String findDownloadLink(final String dlUrl) throws IOException {
		final URL url = new URL(TvSubtitlesConf.HOME_URL + "/" + dlUrl);
		final URLConnection urlConn = url.openConnection();
		urlConn.setConnectTimeout(FrameworkConf.TIME_OUT_MS);
		urlConn.setReadTimeout(FrameworkConf.TIME_OUT_MS);
		urlConn.setRequestProperty("User-Agent", TvSubtitlesConf.USER_AGENT);

		final Source source = new Source(urlConn);

		for (final Segment segment : source.getAllStartTags("a href=")) {
			if (segment.getChildElements().size() > 0) {
				final List<StartTag> imgTag = segment.getChildElements().get(0).getAllStartTags("img src");
				if (imgTag.size() > 0) {
					final String imgTitle = imgTag.get(0).getAttributeValue("title");
					if (imgTitle != null && imgTitle.equals("Download")) {
						return segment.getChildElements().get(0).getAttributeValue("href");
					}
				}
			}
		}
		return null;
	}

	private static int findRate(final Segment segment2) {
		int rate = 0;
		final String valRate = segment2.getChildElements().get(0).getChildElements().get(0).getChildElements().get(0).getChildElements().get(0)
				.getChildElements().get(1).getContent().toString();
		try {
			rate = Integer.valueOf(valRate);
		} catch (final NumberFormatException e) {
			// nothing
		}

		return rate;
	}
}
