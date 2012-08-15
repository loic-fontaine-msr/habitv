package com.dabi.habitv.framework.plugin.api.provider;

import java.util.Set;

import com.dabi.habitv.framework.plugin.api.PluginBase;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

/**
 * Define the interface for provider plugins The goals of the provider plugins,
 * is to find categories of a provider and to give the necessary information to
 * download an episode
 * 
 */
public interface PluginProviderInterface extends PluginBase {
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

	/**
	 * Download episode
	 * 
	 * @param downloadOuput
	 * 
	 * @param cmdProgressionListener
	 *            progression listener
	 * @param downloaders
	 *            downloaders access
	 * 
	 * @param episode
	 *            the episode to download
	 * @throws NoSuchDownloaderException
	 *             downloader needed by provider can't be found
	 */
	void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener, final EpisodeDTO episode)
			throws DownloadFailedException, NoSuchDownloaderException;

}
