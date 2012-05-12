package com.dabi.habitv.provider.tvSubtitles;

import java.util.Set;

import com.dabi.habitv.framework.plugin.api.ProviderPluginInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public class TvSubtitlesPluginManager implements ProviderPluginInterface {

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return TvSubtitlesRetriever.findEpisodeByCategory(category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return TvSubtitlesCategoriesFinder.findCategory(); 
	}

	@Override
	public String downloadCmd(final String url) {
		return TvSubtitlesConf.HTTP_CMD;
	}

	@Override
	public String getDownloader(final String url) {
		return TvSubtitlesConf.HTTP;
	}

	@Override
	public String getName() {
		return TvSubtitlesConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {

	}

}
