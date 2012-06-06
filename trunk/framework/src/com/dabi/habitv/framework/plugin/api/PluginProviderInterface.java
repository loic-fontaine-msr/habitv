package com.dabi.habitv.framework.plugin.api;

import java.util.Set;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

/**
 * Define the interface for provider plugins The goals of the provider plugins,
 * is to find categories of a provider and to give the necessary information
 * to download an episode
 * 
 */
public interface PluginProviderInterface extends PluginBase {
//TODO ajouter download (Episode)
	/**
	 * Find episodes related to a category and sub categories
	 * @param category the category (with sub categories)
	 * @return set of unique episodes
	 */
	Set<EpisodeDTO> findEpisode(CategoryDTO category);

	/**
	 * Find all the categories available for the provider
	 * @return the categories (with sub categories)
	 */
	Set<CategoryDTO> findCategory();

	/**
	 * Determinate the param command to execute for download according the episode url
	 * @param url the episode url
	 * @return the param command
	 */
	String downloadCmd(String url);

	/**
	 * Indicate the downloader to use for the specified url
	 * @param url the episode url
	 * @return the downloader name
	 */
	String getDownloader(String url);

}
