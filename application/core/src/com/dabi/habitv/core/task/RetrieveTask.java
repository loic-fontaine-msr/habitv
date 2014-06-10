package com.dabi.habitv.core.task;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.dabi.habitv.api.plugin.api.PluginExporterInterface;
import com.dabi.habitv.api.plugin.api.PluginProviderInterface;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.dto.ExportDTO;
import com.dabi.habitv.api.plugin.exception.InvalidEpisodeException;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.holder.ExporterPluginHolder;
import com.dabi.habitv.api.plugin.pub.Publisher;
import com.dabi.habitv.core.config.HabitTvConf;
import com.dabi.habitv.core.dao.DownloadedDAO;
import com.dabi.habitv.core.dao.EpisodeExportState;
import com.dabi.habitv.core.event.EpisodeStateEnum;
import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.token.TokenReplacer;

public class RetrieveTask extends AbstractEpisodeTask {

	private final Publisher<RetreiveEvent> retreivePublisher;

	private final TaskAdder taskAdder;

	private final ExporterPluginHolder exporter;

	private final PluginProviderInterface provider;

	private final DownloaderPluginHolder downloaders;

	private final DownloadedDAO downloadDAO;

	private EpisodeExportState episodeExportState;

	public RetrieveTask(final EpisodeDTO episode,
			final Publisher<RetreiveEvent> publisher,
			final TaskAdder taskAdder, final ExporterPluginHolder exporter,
			final PluginProviderInterface provider,
			final DownloaderPluginHolder downloaders,
			final DownloadedDAO downloadDAO) {
		super(episode);
		this.retreivePublisher = publisher;
		this.taskAdder = taskAdder;
		this.exporter = exporter;
		this.provider = provider;
		this.downloadDAO = downloadDAO;
		this.downloaders = downloaders;
		this.episodeExportState = null;
	}

	@Override
	protected void adding() {
		LOG.info("Episode to retreive " + getEpisode());
		retreivePublisher.addNews(new RetreiveEvent(getEpisode(),
				EpisodeStateEnum.TO_DOWNLOAD));
	}

	@Override
	protected void failed(final Throwable e) {
		LOG.error("Episode failed to retreive " + getEpisode(), e);
	}

	@Override
	protected void ended() {
		LOG.error("Episode is ready " + getEpisode());
		retreivePublisher.addNews(new RetreiveEvent(getEpisode(),
				EpisodeStateEnum.READY));
	}

	@Override
	protected void started() {

	}

	@Override
	protected Object doCall() throws InterruptedException, ExecutionException {
		if (!exportOnly()) {
			check();
			download();
		}
		export(exporter.getExporterList());
		return null;
	}

	private boolean exportOnly() {
		return episodeExportState != null;
	}

	private void export(final List<ExportDTO> exporterList)
			throws InterruptedException, ExecutionException {
		int i = 0;
		for (final ExportDTO export : exporterList) {
			if (validCondition(export, getEpisode()) && episodeExportResume(i)) {
				final PluginExporterInterface pluginexporter = exporter
						.getPlugin(export.getName(),
								HabitTvConf.DEFAULT_EXPORTER);

				final ExportTask exportTask = new ExportTask(getEpisode(),
						export, pluginexporter, retreivePublisher, i);
				taskAdder.addExportTask(exportTask, export.getName());
				// wait for the current exportTask before running an other
				exportTask.waitEndOfTreatment();
				// sub export tasks are run asynchronously
				if (!export.getExporter().isEmpty()) {
					export(export.getExporter());
				}
			}
			i++;
		}
	}

	private boolean episodeExportResume(final int i) {
		// soit il n'y a pas à reprendre soit il faut reprendre et c'est l'étape
		// à reprendre
		return episodeExportState == null || i >= episodeExportState.getState();
	}

	private boolean validCondition(final ExportDTO export,
			final EpisodeDTO episode) {
		boolean ret = true;
		if (export.getConditionReference() != null) {
			final String reference = export.getConditionReference();
			final String actualString = TokenReplacer.replaceAll(reference,
					episode);
			ret = actualString.matches(export.getConditionPattern());
		}
		return ret;
	}

	private void download() {
		final DownloadTask downloadTask = new DownloadTask(getEpisode(),
				provider, downloaders, retreivePublisher, downloadDAO);
		taskAdder.addDownloadTask(downloadTask, getEpisode().getCategory()
				.getPlugin());
		downloadTask.waitEndOfTreatment();
	}

	private void check() {
		try {
			getEpisode().check();
		} catch (final InvalidEpisodeException e) {
			throw new TechnicalException(e);
		}
	}

	@Override
	public String toString() {
		return "Retreiving" + getEpisode().toString();
	}

	public void setEpisodeExportState(
			final EpisodeExportState episodeExportState) {
		this.episodeExportState = episodeExportState;
	}
}
