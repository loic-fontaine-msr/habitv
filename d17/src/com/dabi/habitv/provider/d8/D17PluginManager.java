package com.dabi.habitv.provider.d8;

import java.util.HashMap;
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

public class D17PluginManager implements PluginProviderInterface { // NO_UCD
	// (test
	// only)

	private ClassLoader classLoader;

	@Override
	public String getName() {
		return D17Conf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return D17Retreiver.findEpisodeByCategory(classLoader, category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return D17Retreiver.findCategories(classLoader);
	}

	private String getDownloader(final String url) {
		String downloaderName;
		if (url.startsWith(D17Conf.RTMPDUMP_PREFIX)) {
			downloaderName = D17Conf.RTMDUMP;
		} else {
			downloaderName = D17Conf.CURL;
		}
		return downloaderName;
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener listener, final EpisodeDTO episode)
			throws DownloadFailedException, NoSuchDownloaderException {
		final String videoUrl = D17Retreiver.findVideoUrl(episode.getUrl());
		final String downloaderName = getDownloader(videoUrl);
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());

		pluginDownloader.download(videoUrl, downloadOuput, parameters, listener, downloaders.getProtocol2proxy());
	}

}
