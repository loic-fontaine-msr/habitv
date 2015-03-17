package com.dabi.habitv.provider.beinsport;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabi.habitv.api.plugin.api.PluginProviderDownloaderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.utils.DownloadUtils;
import com.dabi.habitv.framework.plugin.utils.SoccerUtils;

public class BeinSportPluginManager extends BasePluginWithProxy implements
		PluginProviderDownloaderInterface { // NO_UCD

	@Override
	public String getName() {
		return BeinSportConf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		if (BeinSportConf.REPLAY_CATEGORY.equals(category.getId())) {
			final Set<EpisodeDTO> episodeDTOs = new LinkedHashSet<>();
			for (final CategoryDTO subCategory : findReplaycategories()) {
				episodeDTOs.addAll(findEpisodeByCategory(subCategory,
						BeinSportConf.HOME_URL + "/" + subCategory.getId()));
			}
			return episodeDTOs;
		} else if (category.getId().startsWith(BeinSportConf.VIDEOS_CATEGORY)) {
			return findEpisodeByVideoCategory(category,
					BeinSportConf.VIDEOS_URL_RSS);
		} else {
			return findEpisodeByCategory(category, BeinSportConf.HOME_URL
					+ category.getId());
		}
	}

	private Set<EpisodeDTO> findEpisodeByVideoCategory(CategoryDTO category,
			String videosUrlRss) {
		final Set<EpisodeDTO> episodeList = new LinkedHashSet<>();
		org.jsoup.nodes.Document doc;
		try {
			String url = BeinSportConf.CATEGORIES_URL
					+ category.getId().split("_")[1];
			doc = Jsoup.parse(getInputStreamFromUrl(url), "UTF-8", url);
		} catch (IOException e) {
			throw new TechnicalException(e);
		}

		findEpisodeFromArticle(category, episodeList, doc.children());

		return episodeList;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categoryDTOs = new LinkedHashSet<>();
		CategoryDTO videoCategory = new CategoryDTO(
				BeinSportConf.VIDEOS_CATEGORY, BeinSportConf.VIDEOS_CATEGORY,
				BeinSportConf.VIDEOS_CATEGORY, BeinSportConf.EXTENSION);
		videoCategory.setDownloadable(false);
		addVideoCategories(videoCategory);
		categoryDTOs.add(videoCategory);
		final CategoryDTO replayCategory = new CategoryDTO(
				BeinSportConf.REPLAY_CATEGORY, BeinSportConf.REPLAY_CATEGORY,
				BeinSportConf.REPLAY_CATEGORY, BeinSportConf.EXTENSION);
		replayCategory.addSubCategories(findReplaycategories());
		categoryDTOs.add(replayCategory);
		return categoryDTOs;
	}

	private void addVideoCategories(CategoryDTO videoCategory) {
		org.jsoup.nodes.Document doc;
		try {
			doc = Jsoup.parse(getInputStreamFromUrl(BeinSportConf.VIDEO_URL),
					"UTF-8", BeinSportConf.VIDEO_URL);
		} catch (IOException e) {
			throw new TechnicalException(e);
		}

		Elements divSportSelect = doc.select("#sportSelect");

		for (final Element liSport : divSportSelect.select("li")) {
			final Element aHref = liSport.select("a").first();
			final String id = aHref.attr("data-sport");
			String name = aHref.text();
			CategoryDTO videosubCategory = new CategoryDTO(BeinSportConf.NAME,
					name, id, BeinSportConf.EXTENSION);
			videosubCategory.setDownloadable(false);
			addVideoCompetitions(doc, videosubCategory);
			videoCategory.addSubCategory(videosubCategory);
		}
	}

	private void addVideoCompetitions(org.jsoup.nodes.Document doc,
			CategoryDTO videosubCategory) {

		Elements divCompetitionSelect = doc.select("#competitionSelect");

		for (final Element ulCompet : divCompetitionSelect.select("ul")) {
			final String idSport = ulCompet.attr("data-sport");
			if (videosubCategory.getId().equals(idSport)) {
				for (Element liCompet : ulCompet.select("li")) {
					final Element aHref = liCompet.select("a").first();
					final String id = BeinSportConf.VIDEOS_CATEGORY + "_"
							+ aHref.attr("data-categories");
					String name = aHref.text();
					CategoryDTO videoCompetCategory = new CategoryDTO(
							BeinSportConf.NAME, name, id,
							BeinSportConf.EXTENSION);
					videoCompetCategory.setDownloadable(true);
					videosubCategory.addSubCategory(videoCompetCategory);
				}
			}
		}
	}

	@Override
	public ProcessHolder download(final DownloadParamDTO downloadParam,
			final DownloaderPluginHolder downloaders)
			throws DownloadFailedException {
		if (downloadParam.getDownloadInput().endsWith(FrameworkConf.MP4)) {
			return DownloadUtils.download(downloadParam, downloaders);
		} else {
			String m3u8Url;
			try {
				m3u8Url = findm3u8Url(downloadParam.getDownloadInput());
			} catch (UnsupportedEncodingException e) {
				throw new DownloadFailedException(e);
			}

			return DownloadUtils
					.download(DownloadParamDTO.buildDownloadParam(
							downloadParam, m3u8Url), downloaders,
							FrameworkConf.FFMPEG);
		}
	}

	private String findm3u8Url(String downloadInput)
			throws UnsupportedEncodingException {
		return findBestm3u8Url(findHSLURL(findIFrameURL(downloadInput)));
	}

	private String findBestm3u8Url(String hSLURL)
			throws UnsupportedEncodingException {
		String redirectUrl = findRedirectUrl(hSLURL);
		final String m3u8AllQuality = getUrlContent(redirectUrl);
		TreeMap<Integer, String> quality2m3u8 = new TreeMap<Integer, String>();

		Integer bandWidth = null;
		for (String line : m3u8AllQuality.split("\\n")) {
			if (line.contains("EXT-X-STREAM-INF")) {
				bandWidth = findBandWidth(line);
			} else if (bandWidth != null) {
				quality2m3u8.put(bandWidth, URLDecoder.decode(line, "UTF-8"));
				bandWidth = null;
			}
		}
		return buildFromhSLURL(redirectUrl, quality2m3u8.lastEntry().getValue());
	}

	private String findRedirectUrl(String hSLURL) {
		try {
			final HttpURLConnection hc = (HttpURLConnection) (new URL(hSLURL))
					.openConnection();
			hc.setInstanceFollowRedirects(false);
			if (hc.getResponseCode() == 302) {
				return hc.getHeaderField("Location");
			} else {
				throw new DownloadFailedException("Redirect error " + hSLURL);
			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
	}

	private String buildFromhSLURL(String hSLURL, String relativeM3u8) {
		return hSLURL.replaceFirst("/[^/]+\\?",
				"/" + relativeM3u8.substring(0, relativeM3u8.indexOf("?"))
						+ "?");
	}

	private static final Pattern BANDWITH_PATTERN = Pattern
			.compile("BANDWIDTH=(\\d+),");

	private Integer findBandWidth(String line) {
		final Matcher matcher = BANDWITH_PATTERN.matcher(line);
		final boolean hasMatched = matcher.find();
		String ret = null;
		if (hasMatched) {
			ret = matcher.group(matcher.groupCount());
		} else {
			throw new TechnicalException("can't find bandwidth");
		}
		return Integer.valueOf(ret);
	}

	private String findHSLURL(String iFrameURL) {
		final String content = getUrlContent(iFrameURL);
		return findStreamHlsUrl(content);
	}

	private String findStreamHlsUrl(String content) {
		final Matcher matcher = STREAM_HLS_PATTERN.matcher(content);
		final boolean hasMatched = matcher.find();
		String ret = null;
		if (hasMatched) {
			ret = matcher.group(matcher.groupCount());
		} else {
			throw new TechnicalException("can't find stream hls url");
		}
		return ret;
	}

	private String findIFrameURL(String downloadInput) {
		org.jsoup.nodes.Document doc;
		try {
			doc = Jsoup.parse(getInputStreamFromUrl(BeinSportConf.HOME_URL
					+ downloadInput), "UTF-8", downloadInput);
		} catch (IOException e) {
			throw new TechnicalException(e);
		}

		Elements divVodPlayer = doc.select("#vodPlayer");

		return divVodPlayer.select("iframe").first().attr("src");
	}

	private static final Pattern STREAM_HLS_PATTERN = Pattern
			.compile("\"stream_hls_url\"\\s*:\\s*\"([^\"]*)\"");

	private Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO category,
			final String url) {
		final Set<EpisodeDTO> episodeList = new LinkedHashSet<>();

		final org.jsoup.nodes.Document doc = Jsoup.parse(getUrlContent(url));

		Elements divTabContainer = doc.select("#tabContainer");
		if (divTabContainer.isEmpty()) {
			divTabContainer = doc.select("#newsIndex");
		}

		findEpisodeFromArticle(category, episodeList, divTabContainer);

		return episodeList;
	}

	private void findEpisodeFromArticle(final CategoryDTO category,
			final Set<EpisodeDTO> episodeList, Elements divTabContainer) {
		for (final Element article : divTabContainer.select("article")) {
			final Element aHref = article.select("a").first();
			final Element h4 = article.select("h4").first();
			final String href = aHref.attr("href");
			String name = h4.text();
			name = SoccerUtils.maskScore(name);
			episodeList.add(new EpisodeDTO(category, name, href));
		}
	}

	private Collection<CategoryDTO> findReplaycategories() {
		final Set<CategoryDTO> categories = new LinkedHashSet<>();

		final org.jsoup.nodes.Document doc = Jsoup
				.parse(getUrlContent(BeinSportConf.REPLAY_URL));

		final Elements divTabContainer = doc.select("#tabContainer");
		for (final Element h2 : divTabContainer.select("h2")) {
			final Element aHref = h2.child(0);
			final String href = aHref.attr("href");
			final String title = aHref.text();
			CategoryDTO category = new CategoryDTO(BeinSportConf.NAME, title,
					href, BeinSportConf.EXTENSION);
			category.setDownloadable(true);
			categories.add(category);
		}
		return categories;
	}

	@Override
	public DownloadableState canDownload(final String downloadInput) {
		return DownloadableState.IMPOSSIBLE;
	}

}
