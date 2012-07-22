package com.dabi.habitv.provider.canalplus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.utils.FrameworkConf;

public class CanalPlusPluginManager implements PluginProviderInterface {

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
		final String downloaderName = getDownloader(episode.getVideoUrl());
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));

		pluginDownloader.download(episode.getVideoUrl(), downloadOuput, parameters, listener);
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