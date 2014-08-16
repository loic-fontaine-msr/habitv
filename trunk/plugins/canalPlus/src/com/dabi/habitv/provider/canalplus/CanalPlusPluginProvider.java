package com.dabi.habitv.provider.canalplus;

import java.io.IOException;
import java.util.Collections;
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
import com.fasterxml.jackson.databind.ObjectMapper;

public class CanalPlusPluginProvider extends BasePluginWithProxy implements
		PluginProviderInterface, PluginDownloaderInterface { // NO_UCD

	@Override
	@SuppressWarnings("unchecked")
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final Map<String, Object> catData = mapper.readValue(
					getInputStreamFromUrl(category.getId()), Map.class);

			List<Object> strates = (List<Object>) catData.get("strates");
			for (Object strateObject : strates) {
				Map<String, Object> strateMap = (Map<String, Object>) strateObject;
				String type = (String) strateMap.get("type");
				if ("contentGrid".equals(type)) {
					return findEpisodes(category,
							(List<Object>) strateMap.get("contents"));
				}
			}
			return Collections.emptySet();
		} catch (IOException e) {
			throw new DownloadFailedException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private Set<EpisodeDTO> findEpisodes(CategoryDTO category,
			List<Object> objectContent) {
		Set<EpisodeDTO> episodes = new LinkedHashSet<>();
		for (Object objectEpisode : objectContent) {
			episodes.add(buildEpisode(category,
					(Map<String, Object>) objectEpisode));
		}

		return episodes;
	}

	@SuppressWarnings("unchecked")
	private EpisodeDTO buildEpisode(CategoryDTO category,
			Map<String, Object> mapEpisode) {
		return new EpisodeDTO(category, (String) mapEpisode.get("title"),
				CanalUtils.findUrl(this, (String) ((Map<String, Object>) mapEpisode
						.get("onClick")).get("URLMedias")));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Set<CategoryDTO> findCategory() {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final Map<String, Object> mainData = mapper.readValue(
					getInputStreamFromUrl(CanalPlusConf.URL_HOME), Map.class);

			String urlMainPage = getUrlMainPage(mainData);

			final Map<String, Object> catData = mapper.readValue(
					getInputStreamFromUrl(urlMainPage), Map.class);

			return findCategories(catData);

		} catch (IOException e) {
			throw new DownloadFailedException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private Set<CategoryDTO> findCategories(Map<String, Object> catData) {
		final Set<CategoryDTO> categories = new LinkedHashSet<>();
		for (Object data : ((List<Object>) catData.get("strates"))) {
			Map<String, Object> dataMap = (Map<String, Object>) data;
			addCategory(categories, dataMap);
		}
		return categories;
	}

	private void addCategory(final Set<CategoryDTO> categories,
			Map<String, Object> dataMap) {
		String type = (String) dataMap.get("type");
		if ("landing".equals(type)) {
			categories.add(buildLeafCategory(dataMap));
		} else if ("contentGrid".equals(type)) {
			categories.add(buildNodeCategory(dataMap));
		}
	}

	@SuppressWarnings("unchecked")
	private CategoryDTO buildNodeCategory(Map<String, Object> dataMap) {
		String title = (String) dataMap.get("title");
		CategoryDTO categoryDTO = new CategoryDTO(CanalPlusConf.NAME, title,
				title, FrameworkConf.MP4);
		categoryDTO.setDownloadable(false);
		List<Object> contents = (List<Object>) dataMap.get("contents");
		if (contents != null) {
			for (Object subDataMap : contents) {
				addCategory(categoryDTO.getSubCategories(),
						(Map<String, Object>) subDataMap);
			}
		}
		return categoryDTO;
	}

	@SuppressWarnings("unchecked")
	private CategoryDTO buildLeafCategory(Map<String, Object> dataMap) {
		CategoryDTO categoryDTO = new CategoryDTO(CanalPlusConf.NAME,
				(String) dataMap.get("title"),
				(String) ((Map<String, Object>) dataMap.get("onClick"))
						.get("URLPage"), FrameworkConf.MP4);
		categoryDTO.setDownloadable(true);
		return categoryDTO;
	}

	@SuppressWarnings("unchecked")
	private String getUrlMainPage(Map<String, Object> userData) {
		List<Object> arbList = (List<Object>) userData.get("arborescence");
		for (Object catObject : arbList) {
			Map<String, Object> catMap = (Map<String, Object>) catObject;
			if ("OnDemand".equals(catMap.get("picto"))) {
				return (String) ((Map<String, Object>) catMap.get("onClick"))
						.get("URLPage");
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return CanalPlusConf.NAME;
	}

	@Override
	public DownloadableState canDownload(String downloadInput) {
		if (downloadInput.contains("canalplus.")) {
			return DownloadableState.SPECIFIC;
		} else {
			return DownloadableState.IMPOSSIBLE;
		}
	}

	@Override
	public ProcessHolder download(DownloadParamDTO downloadInput,
			DownloaderPluginHolder downloaders) throws DownloadFailedException {
		return CanalUtils.doDownload(downloadInput, downloaders, this,
				CanalPlusConf.VIDEO_INFO_URL);
	}

}
