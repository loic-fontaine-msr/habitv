package com.dabi.habitv.provider.canalplus;

import java.util.HashSet;
import java.util.Set;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.provider.canalplus.video.entities.VIDEO;
import com.dabi.habitv.provider.canalplus.video.entities.VIDEOS;

public class CanalPlusRetriever {

	private final ClassLoader classLoader;

	public CanalPlusRetriever(final ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	protected Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO category) {
		final Set<EpisodeDTO> episodes;

		if (category.getSubCategories().isEmpty()) {
			episodes = findEpisodeBySubCategory(category);
		} else {
			episodes = new HashSet<>();
			for (CategoryDTO subCategory : category.getSubCategories()) {
				episodes.addAll(findEpisodeBySubCategory(subCategory));
			}
		}
		return episodes;
	}

	private Set<EpisodeDTO> findEpisodeBySubCategory(final CategoryDTO category) {
		final VIDEOS videos = (VIDEOS) RetrieverUtils.unmarshalInputStream(RetrieverUtils.getInputStreamFromUrl(CanalPlusConf.VIDEO_URL + category.getId()),
				CanalPlusConf.VIDEO_PACKAGE_NAME, classLoader);
		return buildFromVideo(category, videos);
	}

	protected static Set<EpisodeDTO> buildFromVideo(final CategoryDTO category, final VIDEOS videos) {
		final Set<EpisodeDTO> episodes = new HashSet<>();
		for (VIDEO video : videos.getVIDEO()) {
			String videoUrl = video.getMEDIA().getVIDEOS().getHD();
			if (videoUrl == null || videoUrl.length() < 2) {
				videoUrl = video.getMEDIA().getVIDEOS().getHAUTDEBIT();
			}
			if (videoUrl == null || videoUrl.length() < 2) {
				videoUrl = video.getMEDIA().getVIDEOS().getBASDEBIT();
			}
			episodes.add(new EpisodeDTO(category.getName(), video.getINFOS().getTITRAGE().getSOUSTITRE(), videoUrl));
		}
		return episodes;
	}
}
