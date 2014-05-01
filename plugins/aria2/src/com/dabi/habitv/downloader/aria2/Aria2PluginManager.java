package com.dabi.habitv.downloader.aria2;

import java.util.Map;
import java.util.regex.Pattern;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.api.plugin.dto.ProxyDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.update.BaseUpdatablePlugin;

public class Aria2PluginManager extends BaseUpdatablePlugin implements
		PluginDownloaderInterface {

	private static final Pattern VERSION_PATTERN = Pattern
			.compile("aria2 version ([0-9A-Za-z.-]*).*");

	@Override
	public String getName() {
		return Aria2Conf.NAME;
	}

	@Override
	public void download(final String downloadInput,
			final String downloadDestination,
			final Map<String, String> parameters,
			final CmdProgressionListener listener,
			final Map<ProxyDTO.ProtocolEnum, ProxyDTO> proxies)
			throws DownloadFailedException {

		String binParam = getBinParam(parameters);
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
				cmd += " --all-proxy='http://" + httpProxy.getHost() + ":"
						+ httpProxy.getPort() + "'";
			}
		}
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_INPUT, downloadInput);

		final int lastSlashIndex = downloadDestination.lastIndexOf('/');
		final String fileName = downloadDestination.substring(
				lastSlashIndex + 1, downloadDestination.length());
		final String dirDest = downloadDestination.substring(0, lastSlashIndex);

		cmd = cmd.replaceFirst(Aria2Conf.FILE_NAME, fileName);
		cmd = cmd.replaceFirst(Aria2Conf.DIR_DEST, dirDest);
		try {
			(new Aria2CmdExecutor(parameters.get(FrameworkConf.CMD_PROCESSOR),
					cmd, listener)).execute();
		} catch (final ExecutorFailedException e) {
			throw new DownloadFailedException(e);
		}
	}

	@Override
	protected String getLinuxDefaultBuildPath() {
		return Aria2Conf.DEFAULT_LINUX_BIN_PATH;
	}

	@Override
	protected String getWindowsDefaultBuildPath() {
		return Aria2Conf.DEFAULT_WINDOWS_BIN_PATH;
	}

	@Override
	protected Pattern getVersionPattern() {
		return VERSION_PATTERN;
	}

	@Override
	protected String[] getFilesToUpdate() {
		return new String[]{"aria2c"};
	}
	
	protected String getVersionParam() {
		return " -v";
	}	

}
