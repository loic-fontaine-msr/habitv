package com.dabi.habitv.downloader.curl;

import java.util.Map;

import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.framework.FrameworkConf;

public class CurlPluginManager implements PluginDownloaderInterface {

	@Override
	public String getName() {
		return CurlConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		// no need
	}

	@Override
	public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters,
			final CmdProgressionListener listener) throws DownloadFailedException {

		String downloaderBin = parameters.get(FrameworkConf.PARAMETER_BIN_PATH);
		if (downloaderBin == null) {
			throw new IllegalArgumentException("bin path parameters must be defined");
		}
		String cmd = downloaderBin + CurlConf.CURL_CMD;
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_INPUT, downloadInput);
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_DESTINATION, downloadDestination);
		try {
			(new CurlCmdExecutor(parameters.get(FrameworkConf.CMD_PROCESSOR), cmd, listener)).execute();
		} catch (ExecutorFailedException e) {
			throw new DownloadFailedException(e);
		}
	}

}
