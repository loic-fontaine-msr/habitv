package com.dabi.habitv.api.plugin.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Define the category of the episode The category can define sub categories
 * include and exclude
 * 
 */
public class CategoryDTO implements Comparable<CategoryDTO>, Serializable {

	private static final long serialVersionUID = -7926371729459853575L;

	private final String channel;

	private final String name;

	private final String identifier;

	private CategoryDTO fatherCategory;

	private List<CategoryDTO> subCategories;

	private List<String> include;

	private List<String> exclude;

	private final String extension;

	private final Map<String, String> parameters = new HashMap<>();

	/**
	 * Full Constructor
	 * 
	 * @param channel
	 *            channel of the category
	 * @param name
	 *            label of the category
	 * @param identifier
	 *            unique id of the category
	 * @param include
	 *            pattern to define which episode will be included in the
	 *            category
	 * @param exclude
	 *            pattern to define which episode will be excluded of the
	 *            category
	 * @param extension
	 *            the extension of the files in this category
	 */
	public CategoryDTO(final String channel, final String name,
			final String identifier, final List<String> include,
			final List<String> exclude, final String extension) {
		super();
		this.channel = channel;
		this.name = name;
		this.identifier = identifier;
		this.include = include;
		this.exclude = exclude;
		this.extension = extension;
	}

	/**
	 * Light constructor
	 * 
	 * @param channel
	 *            channel of the category
	 * @param name
	 *            label of the category
	 * @param identifier
	 *            unique id of the category
	 * @param extension
	 *            the extension of the files in this category
	 */
	public CategoryDTO(final String channel, final String name,
			final String identifier, final String extension) {
		super();
		this.channel = channel;
		this.name = name;
		this.identifier = identifier;
		this.extension = extension;
	}

	/**
	 * @return patterns to define which episode will be included in the category
	 */
	public List<String> getInclude() {
		if (include == null) {
			include = new ArrayList<>();
		}
		return include;
	}

	/**
	 * @return patterns to define which episode will be excluded of the category
	 */
	public List<String> getExclude() {
		if (exclude == null) {
			exclude = new ArrayList<>();
		}
		return exclude;
	}

	/**
	 * @return Name of the category, must be unique
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return identifier of the category
	 */
	public String getId() {
		return identifier;
	}

	/**
	 * @return the sub categories, init list if empty
	 */
	public List<CategoryDTO> getSubCategories() {
		if (subCategories == null) {
			subCategories = new ArrayList<>();
		}
		return subCategories;
	}

	/**
	 * Add sub category to current category init list if empty
	 * 
	 * @param subCategory
	 *            the sub category to add
	 */
	public void addSubCategory(final CategoryDTO subCategory) {
		if (subCategories == null) {
			subCategories = new ArrayList<>();
		}
		subCategory.setFatherCategory(this);
		subCategories.add(subCategory);
	}

	/**
	 * Add sub categories to current category init list if empty
	 * 
	 * @param subCategory
	 *            the sub categories to add
	 */
	public void addSubCategories(final Collection<CategoryDTO> categoryListDTO) {
		if (subCategories == null) {
			subCategories = new ArrayList<>();
		}
		for (final CategoryDTO subCategory : categoryListDTO) {
			subCategory.setFatherCategory(this);
		}
		subCategories.addAll(categoryListDTO);
	}

	/**
	 * Extension of the files in the category
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * @return channel of the category
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	/**
	 * Define equality by the name
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		boolean ret;
		if (obj instanceof CategoryDTO) {
			final CategoryDTO category = (CategoryDTO) obj;
			ret = getName().equals(category.getName());
		} else {
			ret = false;
		}
		return ret;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Check the category
	 * @return
	 * 
	 */
	public boolean check() {
		return checkMinSize(identifier) && checkMinSize(name);
	}

	private static boolean checkMinSize(final String category) {
		return category != null && category.length() > 0;
	}

	/**
	 * Compare with another category
	 * 
	 * @param other
	 *            category
	 * @return int
	 */
	@Override
	public int compareTo(final CategoryDTO o) {
		int ret = getChannel().compareTo(o.getChannel());
		if (ret != 0) {
			ret = getId().compareTo(o.getId());
		}
		return ret;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public CategoryDTO getFatherCategory() {
		return fatherCategory;
	}

	private void setFatherCategory(final CategoryDTO fatherCategory) {
		this.fatherCategory = fatherCategory;
	}

	public void addParameter(final String key, final String value) {
		this.parameters.put(key, value);
	}

	public String getParameter(final String downloaderParam) {
		return this.parameters.get(downloaderParam);
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}
}
