package com.dabi.habitv.downloader.http;

import java.util.Observable;
import java.util.Observer;

import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;

public class DlObserverListener implements Observer {

	private final CmdProgressionListener listener;

	public DlObserverListener(final CmdProgressionListener listener) {
		this.listener = listener;
	}

	private String progression;
	
	private long lastTime = 0;

	@Override
	public void update(Observable o, Object arg) {
		HttpDownload dl = (HttpDownload) o;
		progression = String.valueOf(Math.round(dl.getProgress()));
		if (listener != null && (System.currentTimeMillis() - lastTime ) > 2000) {//FIXME en conf
			listener.listen(progression);
			lastTime = System.currentTimeMillis();
		}
	}

}
