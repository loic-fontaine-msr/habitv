package com.dabi.habitv.provider.m6w9;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.Archive;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public class M6W9PluginManager implements PluginProviderInterface {

	private Archive cachedArchive;

	private long cachedTimeMs;

	private Archive getCachedArchive() {
		final long now = System.currentTimeMillis();
		if (cachedArchive == null || (now - cachedTimeMs) > M6W9Conf.MAX_CACHE_ARCHIVE_TIME_MS) {
			cachedArchive = M6W9Retriever.load();
			cachedTimeMs = now;
		}
		return cachedArchive;
	}

	@Override
	public String getName() {
		return M6W9Conf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {

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

		String episodeUrl;
		try {//TODO peut mieux faire
			episodeUrl = M6W9Retriever
					.findFinalLink(RetrieverUtils.getInputStreamFromUrl(String.format(M6W9Conf.CLIP_URL, M6W9Conf.M6_URL_NAME, id1, id2, id)));
		} catch (final TechnicalException e) {
			episodeUrl = M6W9Retriever
					.findFinalLink(RetrieverUtils.getInputStreamFromUrl(String.format(M6W9Conf.CLIP_URL, M6W9Conf.W9_URL_NAME, id1, id2, id)));
		}

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.PARAMETER_ARGS, buildDownloadParam(episodeUrl, M6W9Conf.DUMP_CMD));
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());

		pluginDownloader.download(episodeUrl, downloadOuput, parameters, listener);
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

}
