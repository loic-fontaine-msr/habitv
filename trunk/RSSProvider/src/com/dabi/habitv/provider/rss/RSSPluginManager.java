package com.dabi.habitv.provider.rss;

import java.util.Set;

import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public class RSSPluginManager implements PluginProviderInterface {
	
	private ClassLoader classLoader;

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return RSSRetriever.findEpisodeByCategory(classLoader, category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return RSSCategoriesFinder.findCategory(classLoader); 
	}

	@Override
	public String downloadCmd(final String url) {
		return null;
	}

	@Override
	public String getDownloader(final String url) {
		return RSSConf.DOWNLOADER;
	}

	@Override
	public String getName() {
		return RSSConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

}
