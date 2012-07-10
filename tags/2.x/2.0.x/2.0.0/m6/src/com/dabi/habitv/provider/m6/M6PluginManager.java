package com.dabi.habitv.provider.m6;

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
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public class M6PluginManager implements PluginProviderInterface {

	private ClassLoader classLoader;

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return M6Retriever.findEpisodeByCategory(classLoader, category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return M6CategoriesFinder.findCategory(classLoader);
	}

	@Override
	public String getName() {
		return M6Conf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public void download(final String downloadOuput, final DownloadersDTO downloaders, final CmdProgressionListener listener, final EpisodeDTO episode)
			throws DownloadFailedException, NoSuchDownloaderException {
		final String downloaderName = M6Conf.RTMDUMP;
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.PARAMETER_ARGS, buildDownloadParam(episode));

		pluginDownloader.download(episode.getVideoUrl(), downloadOuput, parameters, listener);
	}

	private String buildDownloadParam(final EpisodeDTO episode) {
		final String tokenContent = RetrieverUtils.getUrlContent(M6Conf.TOKEN_URL + episode.getVideoUrl());
		final String tokenParam = tokenContent.split("\\?")[1];
		final String tokenParamNoLang = tokenParam.substring(0, tokenParam.indexOf("&lang="));
		final String cmdParam = M6Conf.DUMP_CMD.replace("#TOKEN#", tokenParamNoLang);
		return cmdParam;
	}
}
