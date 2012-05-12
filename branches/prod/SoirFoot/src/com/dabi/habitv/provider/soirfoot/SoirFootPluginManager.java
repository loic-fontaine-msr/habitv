package com.dabi.habitv.provider.soirfoot;

import java.util.Set;

import com.dabi.habitv.framework.plugin.api.ProviderPluginInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public class SoirFootPluginManager implements ProviderPluginInterface {

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return SoirFootRetriever.findEpisodeByCategory(category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return SoirFootCategoriesFinder.findCategory(); 
	}

	@Override
	public String downloadCmd(final String url) {
		return SoirFootConf.HTTP_CMD;
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

	}

}
