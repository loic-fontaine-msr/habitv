package com.dabi.habitv.downloader.http;

import java.util.Observable;
import java.util.Observer;

import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.utils.FrameworkConf;

public class DlObserverListener implements Observer {

	private final CmdProgressionListener listener;

	public DlObserverListener(final CmdProgressionListener listener) {
		this.listener = listener;
	}

	private long lastTime = 0;

	@Override
	public void update(final Observable observable, final Object arg) {
		final HttpDownload dl = (HttpDownload) observable;

		final String progression = String.valueOf(Math.round(dl.getProgress()));
		if (listener != null && (System.currentTimeMillis() - lastTime) > FrameworkConf.TIME_BETWEEN_LOG) {
			listener.listen(progression);
			lastTime = System.currentTimeMillis();
		}
	}

}
