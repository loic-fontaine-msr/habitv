package com.dabi.habitv.provider.m6w9;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.Archive;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.provider.BasePluginProvider;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class M6W9PluginManager extends BasePluginProvider {

	private static final Logger LOG = Logger.getLogger(M6W9PluginManager.class);

	private Archive cachedArchive;

	private long cachedTimeMs;

	private Archive getCachedArchive() {
		final long now = System.currentTimeMillis();
		if (cachedArchive == null || (now - cachedTimeMs) > M6W9Conf.MAX_CACHE_ARCHIVE_TIME_MS) {
			cachedArchive = load();
			cachedTimeMs = now;
		}
		return cachedArchive;
	}

	@Override
	public String getName() {
		return M6W9Conf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new HashSet<>();
		if (!category.getSubCategories().isEmpty()) {
			for (final CategoryDTO subCat : category.getSubCategories()) {
				episodeList.addAll(findEpisode(subCat));
			}
		}
		final Collection<EpisodeDTO> collection = getCachedArchive().getCatName2Episode().get(category.getId());
		if (collection != null) {
			episodeList.addAll(collection);
		}
		return episodeList;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return new HashSet<>(getCachedArchive().getCategories());
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener listener, final EpisodeDTO episode)
			throws DownloadFailedException, NoSuchDownloaderException {

		final String downloaderName = M6W9Conf.RTMDUMP;
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);
		final String id = episode.getUrl();
		final String id1 = id.substring(id.length() - 2, id.length());
		final String id2 = id.substring(id.length() - 4, id.length() - 2);

		LinkedList<String> episodeUrlList;
		try {// TODO peut mieux faire
			episodeUrlList = findFinalLink(getInputStreamFromUrl(String.format(M6W9Conf.CLIP_URL, M6W9Conf.M6_URL_NAME, id1, id2, id)));
		} catch (final TechnicalException e) {
			try {
				episodeUrlList = findFinalLink(getInputStreamFromUrl(String.format(M6W9Conf.CLIP_URL, M6W9Conf.W9_URL_NAME, id1, id2, id)));
			} catch (final TechnicalException e1) {
				episodeUrlList = findFinalLink(getInputStreamFromUrl(String.format(M6W9Conf.CLIP_URL, M6W9Conf.SIXTER_URL_NAME, id1, id2, id)));
			}
		}

		int i = 0;
		for (final String episodeUrl : episodeUrlList) {
			final Map<String, String> parameters = new HashMap<>(2);
			parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
			parameters.put(FrameworkConf.PARAMETER_ARGS, buildDownloadParam(episodeUrl, M6W9Conf.RTMPDUMP_CMD));
			parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());

			i++;
			try {
				pluginDownloader.download(episodeUrl, downloadOuput, parameters, listener, getProtocol2proxy());
				break;
			} catch (final DownloadFailedException | TechnicalException e) {
				LOG.error("", e);
				// will try next link if there is
				if (i < episodeUrlList.size()) {
					continue;
				} else {
					throw e;
				}
			}
		}
	}

	private String buildDownloadParam(final String episodeUrl, final String dumpCmd) throws DownloadFailedException {
		final long param2 = (getServerDate()).getTime() / 1000;
		final String loc_3 = episodeUrl.substring(4, episodeUrl.length());
		final long loc_4 = param2 + M6W9Conf.DELAY;
		final String loc_5 = (M6W9Conf.LIMELIGHT_APPLICATION_NAME + loc_3) + "?s=" + param2 + "&e=" + loc_4;
		final String loc_6 = MD5hash(M6W9Conf.LIMELIGHT_SECRET_KEY + loc_5);
		final String loc_7 = "s=" + param2 + "&e=" + loc_4 + "&h=" + loc_6;
		return dumpCmd.replace("#TOKEN#", loc_7);
	}

	protected Date getServerDate() {
		return new Date();
	}

	private String MD5hash(final String toHash) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(toHash.getBytes());
		} catch (final NoSuchAlgorithmException e) {
			throw new TechnicalException(e);
		}
		final StringBuilder hashString = new StringBuilder();
		for (int i = 0; i < hash.length; i++) {
			final String hex = Integer.toHexString(hash[i]);
			if (hex.length() == 1) {
				hashString.append('0');
				hashString.append(hex.charAt(hex.length() - 1));
			} else
				hashString.append(hex.substring(hex.length() - 2));
		}
		return hashString.toString();
	}

	private static final ObjectMapper mapper = new ObjectMapper();

	private Archive load() {
		final Map<String, CategoryDTO> categories = new HashMap<>();
		final Map<String, Collection<EpisodeDTO>> catName2Episode = new HashMap<>();
		try {
			loadChannel(M6W9Conf.M6_NAME, M6W9Conf.M6_URL_NAME, categories, catName2Episode);
			loadChannel(M6W9Conf.W9_NAME, M6W9Conf.W9_URL_NAME, categories, catName2Episode);
			loadChannel(M6W9Conf.SIXTER_NAME, M6W9Conf.SIXTER_URL_NAME, categories, catName2Episode);
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
		return new Archive(categories.values(), catName2Episode);
	}

	@SuppressWarnings("unchecked")
	private void loadChannel(final String channel, final String urlName, final Map<String, CategoryDTO> categories,
			final Map<String, Collection<EpisodeDTO>> catName2Episode) throws JsonParseException, JsonMappingException, IOException {
		final InputStream in = getInputStreamFromUrl(String.format(M6W9Conf.CATALOG_URL, urlName));
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
