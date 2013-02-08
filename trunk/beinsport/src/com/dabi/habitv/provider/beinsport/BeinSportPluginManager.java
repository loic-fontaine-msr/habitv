package com.dabi.habitv.provider.beinsport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public class BeinSportPluginManager implements PluginProviderInterface { // NO_UCD
	// (test
	// only)

	private ClassLoader classLoader;

	@Override
	public String getName() {
		return BeinSportConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		switch (category.getId()) {
		case BeinSportConf.VIDEOS_CATEGORY:
			return BeinSportRetreiver.findEpisodeByCategory(classLoader, category, RetrieverUtils.getInputStreamFromUrl((BeinSportConf.CATALOG_URL2)));
		case BeinSportConf.REPLAY_CATEGORY:
			final Set<EpisodeDTO> episodeDTOs = new HashSet<>();
			for (final CategoryDTO subCategory : BeinSportRetreiver.findReplaycategories()) {
				episodeDTOs.addAll(BeinSportRetreiver.findEpisodeByCategory(subCategory,
						RetrieverUtils.getInputStreamFromUrl(BeinSportConf.HOME_URL +subCategory.getId())));
			}
			return episodeDTOs;
		default:
			return BeinSportRetreiver.findEpisodeByCategory(category, RetrieverUtils.getInputStreamFromUrl(BeinSportConf.HOME_URL + category.getId()));
		}
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categoryDTOs = new HashSet<>();
		categoryDTOs.add(new CategoryDTO(BeinSportConf.VIDEOS_CATEGORY, BeinSportConf.VIDEOS_CATEGORY, BeinSportConf.VIDEOS_CATEGORY, BeinSportConf.EXTENSION));
		final CategoryDTO replayCategory = new CategoryDTO(BeinSportConf.REPLAY_CATEGORY, BeinSportConf.REPLAY_CATEGORY, BeinSportConf.REPLAY_CATEGORY,
				BeinSportConf.EXTENSION);
		replayCategory.addSubCategories(BeinSportRetreiver.findReplaycategories());
		categoryDTOs.add(replayCategory);
		return categoryDTOs;
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener,
			final EpisodeDTO episode) throws DownloadFailedException, NoSuchDownloaderException {
		if (episode.getUrl().endsWith(".mp4")) {
			curlDownload(downloadOuput, downloaders, cmdProgressionListener, episode);
		} else {
			rtmpDumpDownload(downloadOuput, downloaders, cmdProgressionListener, episode);
		}
	}

	private void rtmpDumpDownload(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener listener, final EpisodeDTO episode)
			throws DownloadFailedException, NoSuchDownloaderException {
		final String downloaderName = BeinSportConf.RTMDUMP;
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);
		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));

		final String finalVideoUrl = BeinSportRetreiver.findFinalRtmpUrl(episode.getUrl());
		final String[] tab = finalVideoUrl.split("/");
		final String contextRoot = tab[3];
		final String rtmpdumpCmd = BeinSportConf.RTMPDUMP_CMD2.replace("#PROTOCOL#", tab[0]).replace("#HOST#", tab[2]).replaceAll("#CONTEXT_ROOT#", contextRoot);
		final String relativeUrl = finalVideoUrl.substring(finalVideoUrl.indexOf("/" + contextRoot + "/")+1);

		parameters.put(FrameworkConf.PARAMETER_ARGS, rtmpdumpCmd);
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());
		pluginDownloader.download(relativeUrl, downloadOuput, parameters, listener);
	}

	private void curlDownload(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener,
			final EpisodeDTO episode) throws NoSuchDownloaderException, DownloadFailedException {
		final String downloaderName = BeinSportConf.CURL;
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());

		pluginDownloader.download(episode.getUrl(), downloadOuput, parameters, cmdProgressionListener);
	}

}
