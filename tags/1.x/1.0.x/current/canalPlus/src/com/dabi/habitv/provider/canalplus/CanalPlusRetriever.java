package com.dabi.habitv.provider.canalplus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.provider.canalplus.mea.entities.MEA;
import com.dabi.habitv.provider.canalplus.mea.entities.MEAS;
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
			for (CategoryDTO subCategory : getCategoryById(category.getId())) {
				episodes.addAll(findEpisodeBySubCategory(subCategory));
			}
		} else {
			episodes = new HashSet<>();
			for (CategoryDTO subCategory : category.getSubCategories()) {
				episodes.addAll(findEpisodeBySubCategory(subCategory));
			}
		}
		return episodes;
	}

	private Collection<CategoryDTO> getCategoryById(final String identifier) {
		final Set<CategoryDTO> categories = new HashSet<>();

		final MEAS meas = (MEAS) RetrieverUtils.unmarshalInputStream(RetrieverUtils.getInputStreamFromUrl(CanalPlusConf.MEA_URL + identifier),
				CanalPlusConf.MEA_PACKAGE_NAME, classLoader);
		for (MEA mea : meas.getMEA()) {
			categories.add(new CategoryDTO(mea.getRUBRIQUAGE().getRUBRIQUE(), String.valueOf(mea.getID())));
		}

		return categories;
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

			String name = video.getINFOS().getTITRAGE().getSOUSTITRE();
			if (category.getName().contains("FOOTBALL")) {
				name = name.replaceAll("(\\d\\s*-\\s*\\d)", "").replaceAll("(\\d_*-_*\\d)", "");
			}

			//il est possible que plusieurs épisode s'appelle soustitre
			// mais si on concatène avec titre c'est trop long
			episodes.add(new EpisodeDTO(category.getName(), name, videoUrl));
		}
		return episodes;
	}
}
