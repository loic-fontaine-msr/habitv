package com.dabi.habitv.provider.canalplus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.dabi.habitv.api.plugin.api.PluginClassLoaderInterface;
import com.dabi.habitv.api.plugin.api.PluginProviderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.utils.M3U8Utils;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.framework.plugin.utils.SoccerUtils;
import com.dabi.habitv.provider.canalplus.initplayer.entities.INITPLAYER;
import com.dabi.habitv.provider.canalplus.initplayer.entities.SELECTION;
import com.dabi.habitv.provider.canalplus.initplayer.entities.THEMATIQUE;
import com.dabi.habitv.provider.canalplus.mea.entities.MEA;
import com.dabi.habitv.provider.canalplus.mea.entities.MEAS;
import com.dabi.habitv.provider.canalplus.video.entities.VIDEO;
import com.dabi.habitv.provider.canalplus.video.entities.VIDEOS;

public class CanalPlusPluginProvider extends BasePluginWithProxy implements PluginProviderInterface, PluginClassLoaderInterface { // NO_UCD

	private ClassLoader classLoader;

	@Override
	public final void setClassLoader(final ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	private ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodes;

		if (category.getSubCategories().isEmpty()) {
			episodes = findEpisodeBySubCategory(category, category);
			for (final CategoryDTO subCategory : getEpisodeCategoryById(category.getId())) {
				episodes.addAll(findEpisodeBySubCategory(subCategory, category));
			}
		} else {
			episodes = new HashSet<>();
			for (final CategoryDTO subCategory : category.getSubCategories()) {
				episodes.addAll(findEpisodeBySubCategory(subCategory, category));
			}
		}
		return episodes;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categories = new HashSet<>();

		final INITPLAYER initplayer = (INITPLAYER) RetrieverUtils.unmarshalInputStream(getInputStreamFromUrl(CanalPlusConf.INITPLAYER_URL),
				CanalPlusConf.INITPLAYER_PACKAGE_NAME, getClassLoader());
		CategoryDTO categoryDTO;
		for (final THEMATIQUE thematique : initplayer.getTHEMATIQUES().getTHEMATIQUE()) {
			for (final SELECTION selection : thematique.getSELECTIONS().getSELECTION()) {
				categoryDTO = new CategoryDTO(CanalPlusConf.NAME, selection.getNOM(), String.valueOf(selection.getID()), getExtension());
				categories.add(categoryDTO);
				categoryDTO.addSubCategories(getCategoryById(String.valueOf(selection.getID())));
			}
		}
		return categories;
	}

	private String getExtension() {
		return FrameworkConf.MP4;
	}

	@Override
	public String getName() {
		return CanalPlusConf.NAME;
	}

	private Collection<CategoryDTO> getCategoryById(final String identifier) {
		final Set<CategoryDTO> categories = new HashSet<>();

		final MEAS meas = (MEAS) RetrieverUtils.unmarshalInputStream(getInputStreamFromUrl(CanalPlusConf.MEA_URL + identifier), CanalPlusConf.MEA_PACKAGE_NAME);
		for (final MEA mea : meas.getMEA()) {
			categories.add(new CategoryDTO(CanalPlusConf.NAME, mea.getRUBRIQUAGE().getRUBRIQUE(), String.valueOf(mea.getID()), getExtension()));
		}

		return categories;
	}

	private Collection<CategoryDTO> getEpisodeCategoryById(final String identifier) {
		final Set<CategoryDTO> categories = new HashSet<>();

		final MEAS meas = (MEAS) RetrieverUtils.unmarshalInputStream(getInputStreamFromUrl(CanalPlusConf.MEA_URL + identifier), CanalPlusConf.MEA_PACKAGE_NAME);
		for (final MEA mea : meas.getMEA()) {
			categories.add(new CategoryDTO(CanalPlusConf.NAME, mea.getINFOS().getTITRAGE().getSOUSTITRE(), String.valueOf(mea.getID()), getExtension()));
		}

		return categories;
	}

	private Set<EpisodeDTO> findEpisodeBySubCategory(final CategoryDTO category, final CategoryDTO originalcategory) {
		final VIDEOS videos = (VIDEOS) RetrieverUtils.unmarshalInputStream(getInputStreamFromUrl(CanalPlusConf.VIDEO_URL + category.getId()),
				CanalPlusConf.VIDEO_PACKAGE_NAME);
		return buildFromVideo(category, videos, originalcategory);
	}

	private static Set<EpisodeDTO> buildFromVideo(final CategoryDTO category, final VIDEOS videos, final CategoryDTO originalCategory) {
		final Set<EpisodeDTO> episodes = new HashSet<>();
		for (final VIDEO video : videos.getVIDEO()) {
			String videoUrl = video.getMEDIA().getVIDEOS().getHLS();
			if (videoUrl == null || videoUrl.length() < 2) {
				videoUrl = video.getMEDIA().getVIDEOS().getHD();
			} else {
				videoUrl = M3U8Utils.keepBestQuality(videoUrl);
			}
			if (videoUrl == null || videoUrl.length() < 2) {
				videoUrl = video.getMEDIA().getVIDEOS().getHAUTDEBIT();
			}
			if (videoUrl == null || videoUrl.length() < 2) {
				videoUrl = video.getMEDIA().getVIDEOS().getBASDEBIT();
			}

			String name = video.getINFOS().getTITRAGE().getSOUSTITRE() + " - " + video.getINFOS().getTITRAGE().getTITRE();
			if (originalCategory.getName().contains("FOOTBALL")) {
				name = SoccerUtils.maskScore(name);
			}
			if (video.getINFOS().getTITRAGE().getSOUSTITRE() == null || video.getINFOS().getTITRAGE().getSOUSTITRE().isEmpty()) {
				name = video.getINFOS().getPUBLICATION().getDATE();
			}

			// il est possible que plusieurs épisode s'appelle du soustitre
			// mais si on concatène avec titre c'est trop long
			if (checkName(name)) {
				episodes.add(new EpisodeDTO(originalCategory, name, videoUrl));
			}
		}
		return episodes;
	}

	private static boolean checkName(final String name) {
		return name != null && name.isEmpty();
	}

}
