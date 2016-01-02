package com.dabi.habitv.provider.arte;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import com.dabi.habitv.api.plugin.api.PluginProviderDownloaderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.utils.DownloadUtils;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ArtePluginManager extends BasePluginWithProxy implements PluginProviderDownloaderInterface { // NO_UCD

	@Override
	public String getName() {
		return ArteConf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return findEpisodeByCategory(category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return findCategories(RetrieverUtils.getUrlContent(ArteConf.CAT_PAGE, FrameworkConf.UTF8, getHttpProxy()));
	}

	@Override
	public ProcessHolder download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders)
			throws DownloadFailedException {
		return DownloadUtils.download(downloadParam, downloaders, "youtube");
	}

	private Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO category) {
		return jsonToEpisodes(category,
				getJsonLine(RetrieverUtils.getUrlContent(category.getId(), FrameworkConf.UTF8, getHttpProxy()), "categoryVideoSet"));
	}

	private Set<EpisodeDTO> jsonToEpisodes(CategoryDTO category, String jsonLine) {
		final Set<EpisodeDTO> episodeList = new LinkedHashSet<EpisodeDTO>();

		if (jsonLine != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode;
			try {
				jsonNode = objectMapper.readTree(jsonLine);
				JsonNode categoriesVideso = jsonNode.get("categoryVideoSet");
				for (JsonNode catVide : categoriesVideso.get("videos")) {
					episodeList.add(new EpisodeDTO(category, getTitle(catVide), catVide.get("url").asText()));
				}
			} catch (IOException e) {
				throw new TechnicalException(e);
			}
		}

		return episodeList;
	}

	private String getTitle(JsonNode vid) {
		JsonNode titleNode = vid.get("title");
		StringBuilder title = new StringBuilder(titleNode.asText());
		JsonNode subTitleNode = vid.get("subtitle");
		if (subTitleNode != null && !subTitleNode.isNull()) {
			title.append(" - " + subTitleNode.asText());
		} else {
			JsonNode dateNode = vid.get("scheduled_on");
			title.append(" - " + dateNode.asText());
			title.append(" - " + vid.get("id").asText());
		}
		return title.toString();
	}

	private Set<CategoryDTO> findCategories(final String urlContent) {
		return jsonToCategories(getJsonLine(urlContent, "categoriesVideos"));
	}

	private String getJsonLine(final String urlContent, String properties) {
		String lineJson = null;
		for (String line : urlContent.split("\\n")) {
			if (line.contains(properties)) {
				lineJson = "{" + line.replace(properties, "\"" + properties + "\"") + "\"lol\":{}}";
			}
		}
		return lineJson;
	}

	private Set<CategoryDTO> jsonToCategories(String lineJson) {
		final Set<CategoryDTO> categoryDTOs = new LinkedHashSet<>();
		if (lineJson != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode;
			try {
				jsonNode = objectMapper.readTree(lineJson);
				JsonNode categoriesVideso = jsonNode.get("categoriesVideos");
				for (JsonNode categorie : categoriesVideso) {
					JsonNode cat = categorie.get("category");
					CategoryDTO category = new CategoryDTO(ArteConf.NAME, cat.get("name").asText(), ArteConf.HOME_URL
							+ cat.get("url").asText(), FrameworkConf.MP4);
					category.setDownloadable(true);
					categoryDTOs.add(category);
				}
			} catch (IOException e) {
				throw new TechnicalException(e);
			}
		}
		return categoryDTOs;
	}

	@Override
	public DownloadableState canDownload(String downloadInput) {
		return downloadInput.contains("arte") ? DownloadableState.SPECIFIC : DownloadableState.IMPOSSIBLE;
	}

}
