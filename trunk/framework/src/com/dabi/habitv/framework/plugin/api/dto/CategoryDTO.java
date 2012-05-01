package com.dabi.habitv.framework.plugin.api.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Define the category of the episode
 * The category can define sub categories
 * include and exclude
 *
 */
public class CategoryDTO {

	final private String name;

	final private String identifier;

	private List<CategoryDTO> subCategories;

	private List<String> include;

	private List<String> exclude;

	/**
	 * Full Constructor
	 * @param name label of the category
	 * @param identifier unique id of the category
	 * @param include pattern to define which episode will be included in the category
	 * @param exclude pattern to define which episode will be excluded of the category
	 */
	public CategoryDTO(final String name, final String identifier, final List<String> include, final List<String> exclude) {
		super();
		this.name = name;
		this.identifier = identifier;
		this.include = include;
		this.exclude = exclude;
	}

	/**
	 * Light constructor
	 * @param name label of the category
	 * @param identifier unique id of the category
	 */
	public CategoryDTO(final String name, final String identifier) {
		super();
		this.name = name;
		this.identifier = identifier;
		//FIXME validate method
	}

	/**
	 * @return patterns to define which episode will be included in the category
	 */
	public List<String> getInclude() {
		if (this.include == null) {
			this.include = new ArrayList<>();
		}
		return include;
	}

	/**
	 * @return patterns to define which episode will be excluded of the category
	 */
	public List<String> getExclude() {
		if (this.exclude == null) {
			this.exclude = new ArrayList<>();
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
		if (this.subCategories == null) {
			this.subCategories = new ArrayList<>();
		}
		return subCategories;
	}

	/**
	 * Add sub category to current category
	 * init list if empty
	 * @param subCategory the sub category to add
	 */
	public void addSubCategory(final CategoryDTO subCategory) {
		if (this.subCategories == null) {
			this.subCategories = new ArrayList<>();
		}
		this.subCategories.add(subCategory);
	}

	/**
	 * Add sub categories to current category
	 * init list if empty
	 * @param subCategory the sub categories to add
	 */
	public void addSubCategories(final Collection<CategoryDTO> categoryListDTO) {
		if (this.subCategories == null) {
			this.subCategories = new ArrayList<>();
		}
		this.subCategories.addAll(categoryListDTO);
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
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		boolean ret;
		if (obj instanceof CategoryDTO) {
			final CategoryDTO category = (CategoryDTO) obj;
			ret = this.getName().equals(category.getName());
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
}
