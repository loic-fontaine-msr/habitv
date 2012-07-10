package com.dabi.habitv.downloader.cmdexecutor;

import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdExecutor;

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
	public void download(final String cmd, final CmdProgressionListener listener) throws DownloadFailedException {
		try {
			new CmdExecutor(cmd, listener).execute();
		} catch (ExecutorFailedException e) {
			throw new DownloadFailedException(e);
		}
	}

}
