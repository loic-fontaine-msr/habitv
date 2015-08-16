package com.dabi.habitv.api.plugin.api;

import java.util.Set;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;

/**
 * Define the interface for provider plugins The goals of the provider plugins,
 * is to find categories of a provider and to give the necessary information to
 * download an episode
 * 
 */
public interface PluginProviderInterface extends PluginBaseInterface {
	/**
	 * Find episodes related to a category and sub categories
	 * 
	 * @param category
	 *            the category (with sub categories)
	 * @return set of unique episodes
	 */
	Set<EpisodeDTO> findEpisode(final CategoryDTO category);

	/**
	 * Find all the categories available for the provider
	 * 
	 * @return the categories (with sub categories)
	 */
	Set<CategoryDTO> findCategory();

}
