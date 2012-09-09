package com.dabi.habitv.provider.m6w9;

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
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public final class M6W9PluginManager implements PluginProviderInterface {

	private ClassLoader classLoader;

	@Override
	public String getName() {
		return M6W9Conf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		Set<EpisodeDTO> episodeList;
		switch (category.getFatherCategory().getId()) {
		case M6Conf.NAME:
			episodeList = Retriever.findEpisodeByCategory(classLoader, category,
					RetrieverUtils.getEncryptedInputStreamFromUrl(M6Conf.CATALOG_URL, M6Conf.ENCRYPTION, M6Conf.SECRET_KEY));
			break;
		case W9Conf.NAME:
			episodeList = Retriever.findEpisodeByCategory(classLoader, category, RetrieverUtils.getInputStreamFromUrl((W9Conf.CATALOG_URL)));
			break;
		default:
			throw new TechnicalException("category root id must be m6 or w9");
		}
		return episodeList;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categoryDTOs = new HashSet<>();
		// M6
		final CategoryDTO categoryM6 = new CategoryDTO(M6Conf.NAME, M6Conf.NAME, M6Conf.NAME, M6W9Conf.EXTENSION);
		categoryM6.addSubCategories(CategoriesFinder.findCategory(classLoader,
				RetrieverUtils.getEncryptedInputStreamFromUrl(M6Conf.CATALOG_URL, M6Conf.ENCRYPTION, M6Conf.SECRET_KEY), M6Conf.NAME));
		categoryDTOs.add(categoryM6);
		// W9
		final CategoryDTO categoryW9 = new CategoryDTO(W9Conf.NAME, W9Conf.NAME, W9Conf.NAME, M6W9Conf.EXTENSION);
		categoryW9.addSubCategories(CategoriesFinder.findCategory(classLoader, RetrieverUtils.getInputStreamFromUrl(W9Conf.CATALOG_URL), W9Conf.NAME));
		categoryDTOs.add(categoryW9);
		return categoryDTOs;
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener listener, final EpisodeDTO episode)
			throws DownloadFailedException, NoSuchDownloaderException {

		final String dumpCmd;
		if (W9Conf.NAME.equals(episode.getCategory().getChannel())) {
			dumpCmd = W9Conf.DUMP_CMD;
		} else {
			dumpCmd = M6Conf.DUMP_CMD;
		}

		final String downloaderName = M6W9Conf.RTMDUMP;
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.PARAMETER_ARGS, buildDownloadParam(episode, dumpCmd));

		pluginDownloader.download(episode.getUrl(), downloadOuput, parameters, listener);
	}

	private String buildDownloadParam(final EpisodeDTO episode, final String dumpCmd) {
		final String tokenContent = RetrieverUtils.getUrlContent(M6Conf.TOKEN_URL + episode.getUrl());
		final String tokenParam = tokenContent.split("\\?")[1];
		final String tokenParamNoLang = tokenParam.substring(0, tokenParam.indexOf("&lang="));
		return dumpCmd.replace("#TOKEN#", tokenParamNoLang);
	}

}
