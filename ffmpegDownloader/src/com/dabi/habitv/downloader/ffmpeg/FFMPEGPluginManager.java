package com.dabi.habitv.downloader.ffmpeg;

import java.util.Map;
import java.util.regex.Pattern;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.ProxyDTO;
import com.dabi.habitv.framework.plugin.api.update.BaseUpdatablePlugin;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class FFMPEGPluginManager extends BaseUpdatablePlugin implements PluginDownloaderInterface {

	private static final Pattern VERSION_PATTERN = Pattern
			.compile("ffmpeg version ([\\-0-9A-Za-z.-]*).*");

	
	@Override
	public String getName() {
		return FFMPEGConf.NAME;
	}

	@Override
	public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters,
			final CmdProgressionListener listener, final Map<ProxyDTO.ProtocolEnum, ProxyDTO> proxies) throws DownloadFailedException {

		String downloaderBin = getBinParam(parameters);
		String cmd = downloaderBin + FFMPEGConf.FFMPEG_CMD_2;
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_INPUT, downloadInput);
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_DESTINATION, downloadDestination);
		cmd = cmd.replaceFirst(FrameworkConf.EXTENSION, parameters.get(FrameworkConf.EXTENSION));

		try {
			(new FFMPEGCmdExecutor(parameters.get(FrameworkConf.CMD_PROCESSOR), cmd, listener)).execute();
		} catch (final ExecutorFailedException e) {
			throw new DownloadFailedException(e);
		}
	}

	@Override
	protected String getLinuxDefaultBuildPath() {
		return FFMPEGConf.DEFAULT_LINUX_BIN_PATH;
	}

	@Override
	protected String getWindowsDefaultBuildPath() {
		return FFMPEGConf.DEFAULT_WINDOWS_BIN_PATH;
	}

	@Override
	protected Pattern getVersionPattern() {
		return VERSION_PATTERN;
	}
	
	protected String getVersionParam() {
		return " -version";
	}	

}
