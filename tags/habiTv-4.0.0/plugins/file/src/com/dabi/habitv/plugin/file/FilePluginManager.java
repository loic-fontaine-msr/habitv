package com.dabi.habitv.plugin.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.dabi.habitv.api.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.api.plugin.api.PluginProviderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.dto.StatusEnum;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.utils.DownloadUtils;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public class FilePluginManager extends BasePluginWithProxy implements
		PluginProviderInterface, PluginDownloaderInterface {

	private static final Logger LOG = Logger.getLogger(FilePluginManager.class);

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new LinkedHashSet<>();
		File file = new File(category.getId());
		if (file.exists()) {
			Set<String> urls = findUrlInFiles(file);
			for (String url : urls) {
				String name;
				if (DownloadUtils.isHttpUrl(url)) {
					name = RetrieverUtils.getTitleByUrl(url);
				} else {
					name = url;
				}
				episodeList.add(new EpisodeDTO(category, name, url));
			}
			if (category.getParameter(FileConf.DELETE_LOADED_EP) == null
					|| Boolean.parseBoolean(category
							.getParameter(FileConf.DELETE_LOADED_EP))) {
				file.renameTo(new File(category.getId() + ".done"));
			}
		} else {
			LOG.error("Le fichier " + file.getAbsolutePath() + " n'existe pas.");
		}
		return episodeList;
	}

	public Set<String> findUrlInFiles(final File file) {

		String ligne;
		final Set<String> urlList = new HashSet<>();
		try (BufferedReader lecteurAvecBuffer = new BufferedReader(
				new InputStreamReader(new FileInputStream(file),
						FrameworkConf.UTF8))) {
			while ((ligne = lecteurAvecBuffer.readLine()) != null) {
				urlList.add(ligne);
			}
		} catch (final FileNotFoundException exc) {
			// will return null
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
		return urlList;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categoryList = new LinkedHashSet<>();
		addCategoryTemplate(
				categoryList,
				FileConf.NAME,
				"§ID§!!Saisissez l'url d'un fichier en local \n où se trouve des urls à télécharger \"C:/temp/file.txt\"");

		return categoryList;
	}

	private void addCategoryTemplate(final Set<CategoryDTO> categoryList,
			String name, String id) {
		final CategoryDTO categoryDTO = new CategoryDTO(FileConf.NAME, name,
				id, null, null, null);
		categoryDTO.setTemplate(true);
		categoryDTO.setState(StatusEnum.USER);
		categoryDTO.setDownloadable(false);
		categoryList.add(categoryDTO);
	}

	@Override
	public String getName() {
		return FileConf.NAME;
	}

	@Override
	public DownloadableState canDownload(String downloadInput) {
		return DownloadableState.IMPOSSIBLE;
	}

	@Override
	public ProcessHolder download(DownloadParamDTO downloadParam,
			DownloaderPluginHolder downloaders) throws DownloadFailedException {
		return DownloadUtils.download(downloadParam, downloaders);
	}

}
