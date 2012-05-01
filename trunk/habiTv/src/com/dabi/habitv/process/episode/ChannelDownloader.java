package com.dabi.habitv.process.episode;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.dabi.habitv.config.HabitTvConf;
import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.config.entities.Downloader;
import com.dabi.habitv.config.entities.Exporter;
import com.dabi.habitv.dldao.DownloadedDAO;
import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.api.ExporterPluginInterface;
import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.ProviderPluginInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.plugin.PluginFactory;
import com.dabi.habitv.utils.FilterUtils;

public class ChannelDownloader implements Runnable {

	private final List<CategoryDTO> categoryList;

	private final ExecutorService downloadThreadPool;

	private final Config config;

	private final ProviderPluginInterface provider;

	private final ExecutorService exportThreadPool;

	private ExecutorService miscThreadPool; // FIXME un par episode !

	private final PluginFactory<ExporterPluginInterface> exporterFactory;

	private final PluginFactory<PluginDownloaderInterface> dowloaderFactory;

	private final ProcessEpisodeListener listener;

	public ChannelDownloader(final ProcessEpisodeListener listener, final List<CategoryDTO> categoryList, final Config config,
			final ExecutorService exportThreadPool, final ProviderPluginInterface provider, final PluginFactory<ExporterPluginInterface> exporterFactory,
			final PluginFactory<PluginDownloaderInterface> dowloaderFactory) {
		this.listener = listener;
		this.config = config;
		this.categoryList = categoryList;
		this.exportThreadPool = exportThreadPool;
		this.downloadThreadPool = Executors.newFixedThreadPool(config.getSimultaneousEpisodeDownload());
		this.provider = provider;
		this.exporterFactory = exporterFactory;
		this.dowloaderFactory = dowloaderFactory;
	}

	private void downloadEpisode(final List<CategoryDTO> categoryDTOs) {
		for (final CategoryDTO category : categoryDTOs) {
			if (category.getSubCategories().isEmpty()) {
				// dao to find dowloaded episodes
				final DownloadedDAO filesDAO = new DownloadedDAO(config.getWorkingDir(), provider.getName(), category.getName());
				if (!filesDAO.isIndexCreated()) {
					listener.buildEpisodeIndex(category);
				}
				// get list of downloadable episodes
				Set<EpisodeDTO> episodeList = provider.findEpisode(category);
				// filter episode lister by include/exclude and already
				// downloaded
				episodeList = FilterUtils.filterByIncludeExcludeAndDowloaded(episodeList, category.getInclude(), category.getExclude(),
						filesDAO.findDownloadedFiles());
				for (final EpisodeDTO episode : episodeList) {
					final EpisodeExporter episodeExporter = new EpisodeExporter(provider.getName(), category, episode);
					if (filesDAO.isIndexCreated()) {
						listener.episodeToDownload(episode);
						// producer download the file
						downloadThreadPool.execute(buildDownloadAndExportThread(episode, episodeExporter, filesDAO));
					} else {
						// if index has not been created the first run will only
						// fill this file
						filesDAO.addDownloadedFiles(episode.getName());
					}
				}
			} else {
				downloadEpisode(category.getSubCategories());
			}
		}
	}

	private void downloadEpisode() {
		downloadEpisode(categoryList);

		downloadThreadPool.shutdown();
		try {
			downloadThreadPool.awaitTermination(config.getAllDownloadTimeout(), TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new TechnicalException(e);
		}
	}

	private Downloader findDowloader(final String downloaderName) {
		for (Downloader currentDownloader : config.getDownloader()) {
			if (currentDownloader.getName().equals(downloaderName)) {
				return currentDownloader;
			}
		}
		throw new IllegalArgumentException(downloaderName + " n'a pas été trouvé");
	}

	private Runnable buildDownloadAndExportThread(final EpisodeDTO episode, final EpisodeExporter episodeExporter, final DownloadedDAO filesDAO) {
		return new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setName("EPISODE" + episode.getName());
				final String downloaderName = provider.getDownloader(episode.getVideoUrl());
				final PluginDownloaderInterface pluginDownloader = dowloaderFactory.findPlugin(downloaderName, HabitTvConf.DEFAULT_DOWNLOADER);
				final Downloader downloaderConfig = findDowloader(downloaderName);

				try {
					episodeExporter.download(downloaderConfig, pluginDownloader, provider.downloadCmd(episode.getVideoUrl()), config.getDownloadOuput(),
							new CmdProgressionListener() {
								@Override
								public void listen(final String progression) {
									listener.downloadingEpisode(episode, progression);
								}
							});
					listener.downloadedEpisode(episode);

					// add the export thread
					exportThreadPool.execute(buildExportThread(episode, config.getExporter(), episodeExporter, filesDAO, true));
				} catch (ExecutorFailedException e) {
					listener.downloadFailed(episode, e);
				}
			}
		};
	}

	private Runnable buildExportThread(final EpisodeDTO episode, final List<Exporter> exporterList, final EpisodeExporter episodeExporter,
			final DownloadedDAO filesDAO, final boolean rootCall) {
		return new Runnable() {
			@Override
			public void run() {
				final String threadName = "EXPORT" + episode.getName();
				Thread.currentThread().setName(threadName);

				if (rootCall) {
					miscThreadPool = Executors.newCachedThreadPool();
				}

				for (final Exporter exporter : exporterList) {
					try {
						episodeExporter.export(exporter.getCmd(), exporterFactory.findPlugin(exporter.getName(), HabitTvConf.DEFAULT_EXPORTER),
								new CmdProgressionListener() {
									@Override
									public void listen(final String progression) {
										listener.exportEpisode(episode, exporter, progression);
									}
								});
					} catch (ExecutorFailedException e) {
						listener.exportFailed(episode, exporter, e);
					}
					if (!exporter.getExporter().isEmpty()) {
						miscThreadPool.execute(buildExportThread(episode, exporter.getExporter(), episodeExporter, filesDAO, false));
					}
				}

				if (rootCall) {
					miscThreadPool.shutdown();
					try {
						miscThreadPool.awaitTermination(config.getAllDownloadTimeout(), TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						throw new TechnicalException(e);
					}
					filesDAO.addDownloadedFiles(episode.getName());
					listener.episodeReady(episode);
				}
			}
		};
	}

	@Override
	public void run() {
		listener.providerDownloadCheckStarted(provider);
		downloadEpisode();
	}

}
