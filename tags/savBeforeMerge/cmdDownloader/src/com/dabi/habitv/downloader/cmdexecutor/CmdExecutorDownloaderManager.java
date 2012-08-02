package com.dabi.habitv.downloader.cmdexecutor;

import java.util.Map;

import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdExecutor;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.utils.FrameworkConf;

public final class CmdExecutorDownloaderManager implements PluginDownloaderInterface {

	@Override
	public String getName() {
		return CmdDownloaderConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		// no need
	}

	@Override
	public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters,
			final CmdProgressionListener listener) throws DownloadFailedException {
		final String cmd = parameters.get("cmd");
		if (cmd == null) {
			throw new IllegalArgumentException("cmd parameters must be defined");
		}
		replaceIfContains(cmd, FrameworkConf.DOWNLOAD_INPUT, downloadInput);
		replaceIfContains(cmd, FrameworkConf.DOWNLOAD_DESTINATION, downloadDestination);

		try {
			new CmdExecutor(cmd, listener).execute();
		} catch (ExecutorFailedException e) {
			throw new DownloadFailedException(e);
		}
	}

	private String replaceIfContains(final String cmd, final String paramPattern, final String param) {
		if (cmd.contains(param)) {
			return cmd.replaceFirst(paramPattern, param);
		} else {
			throw new IllegalArgumentException("cmd param must defined the param pattern " + paramPattern);
		}
	}

}
