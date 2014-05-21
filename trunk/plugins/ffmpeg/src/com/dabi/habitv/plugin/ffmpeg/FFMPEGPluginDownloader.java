package com.dabi.habitv.plugin.ffmpeg;

import java.util.regex.Pattern;

import com.dabi.habitv.api.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.update.BaseUpdatablePlugin;

public class FFMPEGPluginDownloader extends BaseUpdatablePlugin implements
		PluginDownloaderInterface {

	private static final Pattern VERSION_PATTERN = Pattern
			.compile("ffmpeg version ([\\-0-9A-Za-z.-]*).*");

	@Override
	public String getName() {
		return FFMPEGConf.NAME;
	}

	@Override
	public ProcessHolder download(final DownloadParamDTO downloadParam,
			final DownloaderPluginHolder downloaders)
			throws DownloadFailedException {

		final String downloaderBin = getBinParam(downloaders);
		String cmd = downloaderBin + FFMPEGConf.FFMPEG_CMD;
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_INPUT,
				downloadParam.getDownloadInput());
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_DESTINATION,
				downloadParam.getDownloadOutput());
		cmd = cmd.replaceFirst(FrameworkConf.EXTENSION,
				downloadParam.getExtension());

		try {
			return (new FFMPEGCmdExecutor(downloaders.getCmdProcessor(), cmd));
		} catch (final ExecutorFailedException e) {
			throw new DownloadFailedException(e);
		}
	}

	@Override
	protected Pattern getVersionPattern() {
		return VERSION_PATTERN;
	}

	@Override
	protected String getVersionParam() {
		return " -version";
	}

	@Override
	public DownloadableState canDownload(final String downloadInput) {
		return downloadInput.endsWith(FrameworkConf.M3U8) ? DownloadableState.SPECIFIC
				: DownloadableState.IMPOSSIBLE;
	}

}
