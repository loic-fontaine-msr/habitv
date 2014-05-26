package com.dabi.habitv.plugin.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import com.dabi.habitv.api.plugin.api.PluginProviderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public class FilePluginManager extends BasePluginWithProxy implements
		PluginProviderInterface {

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new HashSet<>();
		File file = new File(category.getId());
		if (file.exists()) {
			Set<String> urls = findUrlInFiles(file);
			for (String url : urls) {
				String name;
				if (url.startsWith(FrameworkConf.HTTP_PREFIX)) {
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
			emptyFile(file);
		}
		return episodeList;
	}

	private void emptyFile(File file) {
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			fileOutputStream.write(new String().getBytes());
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
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
		final Set<CategoryDTO> categoryList = new HashSet<>();
		final CategoryDTO categoryDTO = new CategoryDTO(FileConf.NAME,
				"Set a category name : ex : ThingsToDL",
				"Set File Url Here ex : D:/toDL.txt ", null, null,
				"Set files extension Here");
		categoryDTO
				.addParameter(FrameworkConf.DOWNLOADER_PARAM,
						"Set the downloader here  :aria2, youtube, rtmpdump (default aria2)");
		categoryDTO.addParameter(FileConf.DELETE_LOADED_EP, "true");
		categoryList.add(categoryDTO);
		
		addCategoryTemplate(
				categoryList,
				FileConf.NAME,
				"§ID§||Saisissez l'url d'un fichier en local \n où se trouve des urls à télécharger \"C:/temp/file.txt\"");
		
		return categoryList;
	}
	
	private void addCategoryTemplate(final Set<CategoryDTO> categoryList,
			String name, String id) {
		final CategoryDTO categoryDTO = new CategoryDTO(FileConf.NAME, name, id,
				null, null, null);
		categoryDTO.setTemplate(true);
		categoryList.add(categoryDTO);
	}

	@Override
	public String getName() {
		return FileConf.NAME;
	}

}
