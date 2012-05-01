package com.dabi.habitv.launcher.tray.model;

import java.util.Observable;

import javax.swing.event.EventListenerList;

import com.dabi.habitv.config.ConfigAccess;
import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.grabconfig.entities.GrabConfig;
import com.dabi.habitv.launcher.tray.EpisodeChangedEvent;
import com.dabi.habitv.launcher.tray.HabiTvListener;
import com.dabi.habitv.launcher.tray.ProcessChangedEvent;
import com.dabi.habitv.process.episode.ProcessEpisodeListener;
import com.dabi.habitv.process.episode.RetrieveAndExport;

public class HabitTvTrayModel extends Observable {

	private final RetrieveAndExport retrieveAndExport;

	private final Config config = ConfigAccess.initConfig();

	private final GrabConfig grabConfig = ConfigAccess.initGrabConfig();

	private EventListenerList listeners;

	private final ProgressionModel progressionModel;

	public HabitTvTrayModel() {
		retrieveAndExport = new RetrieveAndExport();
		progressionModel = new ProgressionModel();
		listeners = new EventListenerList();

	}

	protected RetrieveAndExport getRetrieveAndExport() {
		return retrieveAndExport;
	}

	protected Config getConfig() {
		return config;
	}

	protected GrabConfig getGrabConfig() {
		return grabConfig;
	}

	public ProgressionModel getProgressionModel() {
		return progressionModel;
	}

	public void addListener(HabiTvListener listener) {
		listeners.add(HabiTvListener.class, listener);
	}

	public void removeListener(HabiTvListener listener) {
		listeners.remove(HabiTvListener.class, listener);
	}

	public void fireProcessChanged(ProcessStateEnum processStateEnum, String info) {
		HabiTvListener[] listenerList = (HabiTvListener[]) listeners.getListeners(HabiTvListener.class);

		for (HabiTvListener listener : listenerList) {
			listener.processChanged(new ProcessChangedEvent(this, processStateEnum, info));
		}
	}

	public void fireEpisodeChanged(EpisodeStateEnum episodeStateEnum, EpisodeDTO episode) {
		HabiTvListener[] listenerList = (HabiTvListener[]) listeners.getListeners(HabiTvListener.class);

		for (HabiTvListener listener : listenerList) {
			listener.episodeChanged(new EpisodeChangedEvent(this, episode, episodeStateEnum));
		}
	}

	public void startDownloadCheck(final ProcessEpisodeListener listener) {
		(new Thread() {

			@Override
			public void run() {
				retrieveAndExport.execute(config, grabConfig, listener);
			}

		}).start();
	}

}
