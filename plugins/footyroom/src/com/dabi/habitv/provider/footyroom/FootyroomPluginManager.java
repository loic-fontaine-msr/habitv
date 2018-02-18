package com.dabi.habitv.provider.footyroom;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.fasterxml.jackson.core.JsonProcessingException;

public class FootyroomPluginManager extends BasePluginWithProxy implements PluginProviderDownloaderInterface {

	public FootyroomPluginManager() {
		HttpURLConnection.setFollowRedirects(true);
	}

	@Override
	public String getName() {
		return FootyroomConf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new LinkedHashSet<>();
		final Document doc = Jsoup.parse(getUrlContent(category.getId()));
		for (final Element aHref : doc.select("div.posts-page header a.not-spoiler")) {
			final String hRef = aHref.attr("href");
			final String name = aHref.text();
			episodeList.add(new EpisodeDTO(category, name, hRef));
		}
		return episodeList;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categoryDTOs = new LinkedHashSet<>();
		final Document doc = Jsoup.parse(getUrlContent(FootyroomConf.VIDEO_HOME_URL));

		for (final Element league : doc.select("ul.all-leagues-section")) {
			Iterator<Element> competitionIt = league.children().iterator();
			if (competitionIt.hasNext()) {
				String leagueName = competitionIt.next().text();
				final CategoryDTO leagueCat = new CategoryDTO(FootyroomConf.NAME, leagueName, leagueName, FootyroomConf.EXTENSION);
				leagueCat.setDownloadable(false);
				categoryDTOs.add(leagueCat);

				while (competitionIt.hasNext()) {
					Element competition = competitionIt.next();
					Element aHref = competition.child(0);
					final String href = aHref.attr("href");
					final String content = aHref.text();
					final CategoryDTO categoryDTO = new CategoryDTO(FootyroomConf.NAME, content, href, FootyroomConf.EXTENSION);
					categoryDTO.setDownloadable(true);
					leagueCat.addSubCategory(categoryDTO);
				}
			}
		}
		return categoryDTOs;
	}

	@Override
	public ProcessHolder download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders) throws DownloadFailedException {
		String videoUrl;
		try {
			videoUrl = findDownloadlink(downloadParam.getDownloadInput());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		downloadParam.addParam(FrameworkConf.DOWNLOADER_PARAM, FrameworkConf.YOUTUBE);
		return DownloadUtils.download(DownloadParamDTO.buildDownloadParam(downloadParam, videoUrl), downloaders);

	}

	private static final Pattern URL_SOURCE = Pattern.compile("\"source\":\"([^\"]+)\"");
	private static final Pattern URL_IFRAME = Pattern.compile("\\<iframe[^\\>]+src=\\\\\"([^\"]+)\\\\\"[^\\>]+\\>");

	private String findDownloadlink(String url) throws JsonProcessingException, IOException {
		final Document doc = Jsoup.parse(getUrlContent(url));
		Elements scriptTags = doc.select("div.video-section script");
		for (Element scriptTag : scriptTags) {
			String data = scriptTag.data();
			if (data.contains("DataStore")) {
				Matcher matcher = URL_IFRAME.matcher(data);
				if (matcher.find()) {
					String feedUrl = matcher.group(1).replace("\\", "");
					return DownloadUtils.isHttpUrl(feedUrl) ? feedUrl : ("http:" + feedUrl);
				} else {
					matcher = URL_SOURCE.matcher(data);
					if (matcher.find()) {
						String feedUrl = matcher.group(1).replace("\\", "");
						return DownloadUtils.isHttpUrl(feedUrl) ? feedUrl : ("http:" + feedUrl);
					}
				}
			}
		}
		throw new RuntimeException("mediaId not found");
	}

	@Override
	public DownloadableState canDownload(final String downloadInput) {
		return downloadInput.contains("footyroom") ? DownloadableState.SPECIFIC : DownloadableState.IMPOSSIBLE;
	}

}
