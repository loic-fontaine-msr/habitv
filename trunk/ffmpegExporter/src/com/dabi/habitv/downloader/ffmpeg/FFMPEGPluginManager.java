package com.dabi.habitv.downloader.ffmpeg;

import java.util.Map;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.ProxyDTO;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class FFMPEGPluginManager implements PluginDownloaderInterface {

	@Override
	public String getName() {
		return FFMPEGConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		// no need
	}

	@Override
	public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters,
			final CmdProgressionListener listener, final Map<ProxyDTO.ProtocolEnum, ProxyDTO> proxies) throws DownloadFailedException {

		final String downloaderBin = parameters.get(FrameworkConf.PARAMETER_BIN_PATH);
		if (downloaderBin == null) {
			throw new IllegalArgumentException("bin path parameters must be defined");
		}
		String cmd = downloaderBin + FFMPEGConf.FFMPEG_CMD;
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_INPUT, downloadInput);
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_DESTINATION, downloadDestination);
		cmd = cmd.replaceFirst(FrameworkConf.EXTENSION, parameters.get(FrameworkConf.EXTENSION));

		try {
			(new FFMPEGCmdExecutor(parameters.get(FrameworkConf.CMD_PROCESSOR), cmd, listener)).execute();
		} catch (final ExecutorFailedException e) {
			throw new DownloadFailedException(e);
		}
	}

}
