package com.dabi.habitv.provider.pluzz.jpluzz;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.provider.pluzz.PluzzConf;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMainParser {

	private final ObjectMapper mapper = new ObjectMapper();
	private final Map<String, CategoryDTO> catName2RootCat;
	private final Map<String, CategoryDTO> catName2LeafCat;
	private final Map<String, Collection<EpisodeDTO>> catName2Episode;
	private final String mainUrl;
	private final Proxy proxy;

	public JsonMainParser(final String mainUrl, final Proxy proxy) {
		super();
		catName2RootCat = new HashMap<String, CategoryDTO>();
		catName2LeafCat = new HashMap<String, CategoryDTO>();
		catName2Episode = new HashMap<String, Collection<EpisodeDTO>>();
		this.mainUrl = mainUrl;
		this.proxy = proxy;
	}

	/**
	 * @return the categories and episode of the archive
	 */
	public Archive load() {
		try {
			loadEntry(RetrieverUtils.getInputStreamFromUrl(mainUrl, proxy));
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
		return new Archive(this.catName2RootCat.values(), catName2Episode);
	}

	private void loadEntry(final InputStream sin) throws JsonParseException, JsonMappingException, IOException {
		@SuppressWarnings("unchecked")
		final Map<String, Object> userData = mapper.readValue(sin, Map.class);
		buildCategoriesAndEpisode(userData);
	}

	@SuppressWarnings("unchecked")
	private void buildCategoriesAndEpisode(final Map<String, Object> userData) {
		final List<Object> emissions = (List<Object>) ((Map<Object,Object>)userData.get("reponse")).get("emissions");
		for (final Object objectEmission : emissions) {
			final CategoryDTO category = loadCategory((Map<String, Object>) objectEmission);
			category.setDownloadable(true);
			loadEpisode((Map<String, Object>) objectEmission, category);
		}
	}

	private void loadEpisode(final Map<String, Object> objectProgramme, final CategoryDTO category) {
		final String name = buildName(objectProgramme);
		final String url_reference = (String) objectProgramme.get("url");
		final String id = (String) objectProgramme.get("id_diffusion");
		final EpisodeDTO episodeDTO = new EpisodeDTO(category, name + "-" + id, PluzzConf.BASE_URL + url_reference);
		addEpisodeToCat(episodeDTO);
	}

	private void addEpisodeToCat(final EpisodeDTO episodeDTO) {
		final String catId = episodeDTO.getCategory().getId();
		Collection<EpisodeDTO> episodeList = catName2Episode.get(catId);
		if (episodeList == null) {
			episodeList = new LinkedList<EpisodeDTO>();
			catName2Episode.put(catId, episodeList);
		}
		episodeList.add(episodeDTO);
	}

	private CategoryDTO loadCategory(final Map<String, Object> objectProgramme) {
		final String fatherCatName = (String) objectProgramme.get("rubrique");
		CategoryDTO fatherCategory = catName2RootCat.get(fatherCatName);
		if (fatherCategory == null) {
			if (fatherCatName == null || fatherCatName.isEmpty()) {
				fatherCategory = new CategoryDTO(PluzzConf.NAME, "Pas de rubrique", "Pas de rubrique",
						PluzzConf.EXTENSION);
			} else {
				fatherCategory = new CategoryDTO(PluzzConf.NAME, fatherCatName, fatherCatName, PluzzConf.EXTENSION);
			}
			catName2RootCat.put(fatherCatName, fatherCategory);
		}
		final String catId = (String) objectProgramme.get("code_programme");
		final String name = (String) objectProgramme.get("titre");
		CategoryDTO category = catName2LeafCat.get(name);
		if (category == null) {
			category = new CategoryDTO(PluzzConf.NAME, name, catId, PluzzConf.EXTENSION);
			catName2LeafCat.put(name, category);
			fatherCategory.addSubCategory(category);
		}
		return category;
	}

	private String buildName(final Map<String, Object> objectProgramme) {
		String subTitle = (String) objectProgramme.get("soustitre");
		if (subTitle == null || subTitle.isEmpty()) {
			subTitle = (String) objectProgramme.get("date_diffusion");
		}
		return subTitle;
	}

}
