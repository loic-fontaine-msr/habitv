package com.dabi.habitv.provider.pluzz;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.provider.pluzz.jpluzz.Archive;
import com.dabi.habitv.provider.pluzz.jpluzz.JsonArchiveParser;
import com.dabi.habitv.provider.pluzz.jpluzz.PluzzDLM3U8;

public class PluzzPluginManager implements PluginProviderInterface {

	private Archive cachedArchive;

	private long cachedTimeMs;

	private final JsonArchiveParser jsonArchiveParser = new JsonArchiveParser(PluzzConf.ZIP_URL);

	@Override
	public String getName() {
		return PluzzConf.NAME;
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

	private Archive getCachedArchive() {
		final long now = System.currentTimeMillis();
		if (cachedArchive == null || (now - cachedTimeMs) > PluzzConf.MAX_CACHE_ARCHIVE_TIME_MS) {
			cachedArchive = jsonArchiveParser.load();
			cachedTimeMs = now;
		}
		return cachedArchive;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return new HashSet<>(getCachedArchive().getCategories());
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener,
			final EpisodeDTO episode) throws DownloadFailedException, NoSuchDownloaderException {
		final String assemblerBinPath = downloaders.getBinPath(PluzzConf.ASSEMBLER);
		if (assemblerBinPath == null) {
			throw new TechnicalException(PluzzConf.ASSEMBLER + " downloader can't be found, add it the config.xml");
		}
		final PluzzDLM3U8 dl = new PluzzDLM3U8(cmdProgressionListener, downloadOuput, PluzzConf.CORRECT_VIDEO_CMD_MP4, assemblerBinPath);
		dl.dl(episode.getUrl());
	}

}
