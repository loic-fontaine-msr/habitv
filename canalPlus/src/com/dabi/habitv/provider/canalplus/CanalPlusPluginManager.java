package com.dabi.habitv.provider.canalplus;

import java.util.Set;

import com.dabi.habitv.framework.plugin.api.ProviderPluginInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public class CanalPlusPluginManager implements ProviderPluginInterface {

	private ClassLoader classLoader;

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return new CanalPlusRetriever(classLoader).findEpisodeByCategory(category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return new CanalPlusCategoriesFinder(classLoader).findCategory();
	}

	@Override
	public String downloadCmd(final String url) {
		return null;
	}

	@Override
	public String getDownloader(final String url) {
		String downloaderName;
		if (url.startsWith(CanalPlusConf.RTMPDUMP_PREFIX)) {
			downloaderName = CanalPlusConf.RTMDUMP;
		} else {
			downloaderName = CanalPlusConf.CURL;
		}
		return downloaderName;
	}

	@Override
	public String getName() {
		return CanalPlusConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

}
