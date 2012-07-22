package com.dabi.habitv.core.dao;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.dabi.habitv.core.config.ConfigAccess;
import com.dabi.habitv.core.config.HabitTvConf;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.exception.InvalidCategoryException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.grabconfig.entities.Category;
import com.dabi.habitv.grabconfig.entities.Channel;
import com.dabi.habitv.grabconfig.entities.GrabConfig;

public class GrabConfigDAO {

	private static final Logger LOGGER = Logger.getLogger(GrabConfigDAO.class);

	public void saveGrabConfig(final Map<String, Set<CategoryDTO>> channel2Categories) {
		final GrabConfig config = new GrabConfig();
		for (final Entry<String, Set<CategoryDTO>> entry : channel2Categories.entrySet()) {
			final Channel channel = new Channel();
			channel.setName(entry.getKey());
			for (final CategoryDTO categoryDTO : entry.getValue()) {
				try {
					categoryDTO.check();
				} catch (final InvalidCategoryException e) {
					LOGGER.error("Invalid Category" + categoryDTO, e);
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
		category.setExtension(categoryDTO.getExtension());
		for (final String exclude : categoryDTO.getExclude()) {
			category.getExclude().add(exclude);
		}
		for (final String include : categoryDTO.getInclude()) {
			category.getInclude().add(include);
		}
		for (final CategoryDTO subCategoryDTO : categoryDTO.getSubCategories()) {
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
				} catch (final IOException e) {
					throw new TechnicalException(e);
				}
			}
		}
	}

	private static List<CategoryDTO> buildCategoryListDTO(final String channelName, final List<Category> categories) {
		final List<CategoryDTO> categoryDTOs = new ArrayList<>(categories.size());
		CategoryDTO categoryDTO;
		for (final Category category : categories) {
			categoryDTO = new CategoryDTO(channelName, category.getName(), category.getId(), category.getInclude(), category.getExclude(),
					category.getExtension());
			categoryDTO.addSubCategories(buildCategoryListDTO(channelName, category.getCategory()));
			categoryDTOs.add(categoryDTO);
		}
		return categoryDTOs;
	}

	public Map<String, List<CategoryDTO>> loadGrabConfig(final GrabConfig grabConfig) {
		final Map<String, List<CategoryDTO>> channel2Category = new HashMap<>();
		for (final Channel channel : grabConfig.getChannel()) {
			channel2Category.put(channel.getName(), buildCategoryListDTO(channel.getName(), channel.getCategory()));
		}
		return channel2Category;
	}
}
