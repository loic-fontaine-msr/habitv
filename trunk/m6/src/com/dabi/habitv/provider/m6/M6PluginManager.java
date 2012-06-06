package com.dabi.habitv.provider.m6;

import java.util.Set;

import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public class M6PluginManager implements PluginProviderInterface {
	
	private ClassLoader classLoader;

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return M6Retriever.findEpisodeByCategory(classLoader, category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return M6CategoriesFinder.findCategory(classLoader); 
	}

	@Override
	public String downloadCmd(final String url) {
		return M6Conf.DUMP_CMD;
	}

	@Override
	public String getDownloader(final String url) {
		return M6Conf.RTMDUMP;
	}

	@Override
	public String getName() {
		return M6Conf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

}
