package com.dabi.habitv.process.task;

import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.api.PluginExporterInterface;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.dto.ExportDTO;
import com.dabi.habitv.framework.plugin.exception.ExportFailedException;
import com.dabi.habitv.process.publisher.EpisodeStateEnum;
import com.dabi.habitv.process.publisher.Publisher;
import com.dabi.habitv.process.publisher.RetreiveEvent;

public class ExportTask extends AbstractEpisodeTask {

	private final ExportDTO export;

	private final PluginExporterInterface pluginExporter;

	private final Publisher<RetreiveEvent> publisher;

	public ExportTask(final EpisodeDTO episode, final ExportDTO export, final PluginExporterInterface pluginExporter, final Publisher<RetreiveEvent> publisher) {
		super(episode);
		this.export = export;
		this.pluginExporter = pluginExporter;
		this.publisher = publisher;
	}

	@Override
	protected void added() {
		publisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.TO_EXPORT));
	}

	@Override
	protected void failed() {
		publisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.EXPORT_FAILED));
	}

	@Override
	protected void ended() {
	}

	@Override
	protected void started() {
		publisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.EXPORTING));
	}

	@Override
	protected Object doCall() throws ExportFailedException {
		final String cmd = TokenReplacer.replaceAll(export.getCmd(), getEpisode());
		pluginExporter.export(cmd, new CmdProgressionListener() {
			@Override
			public void listen(final String progression) {
				publisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.EXPORTING, progression));
			}
		});
		return null;
	}

}
