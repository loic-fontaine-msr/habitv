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

	protected Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO originalcategory) {
		final Set<EpisodeDTO> episodes;

		if (originalcategory.getSubCategories().isEmpty()) {
			episodes = findEpisodeBySubCategory(originalcategory, originalcategory);
			for (final CategoryDTO subCategory : getCategoryById(originalcategory.getId())) {
				episodes.addAll(findEpisodeBySubCategory(subCategory, originalcategory));
			}
		} else {
			episodes = new HashSet<>();
			for (final CategoryDTO subCategory : originalcategory.getSubCategories()) {
				episodes.addAll(findEpisodeBySubCategory(subCategory, originalcategory));
			}
		}
		return episodes;
	}

	private Collection<CategoryDTO> getCategoryById(final String identifier) {
		final Set<CategoryDTO> categories = new HashSet<>();

		final MEAS meas = (MEAS) RetrieverUtils.unmarshalInputStream(RetrieverUtils.getInputStreamFromUrl(CanalPlusConf.MEA_URL + identifier),
				CanalPlusConf.MEA_PACKAGE_NAME, classLoader);
		for (final MEA mea : meas.getMEA()) {
			categories
					.add(new CategoryDTO(CanalPlusConf.NAME, mea.getINFOS().getTITRAGE().getSOUSTITRE(), String.valueOf(mea.getID()), CanalPlusConf.EXTENSION));
		}

		return categories;
	}

	private Set<EpisodeDTO> findEpisodeBySubCategory(final CategoryDTO category, final CategoryDTO originalcategory) {
		final VIDEOS videos = (VIDEOS) RetrieverUtils.unmarshalInputStream(RetrieverUtils.getInputStreamFromUrl(CanalPlusConf.VIDEO_URL + category.getId()),
				CanalPlusConf.VIDEO_PACKAGE_NAME, classLoader);
		return buildFromVideo(category, videos, originalcategory);
	}

	protected static Set<EpisodeDTO> buildFromVideo(final CategoryDTO category, final VIDEOS videos, final CategoryDTO originalCategory) {
		final Set<EpisodeDTO> episodes = new HashSet<>();
		for (final VIDEO video : videos.getVIDEO()) {
			String videoUrl = video.getMEDIA().getVIDEOS().getHD();
			if (videoUrl == null || videoUrl.length() < 2) {
				videoUrl = video.getMEDIA().getVIDEOS().getHAUTDEBIT();
			}
			if (videoUrl == null || videoUrl.length() < 2) {
				videoUrl = video.getMEDIA().getVIDEOS().getBASDEBIT();
			}

			String name = video.getINFOS().getTITRAGE().getSOUSTITRE() + " - " + video.getINFOS().getTITRAGE().getTITRE();
			if (originalCategory.getName().contains("FOOTBALL")) {
				name = name.replaceAll("(\\d\\s*-\\s*\\d)", "").replaceAll("(\\d_*-_*\\d)", "");
			}

			// il est possible que plusieurs épisode s'appelle du soustitre
			// mais si on concatène avec titre c'est trop long
			episodes.add(new EpisodeDTO(originalCategory, name, videoUrl));
		}
		return episodes;
	}
}
