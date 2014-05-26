package com.dabi.habitv.provider.pluzz.jpluzz;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.provider.pluzz.PluzzConf;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Parse Pluzz JSON file archive
 * 
 */
public class JsonArchiveParser {

	private static final Logger LOGGER = Logger.getLogger(JsonArchiveParser.class);

	private final Map<String, CategoryDTO> catName2RootCat;
	private final Map<String, CategoryDTO> catId2LeafCat;
	private final Map<String, Collection<EpisodeDTO>> catName2Episode;
	private final String zipUrl;
	private final Proxy proxy;

	/**
	 * @param zipUrl
	 *            zipped jsons files
	 */
	public JsonArchiveParser(final String zipUrl, final Proxy proxy) {
		super();
		catName2RootCat = new HashMap<String, CategoryDTO>();
		catId2LeafCat = new HashMap<String, CategoryDTO>();
		catName2Episode = new HashMap<String, Collection<EpisodeDTO>>();
		this.zipUrl = zipUrl;
		this.proxy = proxy;
	}

	/**
	 * @return the categories and episode of the archive
	 */
	public Archive load() {
		final ZipInputStream zin = new ZipInputStream(RetrieverUtils.getInputStreamFromUrl(zipUrl, proxy));
		ZipEntry zipEntry;
		try {
			zipEntry = zin.getNextEntry();
			while (zipEntry != null) {
				LOGGER.debug(zipEntry.getName());
				if (zipEntry.getName().startsWith("catch_up_")) {
					loadEntry(zin);
					zin.closeEntry();
				}
				zipEntry = zin.getNextEntry();
			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				zin.close();
			} catch (final IOException e) {
				LOGGER.error("", e);
			}
		}
		return new Archive(this.catName2RootCat.values(), catName2Episode);
	}

	private void loadEntry(final InputStream zin) throws JsonParseException, JsonMappingException, IOException {
		final ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		final Map<String, Object> userData = mapper.readValue(buildUnclosableStream(zin), Map.class);
		buildCategoriesAndEpisode(userData);
	}

	@SuppressWarnings("unchecked")
	private void buildCategoriesAndEpisode(final Map<String, Object> userData) {
		final List<Object> programmes = (List<Object>) userData.get("programmes");
		for (final Object objectProgramme : programmes) {
			final CategoryDTO category = loadCategory((Map<String, Object>) objectProgramme);
			category.setDownloadable(true);
			loadEpisode((Map<String, Object>) objectProgramme, category);
		}
	}

	private void loadEpisode(final Map<String, Object> objectProgramme, final CategoryDTO category) {
		final String name = buildName(objectProgramme);
		final String videoUrl = (String) objectProgramme.get("url_video");
		final EpisodeDTO episodeDTO = new EpisodeDTO(category, name, videoUrl);
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
			fatherCategory = new CategoryDTO(PluzzConf.NAME, fatherCatName, fatherCatName, PluzzConf.EXTENSION);
			catName2RootCat.put(fatherCatName, fatherCategory);
		}
		final String catId = (String) objectProgramme.get("code_programme");
		final String name = (String) objectProgramme.get("titre");
		CategoryDTO category = catId2LeafCat.get(catId);
		if (category == null) {
			category = new CategoryDTO(PluzzConf.NAME, name, catId, PluzzConf.EXTENSION);
			catId2LeafCat.put(catId, category);
			fatherCategory.addSubCategory(category);
		}
		return category;
	}

	private String buildName(final Map<String, Object> objectProgramme) {
		String subTitle = (String) objectProgramme.get("sous_titre");
		if (subTitle == null || subTitle.isEmpty()) {
			subTitle = (String) objectProgramme.get("date");
		}
		return subTitle;
	}

	private InputStream buildUnclosableStream(final InputStream zin) {
		return new InputStream() {

			/**
			 * @see java.io.InputStream#available()
			 */
			@Override
			public int available() throws IOException {
				return zin.available();
			}

			/**
			 * @see java.io.InputStream#mark(int)
			 */
			@Override
			public synchronized void mark(final int readlimit) {
				zin.mark(readlimit);
			}

			/**
			 * @see java.io.InputStream#markSupported()
			 */
			@Override
			public boolean markSupported() {
				return zin.markSupported();
			}

			/**
			 * @see java.io.InputStream#read(byte[], int, int)
			 */
			@Override
			public int read(final byte[] b, final int off, final int len) throws IOException {
				return zin.read(b, off, len);
			}

			/**
			 * @see java.io.InputStream#read(byte[])
			 */
			@Override
			public int read(final byte[] b) throws IOException {
				return zin.read(b);
			}

			/**
			 * @see java.io.InputStream#reset()
			 */
			@Override
			public synchronized void reset() throws IOException {
				zin.reset();
			}

			/**
			 * @see java.io.InputStream#skip(long)
			 */
			@Override
			public long skip(final long n) throws IOException {
				return zin.skip(n);
			}

			/**
			 * @see java.io.InputStream#close()
			 */
			@Override
			public void close() throws IOException {

			}

			/**
			 * @see java.io.InputStream#read()
			 */
			@Override
			public int read() throws IOException {
				return zin.read();
			}

		};
	}
}
