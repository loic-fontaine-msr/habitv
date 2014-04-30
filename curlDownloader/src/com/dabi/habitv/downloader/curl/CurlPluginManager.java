package com.dabi.habitv.downloader.curl;

import java.util.Map;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.ProxyDTO;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.utils.OSUtils;

public class CurlPluginManager implements PluginDownloaderInterface {

	@Override
	public String getName() {
		return CurlConf.NAME;
	}

	@Override
	public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters,
			final CmdProgressionListener listener, final Map<ProxyDTO.ProtocolEnum, ProxyDTO> proxies) throws DownloadFailedException {

		String downloaderBin = parameters.get(FrameworkConf.PARAMETER_BIN_PATH);
		if (downloaderBin == null) {
			if (OSUtils.isWindows()){
				downloaderBin = CurlConf.DEFAULT_WINDOWS_BIN_PATH;
			} else {
				downloaderBin = CurlConf.DEFAULT_LINUX_BIN_PATH;
			}
		}
		String cmd = downloaderBin + CurlConf.CURL_CMD;
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_INPUT, downloadInput);
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_DESTINATION, downloadDestination);

		if (proxies != null) {
			final ProxyDTO httpProxy = proxies.get(ProxyDTO.ProtocolEnum.HTTP);
			if (httpProxy != null) {
				cmd += "--proxy " + httpProxy.getHost() + ":" + httpProxy.getPort();
			}
		}

		try {
			(new CurlCmdExecutor(parameters.get(FrameworkConf.CMD_PROCESSOR), cmd, listener)).execute();
		} catch (final ExecutorFailedException e) {
			throw new DownloadFailedException(e);
		}
	}

}
