package com.dabi.habitv.process.episode;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
import com.dabi.habitv.framework.plugin.exception.InvalidEpisodeException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.plugin.PluginFactory;
import com.dabi.habitv.taskmanager.Task;
import com.dabi.habitv.taskmanager.TaskMgr;
import com.dabi.habitv.taskmanager.TaskTypeEnum;
import com.dabi.habitv.utils.FilterUtils;

public class ChannelDownloader implements Runnable {

	private final TaskMgr taskMgr;

	private final List<CategoryDTO> categoryList;

	private final Config config;

	private final ProviderPluginInterface provider;

	private final PluginFactory<ExporterPluginInterface> exporterFactory;

	private final PluginFactory<PluginDownloaderInterface> dowloaderFactory;

	private final ProcessEpisodeListener listener;

	private final String channel;

	public ChannelDownloader(final String channel, final TaskMgr taskMgr, final ProcessEpisodeListener listener, final List<CategoryDTO> categoryList,
			final Config config, final ProviderPluginInterface provider, final PluginFactory<ExporterPluginInterface> exporterFactory,
			final PluginFactory<PluginDownloaderInterface> dowloaderFactory) {
		this.taskMgr = taskMgr;
		this.listener = listener;
		this.config = config;
		this.categoryList = categoryList;
		this.provider = provider;
		this.exporterFactory = exporterFactory;
		this.dowloaderFactory = dowloaderFactory;
		this.channel = channel;
	}

	private void downloadEpisode(final List<CategoryDTO> categoryDTOs) {
		for (final CategoryDTO category : categoryDTOs) {
			if (!category.getSubCategories().isEmpty()) {
				downloadEpisode(category.getSubCategories());
			} else {
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
					// TODO Gérer les execptions sur un Episode sans tout
					// arréter
					final EpisodeExporter episodeExporter = new EpisodeExporter(provider.getName(), category, episode);
					if (filesDAO.isIndexCreated()) {
						// producer download the file
						Task dlTask = buildDownloadAndExportTask(episode, episodeExporter, filesDAO);
						taskMgr.addTask(dlTask);

						if (dlTask.isAdded()) {
							listener.episodeToDownload(episode);
						}
					} else {
						// if index has not been created the first run will only
						// fill this file
						filesDAO.addDownloadedFiles(episode.getName());
					}
				}
			}
		}
	}

	private Downloader findDowloader(final String downloaderName) {
		for (Downloader currentDownloader : config.getDownloader()) {
			if (currentDownloader.getName().equals(downloaderName)) {
				return currentDownloader;
			}
		}
		throw new IllegalArgumentException(downloaderName + " n'a pas été déclaré");
	}

	private Task buildDownloadAndExportTask(final EpisodeDTO episode, final EpisodeExporter episodeExporter, final DownloadedDAO filesDAO) {
		Runnable job = new Runnable() {
			@Override
			public void run() {
				try {
					episode.check();
				} catch (InvalidEpisodeException e) {
					throw new TechnicalException(e);
				}
				final String downloaderName = provider.getDownloader(episode.getVideoUrl());
				final PluginDownloaderInterface pluginDownloader = dowloaderFactory.findPlugin(downloaderName, HabitTvConf.DEFAULT_DOWNLOADER);
				final Downloader downloaderConfig = findDowloader(downloaderName);

				try {
					episodeExporter.download(downloaderConfig, pluginDownloader, provider.downloadCmd(episode.getVideoUrl()), episode.getVideoUrl(),
							config.getDownloadOuput(), new CmdProgressionListener() {
								@Override
								public void listen(final String progression) {
									listener.downloadingEpisode(episode, progression);
								}
							});
					listener.downloadedEpisode(episode);
					filesDAO.addDownloadedFiles(episode.getName());

					// add the export thread
					taskMgr.addTask(buildExportTask(episode, config.getExporter(), episodeExporter, filesDAO, true));
				} catch (ExecutorFailedException e) {
					listener.downloadFailed(episode, e);
				}
			}
		};
		return new Task(TaskTypeEnum.DOWNLOAD, channel, episode.getVideoUrl(), job);
	}

	private Task buildExportTask(final EpisodeDTO episode, final List<Exporter> exporterList, final EpisodeExporter episodeExporter,
			final DownloadedDAO filesDAO, final boolean rootCall) {
		final Task task;
		if (rootCall) {
			task = new Task(TaskTypeEnum.EXPORT_MAIN, episode.getVideoUrl());
		} else {
			task = new Task(TaskTypeEnum.EXPORT, episode.getVideoUrl());
		}

		Runnable job = new Runnable() {
			@Override
			public void run() {
				boolean success = true;
				final Collection<Task> taskList = new LinkedList<>();
				for (final Exporter exporter : exporterList) {
					try {
						listener.exportEpisode(episode, exporter, "");
						episodeExporter.export(exporter.getCmd(), exporterFactory.findPlugin(exporter.getName(), HabitTvConf.DEFAULT_EXPORTER),
								new CmdProgressionListener() {
									@Override
									public void listen(final String progression) {
										listener.exportEpisode(episode, exporter, progression);
									}
								});
					} catch (ExecutorFailedException e) {
						listener.exportFailed(episode, exporter, e);
						success = false;
						break;
					}
					if (!exporter.getExporter().isEmpty()) {
						Task subExportTask = buildExportTask(episode, exporter.getExporter(), episodeExporter, filesDAO, false);
						taskMgr.addTask(subExportTask);
						taskList.add(subExportTask);
					}
				}

				task.setSuccess((success));
				if (rootCall) {
					taskMgr.waitForEndTasks(config.getAllDownloadTimeout(), TaskTypeEnum.EXPORT);
					task.setSuccess((success && isAllTaskSuccess(taskList)));
					if (task.isSuccess()) {
						listener.episodeReady(episode);
					}
				}
			}

		};
		task.setJob(job);
		return task;
	}

	private boolean isAllTaskSuccess(final Collection<Task> taskList) {
		boolean ret = true;
		for (final Task task : taskList) {
			if (!task.isSuccess()) {
				ret = false;
				break;
			}
		}
		return ret;
	}

	@Override
	public void run() {
		listener.providerDownloadCheckStarted(provider);
		downloadEpisode(categoryList);
		listener.providerDownloadCheckDone(provider);
	}

}
