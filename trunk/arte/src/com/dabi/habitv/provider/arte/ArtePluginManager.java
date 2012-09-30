package com.dabi.habitv.provider.arte;

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
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public class ArtePluginManager implements PluginProviderInterface { // NO_UCD

	@Override
	public String getName() {
		return ArteConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {

	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return ArteRetreiver.findEpisodeByCategory(category,
				RetrieverUtils.getInputStreamFromUrl(ArteConf.RSS_CATEGORY_URL.replace(ArteConf.ID_EMISSION_TOKEN, category.getId())));
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return ArteRetreiver.findCategories(RetrieverUtils.getUrlContent(ArteConf.RSS_PAGE, "UTF-8"));
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener,
			final EpisodeDTO episode) throws DownloadFailedException, NoSuchDownloaderException {
		final String downloaderName = ArteConf.RTMPDUMP;
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());

		pluginDownloader.download(ArteRetreiver.buildDownloadLink(episode.getUrl()), downloadOuput, parameters, cmdProgressionListener);
	}

}
