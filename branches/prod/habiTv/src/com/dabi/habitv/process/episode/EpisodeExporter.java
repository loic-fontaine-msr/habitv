package com.dabi.habitv.process.episode;

import com.dabi.habitv.config.entities.Downloader;
import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.api.ExporterPluginInterface;
import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.utils.FileUtils;

public class EpisodeExporter {

	private final String channel;

	private final CategoryDTO category;

	private final EpisodeDTO episode;

	public EpisodeExporter(final String channel, final CategoryDTO category, final EpisodeDTO episode) {
		super();
		this.channel = channel;
		this.category = category;
		this.episode = episode;
	}

	public void export(final String exportCmd, final ExporterPluginInterface pluginExporter, final CmdProgressionListener listener)
			throws ExecutorFailedException {
		final String cmd = replaceToken(exportCmd);

		pluginExporter.export(cmd, listener);
	}

	private String replaceToken(final String cmd) {
		// EPISODE
		final String episodeName = ensure(episode.getName());
		String cmdReturn = cmd.replaceAll("#EPISODE_NAME#", ensure(episodeName));

		if (episodeName.length() > 0) {
			cmdReturn = cmdReturn.replaceAll("#EPISODE_NAME_CUT#", ensure(episodeName.substring(0, Math.min(40, episodeName.length() - 1))));

			String episodeNameNoScore = episodeName.replaceAll("(\\d\\s*-\\s*\\d)", "").replaceAll("(\\d_*-_*\\d)", "");
			cmdReturn = cmdReturn.replaceAll("#EPISODE_NAME_NOSCORE#", ensure(episodeNameNoScore.substring(0, Math.min(40, episodeNameNoScore.length() - 1))));
		}

		if (episode.getVideoUrl() != null) {
			cmdReturn = cmdReturn.replaceAll("#VIDEO_URL#", episode.getVideoUrl());
		}
		cmdReturn = cmdReturn.replaceAll("#CATEGORY#", ensure(ensure(episode.getCategory())));

		// TV SHOW
		cmdReturn = cmdReturn.replaceAll("#TVSHOW_NAME#", ensure(ensure(category.getName())));

		// CHANNEL
		cmdReturn = cmdReturn.replaceAll("#CHANNEL_NAME#", ensure(channel));

		return cmdReturn;
	}

	private static String ensure(final String string) {
		return FileUtils.sanitizeFilename(string);
	}

	public void download(final Downloader downloader, final PluginDownloaderInterface pluginDownloader, final String downloadParam, final String fileDest,
			final CmdProgressionListener listener) throws ExecutorFailedException {

		final String param = replaceToken(downloadParam.replaceAll("#FILE_DEST#", fileDest));
		pluginDownloader.download(downloader.getCmd() + " " + param, listener);
	}

}
