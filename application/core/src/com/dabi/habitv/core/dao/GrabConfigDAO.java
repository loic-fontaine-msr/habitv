package com.dabi.habitv.core.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.core.config.HabitTvConf;
import com.dabi.habitv.grabconfig.entities.Category;
import com.dabi.habitv.grabconfig.entities.CategoryType;
import com.dabi.habitv.grabconfig.entities.CategoryType.Configuration;
import com.dabi.habitv.grabconfig.entities.CategoryType.Excludes;
import com.dabi.habitv.grabconfig.entities.CategoryType.Includes;
import com.dabi.habitv.grabconfig.entities.CategoryType.Subcategories;
import com.dabi.habitv.grabconfig.entities.Channel;
import com.dabi.habitv.grabconfig.entities.GrabConfig;
import com.dabi.habitv.grabconfig.entities.GrabConfig.Plugins;
import com.dabi.habitv.grabconfig.entities.Parameter;
import com.dabi.habitv.grabconfig.entities.Plugin;
import com.dabi.habitv.grabconfig.entities.Plugin.Categories;
import com.dabi.habitv.utils.FileUtils;
import com.dabi.habitv.utils.XMLUtils;

public class GrabConfigDAO {

	private final String grabConfigFile;

	public GrabConfigDAO(final String grabConfigFile) {
		super();
		this.grabConfigFile = grabConfigFile;
	}

	public void saveGrabConfig(
			final Map<String, Set<CategoryDTO>> channel2Categories) {
		final GrabConfig config = new GrabConfig();
		addPlugins(channel2Categories, config);
		marshal(config);
	}

	private void addPlugins(
			final Map<String, Set<CategoryDTO>> channel2Categories,
			final GrabConfig config) {
		if (config.getPlugins() == null) {
			config.setPlugins(new Plugins());
		}
		for (final Entry<String, Set<CategoryDTO>> entry : channel2Categories
				.entrySet()) {
			final Plugin channel = new Plugin();
			channel.setName(entry.getKey());
			channel.setStatus(StatusEnum.EXIST.ordinal()); //FIXME new !
			Categories categories = new Categories();
			channel.setCategories(categories);
			for (final CategoryDTO categoryDTO : entry.getValue()) {
				if (categoryDTO.check()) {
					categories.getCategory().add(buildCategory(categoryDTO));
				}
			}
			config.getPlugins().getPlugin().add(channel);
		}
	}

	private CategoryType buildCategory(final CategoryDTO categoryDTO) {
		final CategoryType category = new CategoryType();
		category.setId(categoryDTO.getId());
		category.setName(categoryDTO.getName());
		category.setExtension(categoryDTO.getExtension());
		category.setDownload(categoryDTO.isSelected());
		category.setStatus(StatusEnum.NEW.name());
		if (categoryDTO.isTemplate()) {
			category.setTemplate(categoryDTO.isTemplate());
		}

		if (!categoryDTO.getExclude().isEmpty()) {
			Excludes excludes = new Excludes();
			category.setExcludes(excludes);
			for (final String exclude : categoryDTO.getExclude()) {
				excludes.getExclude().add(exclude);
			}
		}

		if (!categoryDTO.getInclude().isEmpty()) {
			Includes includes = new Includes();
			for (final String include : categoryDTO.getInclude()) {
				includes.getInclude().add(include);
			}
			category.setIncludes(includes);
		}

		Subcategories subCategories = new Subcategories();
		category.setSubcategories(subCategories);
		for (final CategoryDTO subCategoryDTO : categoryDTO.getSubCategories()) {
			subCategories.getCategory().add(buildCategory(subCategoryDTO));
		}
		Configuration configuration = new Configuration();
		category.setConfiguration(configuration);
		for (final Entry<String, String> entry : categoryDTO.getParameters()
				.entrySet()) {
			configuration.getAny().add(
					XMLUtils.buildAnyElement(entry.getKey(), entry.getValue()));
		}
		return category;
	}

