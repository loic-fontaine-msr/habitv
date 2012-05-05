package com.dabi.habitv.provider.soirfoot;

import java.util.Set;

import com.dabi.habitv.framework.plugin.api.ProviderPluginInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public class SoirFootPluginManager implements ProviderPluginInterface {
	
	private ClassLoader classLoader;

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return SoirFootRetriever.findEpisodeByCategory(classLoader, category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return SoirFootCategoriesFinder.findCategory(classLoader); 
	}

	@Override
	public String downloadCmd(final String url) {
		return "";
	}

	@Override
	public String getDownloader(final String url) {
		return SoirFootConf.HTTP;
	}

	@Override
	public String getName() {
		return SoirFootConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

}
