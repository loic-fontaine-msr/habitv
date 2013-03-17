package com.dabi.habitv.downloader.rtmpdump;

import java.util.Map;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.ProxyDTO;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class RtmpDumpPluginManager implements PluginDownloaderInterface {

	@Override
	public String getName() {
		return RtmpDumpConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		// no need
	}

	@Override
	public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters,
			final CmdProgressionListener listener, final Map<ProxyDTO.ProtocolEnum, ProxyDTO> proxies) throws DownloadFailedException {

		final String binParam = parameters.get(FrameworkConf.PARAMETER_BIN_PATH);
		if (binParam == null) {
			throw new IllegalArgumentException("bin path parameters must be defined");
		}
		String cmd = binParam + " ";
		final String cmdParam = parameters.get(FrameworkConf.PARAMETER_ARGS);
		if (cmdParam == null) {
			cmd += RtmpDumpConf.DUMP_CMD;
		} else {
			cmd += cmdParam;
		}
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_INPUT, downloadInput);
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_DESTINATION, downloadDestination);
		final ProxyDTO sockProxy = proxies.get(ProxyDTO.ProtocolEnum.SOCKS);
		if (sockProxy != null) {
			cmd += "−−socks −S " + sockProxy.getHost() + ":" + sockProxy.getPort() + "";
		}
		try {
			(new RtmpDumpCmdExecutor(parameters.get(FrameworkConf.CMD_PROCESSOR), cmd, listener)).execute();
		} catch (final ExecutorFailedException e) {
			throw new DownloadFailedException(e);
		}
	}

}
