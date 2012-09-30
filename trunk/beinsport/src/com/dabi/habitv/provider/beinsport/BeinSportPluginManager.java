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

public class BeinSportPluginManager implements PluginProviderInterface { // NO_UCD (test only)

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
		return BeinSportRetreiver.findEpisodeByCategory(classLoader, category,
				RetrieverUtils
						.getInputStreamFromUrl((BeinSportConf.CATALOG_URL2)));
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		Set<CategoryDTO> categoryDTOs = new HashSet<>();
		categoryDTOs
				.add(new CategoryDTO(BeinSportConf.NAME, BeinSportConf.NAME,
						BeinSportConf.NAME, BeinSportConf.EXTENSION));
		return categoryDTOs;
	}

	@Override
	public void download(final String downloadOuput,
			final DownloaderDTO downloaders,
			final CmdProgressionListener cmdProgressionListener,
			final EpisodeDTO episode) throws DownloadFailedException,
			NoSuchDownloaderException {
		final String downloaderName = BeinSportConf.CURL;
		final PluginDownloaderInterface pluginDownloader = downloaders
				.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH,
				downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.CMD_PROCESSOR,
				downloaders.getCmdProcessor());		

		pluginDownloader.download(episode.getUrl(), downloadOuput, parameters,
				cmdProgressionListener);
	}

}
