package com.dabi.habitv.provider.canalplus;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dabi.habitv.api.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.api.plugin.api.PluginProviderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.utils.SoccerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CanalSportPluginProvider extends BasePluginWithProxy implements
		PluginProviderInterface, PluginDownloaderInterface { // NO_UCD

	@Override
	@SuppressWarnings("unchecked")
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		Set<EpisodeDTO> episodes = new LinkedHashSet<>();
		final ObjectMapper mapper = new ObjectMapper();
		try {
			String token = CanalUtils.findToken(this);
			final Map<String, Object> catData = mapper.readValue(
					getInputStreamFromUrl(CanalSportConf.URL_CATEGORY.replace(
							"{ID}", category.getId())), Map.class);

			List<Object> videos = (List<Object>) catData.get("videos");
			for (Object videoObject : videos) {
				Map<String, Object> videoMap = (Map<String, Object>) videoObject;
				EpisodeDTO episode = buildEpisode(token, category, videoMap);
				if (episode != null) {
					episodes.add(episode);
				}
			}
			return episodes;
		} catch (IOException e) {
			throw new DownloadFailedException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private EpisodeDTO buildEpisode(String token, CategoryDTO category,
			Map<String, Object> mapEpisode) {
		Map<String, Object> mapVideo = (Map<String, Object>) mapEpisode
				.get("Video");
		String vid = (String) mapVideo.get("v_id");
		return vid == null || vid.isEmpty() ? null : new EpisodeDTO(category,
				SoccerUtils.maskScore((String) mapVideo.get("v_s_titre")),
				buildUrl(token, (String) vid));
	}

	private String buildUrl(String token, String vid) {
		return CanalSportConf.URL_VID.replace("{VID}", vid);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Set<CategoryDTO> findCategory() {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final Map<String, Object> catData = mapper.readValue(
					getInputStreamFromUrl(CanalSportConf.URL_HOME), Map.class);

			return findCategories(catData);
		} catch (IOException e) {
			throw new DownloadFailedException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private Set<CategoryDTO> findCategories(Map<String, Object> catData) {
		final Set<CategoryDTO> categories = new LinkedHashSet<>();
		for (Object data : ((List<Object>) catData.get("menu"))) {
			Map<String, Object> dataMap = (Map<String, Object>) data;
			categories.add(buildCategory((Map<String, Object>) dataMap
					.get("Catvideo")));
		}
		return categories;
	}

	private CategoryDTO buildCategory(Map<String, Object> dataMap) {
		CategoryDTO categoryDTO = new CategoryDTO(CanalSportConf.NAME,
				(String) dataMap.get("catv_titre"),
				(String) dataMap.get("catv_id"), FrameworkConf.MP4);
		categoryDTO.setDownloadable(true);
		return categoryDTO;
	}

	@Override
	public String getName() {
		return CanalSportConf.NAME;
	}

	@Override
	public DownloadableState canDownload(String downloadInput) {
		return DownloadableState.IMPOSSIBLE;
	}

	@Override
	public ProcessHolder download(DownloadParamDTO downloadInput,
			DownloaderPluginHolder downloaders) throws DownloadFailedException {
		return CanalUtils.doDownload3(downloadInput, downloaders, this,
				CanalPlusConf.VIDEO_INFO_URL, getName().toLowerCase());
	}

}
