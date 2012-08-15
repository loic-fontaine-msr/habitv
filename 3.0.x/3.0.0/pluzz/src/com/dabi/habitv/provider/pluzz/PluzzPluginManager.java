package com.dabi.habitv.provider.pluzz;

import java.util.Set;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.provider.pluzz.jpluzz.JPluzzDL;

public class PluzzPluginManager implements PluginProviderInterface {

	@Override
	public String getName() {
		return PluzzConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {

	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return PluzzRetreiver.findEpisodeByCategory(category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return PluzzRetreiver.findCategory();
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener,
			final EpisodeDTO episode) throws DownloadFailedException, NoSuchDownloaderException {
		try {
			new JPluzzDL(episode.getUrl(), downloadOuput, false, null, cmdProgressionListener);
		} catch (final Exception e) {
			throw new TechnicalException(e);
		}
	}

}