	public void marshal(final GrabConfig config) {
		final JAXBContext jaxbContext;
		FileOutputStream outputFile = null;
		try {
			jaxbContext = JAXBContext.newInstance(GrabConfig.class.getPackage()
					.getName());
			final Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING,
					HabitTvConf.ENCODING);
			outputFile = new FileOutputStream(grabConfigFile);
			marshaller.marshal(config, outputFile);
		} catch (JAXBException | FileNotFoundException e) {
			throw new TechnicalException(e);
		} finally {
			if (outputFile != null) {
				try {
					outputFile.close();
				} catch (final IOException e) {
					throw new TechnicalException(e);
				}
			}
		}
	}

	private static Set<CategoryDTO> buildCategoryListDTO(
			final LoadModeEnum loadMode, final String channelName,
			final List<CategoryType> categories) {
		final Set<CategoryDTO> categoryDTOs = new HashSet<>(categories.size());
		CategoryDTO categoryDTO;
		for (final CategoryType category : categories) {
			final Set<CategoryDTO> subCategoriesDTO;
			if (category.getSubcategories() != null) {
				subCategoriesDTO = buildCategoryListDTO(loadMode, channelName,
						category.getSubcategories().getCategory());
			} else {
				subCategoriesDTO = Collections.emptySet();
			}

			if (category.getDownload() == null || category.getDownload()
					|| !subCategoriesDTO.isEmpty()
					|| loadMode.equals(LoadModeEnum.ALL)) {
				categoryDTO = new CategoryDTO(channelName, category.getName(),
						category.getId(), getInclude(category),
						getExclude(category), category.getExtension());
				categoryDTO.setSelected(category.getDownload() != null
						&& category.getDownload());
				categoryDTO.setTemplate(category.getTemplate() != null
						&& category.getTemplate());
				categoryDTO.addSubCategories(subCategoriesDTO);
				if (category.getConfiguration() != null
						&& !category.getConfiguration().getAny().isEmpty()) {
					for (final Object parameter : category.getConfiguration()
							.getAny()) {
						categoryDTO.addParameter(
								XMLUtils.getTagName(parameter),
								XMLUtils.getTagValue(parameter));
					}
				}
				categoryDTOs.add(categoryDTO);
			}
		}
		return categoryDTOs;
	}

	private static List<String> getExclude(final CategoryType category) {
		if (category.getExcludes() == null) {
			return Collections.emptyList();
		} else {
			return category.getExcludes().getExclude();
		}
	}

	private static List<String> getInclude(final CategoryType category) {
		if (category.getIncludes() == null) {
			return Collections.emptyList();
		} else {
			return category.getIncludes().getInclude();
		}
	}

	public GrabConfig unmarshal() {
		GrabConfig grabConfig = null;
		try {
			final JAXBContext jaxbContext = JAXBContext
					.newInstance(GrabConfig.class.getPackage().getName());
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			FileUtils.setValidation(unmarshaller, HabitTvConf.GRAB_CONF_XSD);
			grabConfig = ((GrabConfig) unmarshaller
					.unmarshal(new InputStreamReader(new FileInputStream(
							grabConfigFile), HabitTvConf.ENCODING)));
			if (!grabConfig.getChannel().isEmpty()) {
				grabConfig = convertOldGrabconfig(grabConfig);
			}
		} catch (final JAXBException e) {
			throw new TechnicalException(e);
		} catch (final UnsupportedEncodingException e) {
			throw new TechnicalException(e);
		} catch (final FileNotFoundException e) {
			throw new TechnicalException(e);
		}
		return grabConfig;
	}

	private GrabConfig convertOldGrabconfig(GrabConfig grabConfig) {
		Plugins Plugins = new Plugins();
		grabConfig.setPlugins(Plugins);
		Iterator<Channel> it = grabConfig.getChannel().iterator();
		while (it.hasNext()) {
			Channel oldPlugin = it.next();
			Plugins.getPlugin().add(buildPlugin(oldPlugin));
			it.remove();
		}
		marshal(grabConfig);
		return grabConfig;
	}

	private Plugin buildPlugin(Channel oldPlugin) {
		Plugin channelType = new Plugin();
		channelType.setName(oldPlugin.getName());
		channelType.setStatus(channelType.getStatus());
		Categories categories = new Categories();
		channelType.setCategories(categories);
		for (Category oldCategory : oldPlugin.getCategory()) {
			categories.getCategory().add(buildCategoryType(oldCategory));
		}
		return channelType;
	}

	private CategoryType buildCategoryType(Category oldCategory) {
		CategoryType categoryType = new CategoryType();

		buildCategoryConfiguration(oldCategory, categoryType);

		categoryType.setDownload(oldCategory.getToDownload());

		Excludes excludes = new Excludes();
		for (String oldExclude : oldCategory.getExclude()) {
			excludes.getExclude().add(oldExclude);
		}
		categoryType.setExcludes(excludes);

		categoryType.setExtension(oldCategory.getExtension());
		categoryType.setId(oldCategory.getId());
		Includes includes = new Includes();
		for (String oldInclude : oldCategory.getInclude()) {
			includes.getInclude().add(oldInclude);
		}
		categoryType.setIncludes(includes);

		categoryType.setName(oldCategory.getName());
		categoryType.setStatus(oldCategory.getStatus());
		Subcategories subCategories = new Subcategories();
		for (Category oldSuCategory : oldCategory.getCategory()) {
			subCategories.getCategory().add(buildCategoryType(oldSuCategory));
		}
		categoryType.setSubcategories(subCategories);

		return categoryType;
	}

	private void buildCategoryConfiguration(Category oldCategory,
			CategoryType categoryType) {
		Configuration configuration = new Configuration();
		for (Parameter oldParameter : oldCategory.getParameter()) {
			configuration.getAny().add(
					XMLUtils.buildAnyElement(oldParameter.getKey(),
							oldParameter.getValue()));
		}
		categoryType.setConfiguration(configuration);
	}

	private Map<String, Set<CategoryDTO>> buildCategoryDTO(
			final GrabConfig grabConfig, final LoadModeEnum loadMode) {
		final Map<String, Set<CategoryDTO>> channel2Category = new HashMap<>();
		if (grabConfig.getPlugins() != null) {
			for (final Plugin plugin : grabConfig.getPlugins().getPlugin()) {
				final Set<CategoryDTO> buildCategoryListDTO;
				if (plugin.getCategories() == null) {
					buildCategoryListDTO = new HashSet<>();
				} else {
					buildCategoryListDTO = buildCategoryListDTO(loadMode,
							plugin.getName(), plugin.getCategories()
									.getCategory());
				}
				if (!buildCategoryListDTO.isEmpty()) {
					channel2Category.put(plugin.getName(),
							buildCategoryListDTO);
				}
			}
		}
		return channel2Category;
	}

	public enum LoadModeEnum {
		ALL, TO_DOWNLOAD_ONLY;
	}

	public Map<String, Set<CategoryDTO>> load(final LoadModeEnum loadMode) {
		return buildCategoryDTO(unmarshal(), loadMode);
	}

	public Map<String, Set<CategoryDTO>> load() {
		return buildCategoryDTO(unmarshal(), LoadModeEnum.TO_DOWNLOAD_ONLY);
	}

	public boolean exist() {
		return (new File(grabConfigFile)).exists();
	}

	public void updateGrabConfig(
			final Map<String, Set<CategoryDTO>> channel2Categories) {
		final HashMap<String, Set<CategoryDTO>> channel2CategoriesTemp = new HashMap<>(
				channel2Categories);
		final GrabConfig grabConfig = unmarshal();
		if (grabConfig.getPlugins() != null) {
			StatusEnum Pluginstatus;
			for (final Plugin channel : grabConfig.getPlugins().getPlugin()) {
				final Set<CategoryDTO> categoryPlugin = channel2CategoriesTemp
						.get(channel.getName());
				if (channel.getCategories() == null) {
					channel.setCategories(new Categories());
				}
				if (categoryPlugin != null) {
					updateCategory(channel.getCategories().getCategory(),
							categoryPlugin);
					channel2CategoriesTemp.remove(channel.getName());
					Pluginstatus = StatusEnum.EXIST;
				} else {
					Pluginstatus = StatusEnum.DELETED;
				}
				channel.setStatus(Pluginstatus.ordinal());
			}
		}
		addPlugins(channel2CategoriesTemp, grabConfig);
		marshal(grabConfig);
	}

	private void updateCategory(final List<CategoryType> categoryList,
			final Collection<CategoryDTO> categoryDTOList) {
		final Map<String, CategoryDTO> catNameToCat = new HashMap<>();
		for (final CategoryDTO categoryDTO : categoryDTOList) {
			catNameToCat.put(categoryDTO.getName(), categoryDTO);
		}
		StatusEnum statusEnum;
		for (final CategoryType category : categoryList) {
			final CategoryDTO associatedCatDTO = catNameToCat.get(category
					.getName());
			if (associatedCatDTO != null) {
				catNameToCat.remove(category.getName());
				if (category.getSubcategories() != null
						&& !category.getSubcategories().getCategory().isEmpty()) {
					updateCategory(category.getSubcategories().getCategory(),
							associatedCatDTO.getSubCategories());
				}
				statusEnum = StatusEnum.EXIST;
			} else {
				statusEnum = StatusEnum.DELETED;
			}
			category.setStatus(statusEnum.name());
		}
		for (final CategoryDTO categoryDTO : catNameToCat.values()) {
			categoryList.add(buildCategory(categoryDTO));
		}
	}

	public void clean() {
		GrabConfig grabconfig = unmarshal();
		Iterator<Plugin> it = grabconfig.getPlugins().getPlugin().iterator();
		while (it.hasNext()) {
			Plugin channel = it.next();
			if (StatusEnum.DELETED.name().equals(channel.getStatus())) {
				it.remove();
			} else {
				cleanCategories(channel.getCategories().getCategory());
			}
		}
		marshal(grabconfig);
	}

	private void cleanCategories(List<CategoryType> categories) {
		Iterator<CategoryType> it = categories.iterator();
		while (it.hasNext()) {
			CategoryType category = it.next();
			if (StatusEnum.DELETED.name().equals(category.getStatus())) {
				it.remove();
			} else {
				cleanCategories(category.getSubcategories().getCategory());
			}
		}
	}
}
