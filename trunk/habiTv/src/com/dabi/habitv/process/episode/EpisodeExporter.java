package com.dabi.habitv.process.episode;

import java.util.HashMap;
import java.util.Map;

import com.dabi.habitv.config.entities.Downloader;
import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.api.PluginExporterInterface;
import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.utils.FrameworkConf;

public class EpisodeExporter {

	private final EpisodeDTO episode;

	public EpisodeExporter(final EpisodeDTO episode) {
		super();
		this.episode = episode;
	}

	public void export(final String exportCmd, final PluginExporterInterface pluginExporter, final CmdProgressionListener listener)
			throws ExecutorFailedException {
		final String cmd = replaceAllToken(exportCmd);

		pluginExporter.export(cmd, listener);
	}

	private String replaceAllToken(final String cmd) {
		return TokenReplacer.replaceAll(cmd, episode);
	}

	public void download(final Downloader downloader, final PluginDownloaderInterface pluginDownloader, final String downloadParam, final String videoUrl,
			final String fileDest, final CmdProgressionListener listener) throws ExecutorFailedException {

		final Map<String, String> parameters = new HashMap<>();
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloader.getBinPath());
		parameters.put(FrameworkConf.PARAMETER_ARGS, downloadParam);

		pluginDownloader.download(videoUrl, replaceAllToken(fileDest), parameters, listener);
	}

}
