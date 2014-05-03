package com.dabi.habitv.downloader.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;

public class HttpDownloadPluginManager implements PluginDownloaderInterface {

	@Override
	public String getName() {
		return HttpConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		// no need
	}

	@Override
	public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters,
			final CmdProgressionListener listener) throws DownloadFailedException {
		try {
			HttpDownload download = new HttpDownload(new URL(downloadInput), downloadDestination);
			download.addObserver(new DlObserverListener(listener));
			download.run();
		} catch (MalformedURLException e) {
			throw new TechnicalException(e);
		}
	}

}