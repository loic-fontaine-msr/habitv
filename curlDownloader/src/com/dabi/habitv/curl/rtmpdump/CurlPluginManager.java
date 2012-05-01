package com.dabi.habitv.curl.rtmpdump;

import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;

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
	public void download(final String cmd, final CmdProgressionListener listener) throws DownloadFailedException {
		try {
			(new CurlCmdExecutor(cmd, listener)).execute();
		} catch (ExecutorFailedException e) {
			throw new DownloadFailedException(e);
		}
	}

}
