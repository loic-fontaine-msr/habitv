package com.dabi.habitv.provider.canalplus;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.provider.BasePluginProviderClassloader;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
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

public class CanalPlusPluginManager extends BasePluginProviderClassloader { // NO_UCD

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
				categoryDTO = new CategoryDTO(CanalPlusConf.NAME, selection.getNOM(), String.valueOf(selection.getID()), CanalPlusConf.EXTENSION);
				categories.add(categoryDTO);
				categoryDTO.addSubCategories(getCategoryById(String.valueOf(selection.getID())));
			}
		}
		return categories;
	}

	private String getDownloader(final String url) {
		String downloaderName;
		if (url.startsWith(CanalPlusConf.RTMPDUMP_PREFIX)) {
			downloaderName = CanalPlusConf.RTMDUMP;
		} else if (url.contains("m3u8")) {
			downloaderName = CanalPlusConf.FFMPEG;
		} else {
			downloaderName = CanalPlusConf.CURL;
		}
		return downloaderName;
	}

	@Override
	public void download(final String downloadOuput, final DownloaderPluginHolder downloaders, final CmdProgressionListener listener, final EpisodeDTO episode)
			throws DownloadFailedException {
		final String downloaderName = getDownloader(episode.getUrl());
		final PluginDownloaderInterface pluginDownloader = downloaders.getPlugin(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());
		parameters.put(FrameworkConf.EXTENSION, episode.getCategory().getExtension());

		pluginDownloader.download(episode.getUrl(), downloadOuput, parameters, listener, getProtocol2proxy());
	}

	@Override
	public String getName() {
		return CanalPlusConf.NAME;
	}

	private Collection<CategoryDTO> getCategoryById(final String identifier) {
		final Set<CategoryDTO> categories = new HashSet<>();

		final MEAS meas = (MEAS) unmarshalInputStream(getInputStreamFromUrl(CanalPlusConf.MEA_URL + identifier), CanalPlusConf.MEA_PACKAGE_NAME);
		for (final MEA mea : meas.getMEA()) {
			categories.add(new CategoryDTO(CanalPlusConf.NAME, mea.getRUBRIQUAGE().getRUBRIQUE(), String.valueOf(mea.getID()), CanalPlusConf.EXTENSION));
		}

		return categories;
	}

	private Collection<CategoryDTO> getEpisodeCategoryById(final String identifier) {
		final Set<CategoryDTO> categories = new HashSet<>();

		final MEAS meas = (MEAS) unmarshalInputStream(getInputStreamFromUrl(CanalPlusConf.MEA_URL + identifier), CanalPlusConf.MEA_PACKAGE_NAME);
		for (final MEA mea : meas.getMEA()) {
			categories
			.add(new CategoryDTO(CanalPlusConf.NAME, mea.getINFOS().getTITRAGE().getSOUSTITRE(), String.valueOf(mea.getID()), CanalPlusConf.EXTENSION));
		}

		return categories;
	}

	private Set<EpisodeDTO> findEpisodeBySubCategory(final CategoryDTO category, final CategoryDTO originalcategory) {
		final VIDEOS videos = (VIDEOS) unmarshalInputStream(getInputStreamFromUrl(CanalPlusConf.VIDEO_URL + category.getId()), CanalPlusConf.VIDEO_PACKAGE_NAME);
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
			episodes.add(new EpisodeDTO(originalCategory, name, videoUrl));
		}
		return episodes;
	}

}
