package com.dabi.habitv.provider.arte;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

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
		return findCategories();
	}

	@Override
	public ProcessHolder download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders)
			throws DownloadFailedException {
		return DownloadUtils.download(downloadParam, downloaders, "youtube");
	}

	private Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO category) {
		return jsonToEpisodes(category,
				getJsonLine(category.getId(), "#container-collection", "data-categoryVideoSet"));
	}

	private Set<EpisodeDTO> jsonToEpisodes(CategoryDTO category, String jsonLine) {
		final Set<EpisodeDTO> episodeList = new LinkedHashSet<EpisodeDTO>();

		if (jsonLine != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode;
			try {
				jsonNode = objectMapper.readTree(jsonLine);
				for (JsonNode catVide : jsonNode.get("videos")) {
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

	private Set<CategoryDTO> findCategories() {
		return jsonToCategories(getJsonLine(ArteConf.CAT_PAGE, "#home", "data-categoriesVideos"));
	}

	private String getJsonLine(String url, String blockId, String properties) {
		org.jsoup.nodes.Document doc;
		try {
			doc = Jsoup.parse(getInputStreamFromUrl(url), "UTF-8", url);
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
		Element homeElement = doc.select(blockId).first();
		String json = homeElement.attr(properties);
		return json;
	}

	private Set<CategoryDTO> jsonToCategories(String lineJson) {
		final Set<CategoryDTO> categoryDTOs = new LinkedHashSet<>();
		if (lineJson != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode;
			try {
				jsonNode = objectMapper.readTree(lineJson);
				for (JsonNode categorie : jsonNode) {
					JsonNode cat = categorie.get("category");
					CategoryDTO category = new CategoryDTO(ArteConf.NAME, cat.get("name").asText(),
							ArteConf.HOME_URL + cat.get("url").asText(), FrameworkConf.MP4);
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
