package com.dabi.habitv.provider.lequipe;

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

public class LEquipePluginManager implements PluginProviderInterface {

	@Override
	public String getName() {
		return LEquipeConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {

	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return LEquipeRetreiver.findEpisodeByCategory(category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categories = new HashSet<>();
		categories.add(new CategoryDTO(LEquipeConf.NAME, "Rencontre", "Rencontre", LEquipeConf.EXTENSION));
		categories.add(new CategoryDTO(LEquipeConf.NAME, "Résumé", "Résumé", LEquipeConf.EXTENSION));
		categories.add(new CategoryDTO(LEquipeConf.NAME, "Avant-Match", "Avant-Match", LEquipeConf.EXTENSION));
		return categories;
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener,
			final EpisodeDTO episode) throws DownloadFailedException, NoSuchDownloaderException {
		final String downloaderName = LEquipeConf.CURL;
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));

		pluginDownloader.download(LEquipeRetreiver.findDownloadlink(episode.getUrl()), downloadOuput, parameters, cmdProgressionListener);
	}

}
