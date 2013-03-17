package com.dabi.habitv.provider.canalplus;

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

public class CanalPlusPluginManager implements PluginProviderInterface { // NO_UCD
	// (unused
	// code)

	private ClassLoader classLoader;

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return new CanalPlusRetriever(classLoader).findEpisodeByCategory(category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return new CanalPlusCategoriesFinder(classLoader).findCategory();
	}

	private String getDownloader(final String url) {
		String downloaderName;
		if (url.startsWith(CanalPlusConf.RTMPDUMP_PREFIX)) {
			downloaderName = CanalPlusConf.RTMDUMP;
		} else {
			downloaderName = CanalPlusConf.CURL;
		}
		return downloaderName;
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener listener, final EpisodeDTO episode)
			throws DownloadFailedException, NoSuchDownloaderException {
		final String downloaderName = getDownloader(episode.getUrl());
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.CMD_PROCESSOR,
				downloaders.getCmdProcessor());

		pluginDownloader.download(episode.getUrl(), downloadOuput, parameters, listener, downloaders.getProtocol2proxy());
	}

	@Override
	public String getName() {
		return CanalPlusConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

}
