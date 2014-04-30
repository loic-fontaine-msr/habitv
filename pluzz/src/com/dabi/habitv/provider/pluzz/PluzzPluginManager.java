package com.dabi.habitv.provider.pluzz;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.BasePluginProvider;
import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.utils.M3U8Utils;
import com.dabi.habitv.provider.pluzz.jpluzz.Archive;
import com.dabi.habitv.provider.pluzz.jpluzz.JsonArchiveParser;

public class PluzzPluginManager extends BasePluginProvider {

	private Archive cachedArchive;

	private long cachedTimeMs;

	private JsonArchiveParser jsonArchiveParser;

	@Override
	public String getName() {
		return PluzzConf.NAME;
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
			for (final EpisodeDTO episode : collection) {
				final EpisodeDTO newEp = new EpisodeDTO(category, episode.getName(), episode.getUrl());
				newEp.setNum(episode.getNum());
				episodeList.add(newEp);
			}
		}
		return episodeList;
	}

	public JsonArchiveParser getJsonArchiveParser() {
		if (jsonArchiveParser == null) {
			jsonArchiveParser = new JsonArchiveParser(PluzzConf.ZIP_URL, getHttpProxy());
		}
		return jsonArchiveParser;
	}

	private Archive getCachedArchive() {
		final long now = System.currentTimeMillis();
		if (cachedArchive == null || (now - cachedTimeMs) > PluzzConf.MAX_CACHE_ARCHIVE_TIME_MS) {
			cachedArchive = getJsonArchiveParser().load();
			cachedTimeMs = now;
		}
		return cachedArchive;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return new HashSet<>(getCachedArchive().getCategories());
	}

	@Override
	public void download(final String downloadOuput, final DownloaderPluginHolder downloaders, final CmdProgressionListener cmdProgressionListener,
			final EpisodeDTO episode) throws DownloadFailedException {
		final String manifestUrl = M3U8Utils.keepBestQuality(PluzzConf.BASE_URL + episode.getUrl());

		final PluginDownloaderInterface pluginDownloader = downloaders.getPlugin(PluzzConf.FFMPEG);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(PluzzConf.FFMPEG));
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());
		parameters.put(FrameworkConf.EXTENSION, PluzzConf.EXT);

		pluginDownloader.download(manifestUrl, downloadOuput, parameters, cmdProgressionListener, getProtocol2proxy());
	}

}
