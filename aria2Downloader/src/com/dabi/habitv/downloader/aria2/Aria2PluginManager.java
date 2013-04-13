package com.dabi.habitv.downloader.aria2;

import java.util.Map;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.ProxyDTO;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class Aria2PluginManager implements PluginDownloaderInterface { // NO_UCD
	// (unused
	// code)

	@Override
	public String getName() {
		return Aria2Conf.NAME;
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
			cmd += Aria2Conf.CMD;
		} else {
			cmd += cmdParam;
		}
		if (proxies != null) {
			final ProxyDTO httpProxy = proxies.get(ProxyDTO.ProtocolEnum.HTTP);
			if (httpProxy != null) {
				cmd += " --all-proxy='http://" + httpProxy.getHost() + ":" + httpProxy.getPort() + "'";
			}
		}
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_INPUT, downloadInput);

		final int lastSlashIndex = downloadDestination.lastIndexOf('/');
		final String fileName = downloadDestination.substring(lastSlashIndex + 1, downloadDestination.length());
		final String dirDest = downloadDestination.substring(0, lastSlashIndex);

		cmd = cmd.replaceFirst(Aria2Conf.FILE_NAME, fileName);
		cmd = cmd.replaceFirst(Aria2Conf.DIR_DEST, dirDest);
		try {
			(new Aria2CmdExecutor(parameters.get(FrameworkConf.CMD_PROCESSOR), cmd, listener)).execute();
		} catch (final ExecutorFailedException e) {
			throw new DownloadFailedException(e);
		}
	}

}
