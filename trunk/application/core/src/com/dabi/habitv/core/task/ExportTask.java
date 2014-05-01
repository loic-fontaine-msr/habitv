package com.dabi.habitv.core.task;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.api.PluginExporterInterface;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.dto.ExportDTO;
import com.dabi.habitv.api.plugin.exception.ExportFailedException;
import com.dabi.habitv.api.plugin.pub.Publisher;
import com.dabi.habitv.core.event.EpisodeStateEnum;
import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.token.TokenReplacer;

public class ExportTask extends AbstractEpisodeTask {

	private final ExportDTO export;

	private final PluginExporterInterface pluginExporter;

	private final Publisher<RetreiveEvent> publisher;

	private final int rank;

	public ExportTask(final EpisodeDTO episode, final ExportDTO export, final PluginExporterInterface pluginExporter, final Publisher<RetreiveEvent> publisher,
			final int rank) {
		super(episode);
		this.export = export;
		this.pluginExporter = pluginExporter;
		this.publisher = publisher;
		this.rank = rank;
	}

	@Override
	protected void added() {
		LOG.info("Episode to export " + getEpisode() + " " + export.getName());
		publisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.TO_EXPORT));
	}

	@Override
	protected void failed(final Throwable e) {
		LOG.error("Episode failed to export " + getEpisode() + " " + export.getName(), e);
		publisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.EXPORT_FAILED, e, export.getOutput()));
	}

	@Override
	protected void ended() {
		LOG.error("Episode export ended" + getEpisode() + " " + export.getName());
	}

	@Override
	protected void started() {
		LOG.error("Episode export starting" + getEpisode() + " " + export.getName());
		publisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.EXPORTING, export.getOutput(), null));
	}

	@Override
	protected Object doCall() throws ExportFailedException {
		final String cmd = TokenReplacer.replaceAll(export.getCmd(), getEpisode());
		pluginExporter.export(export.getCmdProcessor(), cmd, new CmdProgressionListener() {
			@Override
			public void listen(final String progression) {
				publisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.EXPORTING, export.getOutput(), progression));
			}
		});
		return null;
	}

	@Override
	public int hashCode() {
		return getEpisode().hashCode() + export.hashCode() + export.hashCode() + publisher.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		boolean ret;
		if (obj instanceof ExportTask) {
			final ExportTask exportTask = (ExportTask) obj;
			ret = getEpisode().equals(exportTask.getEpisode());
			if (export.getCmd() != null) {
				ret = ret && export.getCmd().equals(export.getCmd());
			}
		} else {
			ret = false;
		}
		return ret;
	}

	@Override
	public String toString() {
		return getEpisode() + " " + export.getName() + " " + pluginExporter.getName();
	}

	public int getRank() {
		return rank;
	}

}
