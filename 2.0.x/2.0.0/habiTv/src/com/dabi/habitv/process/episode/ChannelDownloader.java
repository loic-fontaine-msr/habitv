package com.dabi.habitv.process.episode;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.dabi.habitv.config.HabitTvConf;
import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.config.entities.Exporter;
import com.dabi.habitv.dldao.DownloadedDAO;
import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.api.PluginExporterInterface;
import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloadersDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.exception.InvalidEpisodeException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
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

	private final PluginProviderInterface provider;

	private final PluginFactory<PluginExporterInterface> exporterFactory;

	private final DownloadersDTO downloaders;

	private final ProcessEpisodeListener listener;

	private final String channel;

	public ChannelDownloader(final String channel, final TaskMgr taskMgr, final ProcessEpisodeListener listener, final List<CategoryDTO> categoryList,
			final Config config, final PluginProviderInterface provider, final PluginFactory<PluginExporterInterface> exporterFactory,
			final DownloadersDTO downloaders) {
		this.taskMgr = taskMgr;
		this.listener = listener;
		this.config = config;
		this.categoryList = categoryList;
		this.provider = provider;
		this.exporterFactory = exporterFactory;
		this.downloaders = downloaders;
		this.channel = channel;
	}

	private void downloadEpisode(final List<CategoryDTO> categoryDTOs) {
		for (final CategoryDTO category : categoryDTOs) {
			if (!category.getSubCategories().isEmpty()) {
				downloadEpisode(category.getSubCategories());
			} else {
				// dao to find dowloaded episodes
				final DownloadedDAO filesDAO = new DownloadedDAO(config.getWorkingDir(), provider.getName(), category.getName(), config.getIndexDir());
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
					if (filesDAO.isIndexCreated()) {
						// producer download the file
						final Task dlTask = buildDownloadAndExportTask(episode, filesDAO);
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

	private Task buildDownloadAndExportTask(final EpisodeDTO episode, final DownloadedDAO filesDAO) {
		final Runnable job = new Runnable() {
			@Override
			public void run() {
				try {
					episode.check();
				} catch (final InvalidEpisodeException e) {
					throw new TechnicalException(e);
				}

				try {
					provider.download(TokenReplacer.replaceAll(config.getDownloadOuput(), episode), downloaders, new CmdProgressionListener() {
						@Override
						public void listen(final String progression) {
							listener.downloadingEpisode(episode, progression);
						}
					}, episode);
					listener.downloadedEpisode(episode);
					filesDAO.addDownloadedFiles(episode.getName());

					// add the export thread
					taskMgr.addTask(buildExportTask(episode, config.getExporter(), filesDAO, null));
				} catch (final DownloadFailedException e) {
					listener.downloadFailed(episode, e);
				} catch (final NoSuchDownloaderException e) {
					// TODO afficher message
					throw new TechnicalException(e);
				}
			}
		};
		return new Task(TaskTypeEnum.DOWNLOAD, channel, episode.getVideoUrl(), job);
	}

	private Task buildExportTask(final EpisodeDTO episode, final List<Exporter> exporterList, final DownloadedDAO filesDAO, final String subExportTask) {
		final Task task;
		if (subExportTask == null) {
			task = new Task(TaskTypeEnum.EXPORT_MAIN, episode.getVideoUrl());
		} else {
			// task = new Task(TaskTypeEnum.EXPORT, subExportTask,
			// episode.getVideoUrl()); FIXME should be
			task = new Task(TaskTypeEnum.EXPORT, episode.getVideoUrl(), episode.getVideoUrl());
		}

		final Runnable job = new Runnable() {
			@Override
			public void run() {
				boolean success = true;
				final Collection<Task> taskList = new LinkedList<>();
				for (final Exporter exporter : exporterList) {
					if (validCondition(exporter, episode)) {
						try {
							listener.exportEpisode(episode, exporter, "");
							final String cmd = TokenReplacer.replaceAll(exporter.getCmd(), episode);
							final PluginExporterInterface pluginexporter = exporterFactory.findPlugin(exporter.getName(), HabitTvConf.DEFAULT_EXPORTER);
							pluginexporter.export(cmd, new CmdProgressionListener() {
								@Override
								public void listen(final String progression) {
									listener.exportEpisode(episode, exporter, progression);
								}
							});
						} catch (final ExecutorFailedException e) {
							listener.exportFailed(episode, exporter, e);
							success = false;
							break;
						}
						if (!exporter.getExporter().isEmpty()) {
							final Task subExportTask = buildExportTask(episode, exporter.getExporter(), filesDAO, exporter.getName());
							taskMgr.addTask(subExportTask);
							taskList.add(subExportTask);
						}
					}
				}

				task.setSuccess((success));
				if (subExportTask == null) {// FIXME le thread principal attend
											// et bloque un
					// thread sans faire de traitement
					// FIXME termine le pool alors qu'autres export ne pas avoir
					// été ajouté
					taskMgr.waitForEndTasks(config.getAllDownloadTimeout(), TaskTypeEnum.EXPORT, episode.getVideoUrl());
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

	private boolean validCondition(final Exporter exporter, final EpisodeDTO episode) {
		boolean ret = true;
		if (exporter.getCondition() != null) {
			final String reference = exporter.getCondition().getReference();
			final String actualString = TokenReplacer.replaceRef(reference, episode);
			ret = actualString.matches(exporter.getCondition().getPattern());
		}
		return ret;
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
