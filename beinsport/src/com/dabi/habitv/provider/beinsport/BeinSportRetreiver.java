package com.dabi.habitv.provider.beinsport;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.framework.plugin.utils.SoccerUtils;

class BeinSportRetreiver {

	private BeinSportRetreiver() {

	}

	static Set<EpisodeDTO> findEpisodeByCategory(final ClassLoader classLoader, final CategoryDTO category, final InputStream inputStream) {
		final Data data = (Data) RetrieverUtils.unmarshalInputStream(inputStream, BeinSportConf.PACKAGE_NAME, classLoader);

		final List<Video> videos = data.getResults().getResultList().getVideo();
		final Set<EpisodeDTO> episodeList = new HashSet<>();
		for (final Video video : videos) {
			List<File> fileList = video.getVideoFiles().getFile();
			final String name = SoccerUtils.maskScore(video.getDescription());
			episodeList.add(new EpisodeDTO(category, name, fileList.get(0).getValue()));
		}

		return episodeList;
	}
}
