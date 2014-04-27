package com.dabi.habitv.downloader.ffmpeg;

import java.util.Arrays;
import java.util.Map;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.ProxyDTO;
import com.dabi.habitv.framework.plugin.api.update.BaseUpdatablePlugin;
import com.dabi.habitv.framework.plugin.api.update.UpdatablePluginInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.utils.OSUtils;

public class FFMPEGPluginManager extends BaseUpdatablePlugin implements PluginDownloaderInterface {

	@Override
	public String getName() {
		return FFMPEGConf.NAME;
	}

	@Override
	public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters,
			final CmdProgressionListener listener, final Map<ProxyDTO.ProtocolEnum, ProxyDTO> proxies) throws DownloadFailedException {

		String downloaderBin = parameters.get(FrameworkConf.PARAMETER_BIN_PATH);
		if (downloaderBin == null) {
			if (OSUtils.isWindows()) {
				downloaderBin = FFMPEGConf.DEFAULT_WINDOWS_BIN_PATH;
			} else {
				downloaderBin = FFMPEGConf.DEFAULT_LINUX_BIN_PATH;
			}
		}
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
	public String getCurrentVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getFilesToUpdate() {
		return Arrays.asList("");
	}

}
