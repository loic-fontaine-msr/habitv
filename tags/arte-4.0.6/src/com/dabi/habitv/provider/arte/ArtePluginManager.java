package com.dabi.habitv.provider.arte;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabi.habitv.api.plugin.api.PluginProviderDownloaderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.utils.DownloadUtils;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ArtePluginManager extends BasePluginWithProxy implements
		PluginProviderDownloaderInterface { // NO_UCD

	@Override
	public String getName() {
		return ArteConf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return findEpisodeByCategory(category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return findCategories(RetrieverUtils.getUrlContent(ArteConf.CAT_PAGE,
				FrameworkConf.UTF8, getHttpProxy()));
	}

	@Override
	public ProcessHolder download(final DownloadParamDTO downloadParam,
			final DownloaderPluginHolder downloaders)
			throws DownloadFailedException {
		String downloadLink;
		try {
			downloadLink = buildDownloadLink(downloadParam.getDownloadInput());
		} catch (IOException e) {
			throw new DownloadFailedException(e);
		}
		return DownloadUtils.download(DownloadParamDTO.buildDownloadParam(
				downloadParam, downloadLink), downloaders);
	}

	private Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new LinkedHashSet<EpisodeDTO>();
		final Document doc = Jsoup.parse(getUrlContent(category.getId(),
				FrameworkConf.UTF8));
		final Elements select = doc.select("#content-videos")
				.select("li.video");
		for (final Element liVideo : select) {
			final String href = liVideo.select("div.video-container").attr(
					"arte_vp_url");
			final String title = liVideo.select("p.time-row").first().text();
			String infoUrl = ArteConf.HOME_URL
					+ liVideo.select("a.info").first().attr("href");
			episodeList.add(new EpisodeDTO(category, findRealTitle(infoUrl,
					title), href));
		}
		return episodeList;
	}

	private String findRealTitle(String infoUrl, String title) {
		try {
			final Document doc = Jsoup.parse(getUrlContent(infoUrl,
					FrameworkConf.UTF8));
			return doc.select("h2.text-thin").first().text();
		} catch (Exception e) {
			return title;
		}
	}

	private Set<CategoryDTO> findCategories(final String urlContent) {
		final Set<CategoryDTO> categoryDTOs = new LinkedHashSet<>();
		final Document doc = Jsoup.parse(urlContent);
		final Elements select = doc.select("section.nav-clusters")
				.select(".head").select(".cluster");
		for (final Element divCluster : select) {
			final Element aHref = divCluster.child(0);
			final String href = aHref.attr("href");
			final String title = aHref.child(0).text();
			CategoryDTO category = new CategoryDTO(ArteConf.NAME, title, href,
					FrameworkConf.MP4);
			category.setDownloadable(true);
			categoryDTOs.add(category);
		}
		return categoryDTOs;
	}

	@SuppressWarnings("unchecked")
	private String buildDownloadLink(final String url)
			throws DownloadFailedException, JsonParseException,
			JsonMappingException, IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final Map<String, Object> mainData = mapper.readValue(
				getInputStreamFromUrl(url), Map.class);

		return findBestQualityLink(((Map<String, Object>) ((Map<String, Object>) mainData
				.get("videoJsonPlayer")).get("VSR")));
	}

	private String findBestQualityLink(Map<String, Object> mapLink) {
		String url = findBestQualityInFormat(mapLink, "HBBTV");
		if (url == null) {
			url = findBestQualityInFormat(mapLink, "M3U8");
		}
		if (url == null) {
			url = findBestQualityInFormat(mapLink, "RMP4");
		}
		return url;
	}

	@SuppressWarnings("unchecked")
	private String findBestQualityInFormat(Map<String, Object> mapLink,
			String format) {
		Integer maxBitrate = null;
		String url = null;
		for (Entry<String, Object> linkEntry : mapLink.entrySet()) {
			Map<String, Object> videoDetails = (Map<String, Object>) linkEntry
					.getValue();
			if ("VF".equals(videoDetails.get("versionCode"))
					&& format.equals(videoDetails.get("videoFormat"))) {
				Integer bitrate = (Integer) videoDetails.get("bitrate");
				if (maxBitrate == null || bitrate > maxBitrate) {
					String streamer = (String) videoDetails.get("streamer");
					String videoUrl = (String) videoDetails.get("url");
					url = (streamer == null) ? videoUrl : streamer + videoUrl;
				}
			}
		}
		return url;
	}

	@Override
	public DownloadableState canDownload(final String downloadInput) {
		return downloadInput.startsWith(ArteConf.HOME_URL) ? DownloadableState.SPECIFIC
				: DownloadableState.IMPOSSIBLE;
	}

}
