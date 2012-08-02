package com.dabi.habitv.provider.tf1;

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

public class TF1PluginManager implements PluginProviderInterface {

	@Override
	public String getName() {
		return TF1Conf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {

	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return TF1Retreiver.findEpisode(category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return TF1Retreiver.findCategory();
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener,
			final EpisodeDTO episode) throws DownloadFailedException, NoSuchDownloaderException {
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(TF1Conf.CURL);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(TF1Conf.CURL));

		pluginDownloader.download(TF1Retreiver.findFinalUrl(episode), downloadOuput, parameters, cmdProgressionListener);
	}

}
