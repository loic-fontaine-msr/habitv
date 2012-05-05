package com.dabi.habitv.downloader.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

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
	public void download(final String cmd, final CmdProgressionListener listener) throws DownloadFailedException {
		try {
			HttpDownload download = new HttpDownload(new URL(cmd));
			download.addObserver(new Observer() {
				
				@Override
				public void update(Observable o, Object arg) {
					// TODO Auto-generated method stub
					
				}
			});
			download.run();
		} catch (MalformedURLException e) {
			throw new TechnicalException(e);
		}
	}

}
