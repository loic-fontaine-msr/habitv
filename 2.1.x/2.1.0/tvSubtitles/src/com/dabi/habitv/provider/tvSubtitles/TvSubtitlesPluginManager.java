package com.dabi.habitv.provider.tvSubtitles;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloadersDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.utils.FrameworkConf;

public class TvSubtitlesPluginManager implements PluginProviderInterface {

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return TvSubtitlesRetriever.findEpisodeByCategory(category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return TvSubtitlesCategoriesFinder.findCategory();
	}

	@Override
	public String getName() {
		return TvSubtitlesConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {

	}

	@Override
	public void download(final String downloadOuput, final DownloadersDTO downloaders, final CmdProgressionListener listener, final EpisodeDTO episode)
			throws DownloadFailedException, NoSuchDownloaderException {
		final String downloaderName = TvSubtitlesConf.DOWNLOADER;
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));

		pluginDownloader.download(episode.getVideoUrl(), downloadOuput, parameters, listener);
	}

}
