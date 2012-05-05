package com.dabi.habitv.provider.soirfoot;

import java.util.HashSet;
import java.util.Set;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public final class SoirFootRetriever {

	private SoirFootRetriever() {

	}

	public static Set<EpisodeDTO> findEpisodeByCategory(final ClassLoader classLoader, final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new HashSet<>();
		return episodeList;
	}

}
