package com.dabi.habitv.provider.m6w9;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.dabi.habitv.framework.plugin.api.dto.Archive;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class M6W9Retriever {

	private static final ObjectMapper mapper = new ObjectMapper();

	private M6W9Retriever() {

	}

	public static Archive load() {
		final Map<String, CategoryDTO> categories = new HashMap<>();
		final Map<String, Collection<EpisodeDTO>> catName2Episode = new HashMap<>();
		try {
			loadChannel(M6W9Conf.M6_NAME, M6W9Conf.M6_URL_NAME, categories, catName2Episode);
			loadChannel(M6W9Conf.W9_NAME, M6W9Conf.W9_URL_NAME, categories, catName2Episode);
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
		return new Archive(categories.values(), catName2Episode);
	}

	@SuppressWarnings("unchecked")
	private static void loadChannel(final String channel, final String urlName, final Map<String, CategoryDTO> categories,
			final Map<String, Collection<EpisodeDTO>> catName2Episode) throws JsonParseException, JsonMappingException, IOException {
		final InputStream in = RetrieverUtils.getInputStreamFromUrl(String.format(M6W9Conf.CATALOG_URL, urlName));
		final Map<String, Object> userData = mapper.readValue(in, Map.class);

		// foreach clpList
		// clé -> id
		// valeur :
		// clpName
		// serie : idPgm
		//
		//
		// foreach pgmList
		// clé -> id
		// valeur :
		// name

		buildCategoryList((Map<String, Object>) userData.get("pgmList"), categories);
		buildEpisodeList((Map<String, Object>) userData.get("clpList"), catName2Episode, categories);
	}

	private static void buildCategoryList(final Map<String, Object> categoryMap, final Map<String, CategoryDTO> categories) {
		for (final Entry<String, Object> catElt : categoryMap.entrySet()) {
			@SuppressWarnings("unchecked")
			final Map<String, Object> categoryFields = (Map<String, Object>) catElt.getValue();
			final String catId = catElt.getKey();
			categories.put(catId, new CategoryDTO(M6W9Conf.NAME, (String) categoryFields.get("name"), catId, M6W9Conf.EXTENSION));
		}
	}

	private static void buildEpisodeList(final Map<String, Object> episodeMap, final Map<String, Collection<EpisodeDTO>> catName2Episode,
			final Map<String, CategoryDTO> categories) {
		for (final Entry<String, Object> episodeElt : episodeMap.entrySet()) {
			@SuppressWarnings("unchecked")
			final Map<String, Object> episodeFields = (Map<String, Object>) episodeElt.getValue();
			final String catId = String.valueOf(episodeFields.get("idPgm"));
			final CategoryDTO category = categories.get(catId);
			if (category == null) {
				categories.put(catId, new CategoryDTO(M6W9Conf.NAME, null, catId, M6W9Conf.EXTENSION));
			}
			addEpisodeToCat(new EpisodeDTO(category, (String) episodeFields.get("clpName"), episodeElt.getKey()), catName2Episode);
		}
	}

	private static void addEpisodeToCat(final EpisodeDTO episodeDTO, final Map<String, Collection<EpisodeDTO>> catName2Episode) {
		final String catId = episodeDTO.getCategory().getId();
		Collection<EpisodeDTO> episodeList = catName2Episode.get(catId);
		if (episodeList == null) {
			episodeList = new LinkedList<EpisodeDTO>();
			catName2Episode.put(catId, episodeList);
		}
		episodeList.add(episodeDTO);
	}

	@SuppressWarnings("unchecked")
	public static LinkedList<String> findFinalLink(final InputStream in) {
		Map<String, CategoryDTO> data;
		try {
			data = mapper.readValue(in, Map.class);
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}

		final Map<String, Object> assetMap = (Map<String, Object>) data.get("asset");
		final LinkedList<String> linkList = new LinkedList<>();
		String link;
		for (final Object objectClip : assetMap.values()) {
			final Map<String, Object> clipMap = (Map<String, Object>) objectClip;
			final String quality = (String) clipMap.get("quality");
			link = (String) clipMap.get("url");
			if (!link.endsWith(".mp4")) {
				link = toMp4(link);
			}
			if ("hd".equals(quality)) {
				linkList.addFirst(link);
			} else {
				linkList.add(link);
			}
		}
		return linkList;
	}

	private static String toMp4(final String link) {

		return "mp4:production/regienum" + link.substring(link.lastIndexOf("/"), link.lastIndexOf(".")) + ".mp4";
	}
}
