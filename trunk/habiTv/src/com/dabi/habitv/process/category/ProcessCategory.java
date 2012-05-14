package com.dabi.habitv.process.category;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.dabi.habitv.config.ConfigAccess;
import com.dabi.habitv.config.HabitTvConf;
import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.framework.plugin.api.ProviderPluginInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.exception.InvalidCategoryException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.grabconfig.entities.Category;
import com.dabi.habitv.grabconfig.entities.Channel;
import com.dabi.habitv.grabconfig.entities.GrabConfig;
import com.dabi.habitv.plugin.PluginFactory;

public class ProcessCategory {
	
	//TODO utiliser TaskMgr

	private static final Logger LOGGER = Logger.getLogger(ProcessCategory.class);

	public void execute(final Config config, final ProcessCategoryListener listener) {
		final PluginFactory<ProviderPluginInterface> pluginProviderFactory = new PluginFactory<>(ProviderPluginInterface.class, config.getProviderPluginDir());
		final Map<String, Set<CategoryDTO>> channel2Categories = new HashMap<>();
		for (ProviderPluginInterface retriever : pluginProviderFactory.getAllPlugin()) {
			listener.getProviderCategories(retriever.getName());
			channel2Categories.put(retriever.getName(), retriever.findCategory());
		}
		saveConfig(channel2Categories);
		listener.categoriesSaved(HabitTvConf.GRABCONFIG_XML_FILE);
	}

	private void saveConfig(final Map<String, Set<CategoryDTO>> channel2Categories) {
		final GrabConfig config = new GrabConfig();
		for (Entry<String, Set<CategoryDTO>> entry : channel2Categories.entrySet()) {
			final Channel channel = new Channel();
			channel.setName(entry.getKey());
			for (CategoryDTO categoryDTO : entry.getValue()) {
				try {
					categoryDTO.check();
				} catch (InvalidCategoryException e) {
					LOGGER.error("Invalid Category", e);
				}
				channel.getCategory().add(buildCategory(categoryDTO));
			}
			config.getChannel().add(channel);
		}
		marshal(config);
	}

	private Category buildCategory(final CategoryDTO categoryDTO) {
		final Category category = new Category();
		category.setId(categoryDTO.getId());
		category.setName(categoryDTO.getName());
		for (String exclude : categoryDTO.getExclude()) {
			category.getExclude().add(exclude);
		}
		for (String include : categoryDTO.getInclude()) {
			category.getInclude().add(include);
		}
		for (CategoryDTO subCategoryDTO : categoryDTO.getSubCategories()) {
			category.getCategory().add(buildCategory(subCategoryDTO));
		}
		return category;
	}

	private static void marshal(final GrabConfig config) {
		final JAXBContext jaxbContext;
		FileOutputStream inputFile = null;
		try {
			jaxbContext = JAXBContext.newInstance(ConfigAccess.GRAB_CONF_PACKAGE_NAME);
			final Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, HabitTvConf.ENCODING);
			inputFile = new FileOutputStream(HabitTvConf.GRABCONFIG_XML_FILE);
			marshaller.marshal(config, inputFile);
		} catch (JAXBException | FileNotFoundException e) {
			throw new TechnicalException(e);
		} finally {
			if (inputFile != null) {
				try {
					inputFile.close();
				} catch (IOException e) {
					throw new TechnicalException(e);
				}
			}
		}
	}
}
