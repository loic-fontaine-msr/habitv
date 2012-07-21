package com.dabi.habitv.process.task;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.dabi.habitv.config.HabitTvConf;
import com.dabi.habitv.dldao.DownloadedDAO;
import com.dabi.habitv.framework.plugin.api.PluginExporterInterface;
import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.dto.ExportDTO;
import com.dabi.habitv.framework.plugin.api.dto.ExporterDTO;
import com.dabi.habitv.framework.plugin.exception.InvalidEpisodeException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.process.publisher.EpisodeStateEnum;
import com.dabi.habitv.process.publisher.Publisher;
import com.dabi.habitv.process.publisher.RetreiveEvent;

public class RetreiveTask extends AbstractEpisodeTask {

	private final Publisher<RetreiveEvent> retreivePublisher;

	private final TaskAdder taskAdder;

	private final ExporterDTO exporter;

	private final PluginProviderInterface provider;

	private final DownloaderDTO downloader;

	private final DownloadedDAO downloadDAO;

	public RetreiveTask(final EpisodeDTO episode, final Publisher<RetreiveEvent> publisher, final TaskAdder taskAdder, final ExporterDTO exporter,
			final PluginProviderInterface provider, final DownloaderDTO downloader) {
		super(episode);
		retreivePublisher = publisher;
		this.taskAdder = taskAdder;
		this.exporter = exporter;
		this.provider = provider;
		downloadDAO = new DownloadedDAO(provider.getName(), episode.getName(), downloader.getIndexDir());
		this.downloader = downloader;
	}

	@Override
	protected void added() {
		retreivePublisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.TO_DOWNLOAD));
	}

	@Override
	protected void failed() {
		retreivePublisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.FAILED));
	}

	@Override
	protected void ended() {
		retreivePublisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.READY));
	}

	@Override
	protected void started() {

	}

	@Override
	protected Object doCall() throws InterruptedException, ExecutionException {
		check();
		download();
		export(exporter.getExporterList());
		return null;
	}

	private void export(final List<ExportDTO> exporterList) throws InterruptedException, ExecutionException {
		for (final ExportDTO export : exporterList) {
			if (validCondition(export, getEpisode())) {

				final PluginExporterInterface pluginexporter = exporter.getExporter(export.getName(), HabitTvConf.DEFAULT_EXPORTER);
				final Future<Object> futureExport = taskAdder.addExportTask(new ExportTask(getEpisode(), export, pluginexporter, retreivePublisher),
						export.getName());
				// sub export tasks are run asynchronously
				if (!export.getExporter().isEmpty()) {
					export(export.getExporter());
				}
				// wait for the current exportTask before running an other
				futureExport.get();// TODO timeout ?
			}
		}
	}

	private boolean validCondition(final ExportDTO export, final EpisodeDTO episode) {
		boolean ret = true;
		if (export.getConditionReference() != null) {
			final String reference = export.getConditionReference();
			final String actualString = TokenReplacer.replaceRef(reference, episode);
			ret = actualString.matches(export.getConditionReference());
		}
		return ret;
	}

	private void download() throws InterruptedException, ExecutionException {
		final Future<Object> futureDl = taskAdder.addDownloadTask(new DownloadTask(getEpisode(), provider, downloader, retreivePublisher, downloadDAO));
		futureDl.get();
	}

	private void check() {
		try {
			getEpisode().check();
		} catch (final InvalidEpisodeException e) {
			throw new TechnicalException(e);
		}
	}
}
