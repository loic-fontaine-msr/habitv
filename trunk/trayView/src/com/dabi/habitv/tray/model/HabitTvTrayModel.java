package com.dabi.habitv.tray.model;

import java.util.Observable;

import javax.swing.event.EventListenerList;

import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.core.config.ConfigAccess;
import com.dabi.habitv.core.event.EpisodeStateEnum;
import com.dabi.habitv.core.event.SearchStateEnum;
import com.dabi.habitv.core.mgr.CoreManager;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.grabconfig.entities.GrabConfig;
import com.dabi.habitv.tray.EpisodeChangedEvent;
import com.dabi.habitv.tray.HabiTvListener;
import com.dabi.habitv.tray.ProcessChangedEvent;

public class HabitTvTrayModel extends Observable {

	private final CoreManager coreManager;

	private Config config = ConfigAccess.initConfig();

	private GrabConfig grabConfig = ConfigAccess.initGrabConfig();

	private final EventListenerList listeners;

	private final ProgressionModel progressionModel;

	private Thread demonThread;

	public HabitTvTrayModel() {
		super();
		coreManager = new CoreManager(config, grabConfig);
		progressionModel = new ProgressionModel();
		listeners = new EventListenerList();
	}

	public ProgressionModel getProgressionModel() {
		return progressionModel;
	}

	public void addListener(final HabiTvListener listener) {
		listeners.add(HabiTvListener.class, listener);
	}

	public void removeListener(final HabiTvListener listener) {
		listeners.remove(HabiTvListener.class, listener);
	}

	public void fireProcessChanged(final SearchStateEnum processStateEnum, final String info) {
		final HabiTvListener[] listenerList = listeners.getListeners(HabiTvListener.class);

		for (final HabiTvListener listener : listenerList) {
			listener.processChanged(new ProcessChangedEvent(this, processStateEnum, info));
		}
	}

	public void fireEpisodeChanged(final EpisodeStateEnum episodeStateEnum, final EpisodeDTO episode) {
		final HabiTvListener[] listenerList = listeners.getListeners(HabiTvListener.class);

		for (final HabiTvListener listener : listenerList) {
			listener.episodeChanged(new EpisodeChangedEvent(this, episode, episodeStateEnum));
		}
	}

	public void startDownloadCheckDemon() {

		demonThread = new Thread() {
			@Override
			public void run() {
				boolean interrupted = false;
				final long confDemonTime;
				if (config.getDemonTime() == null) {
					confDemonTime = 1800;
				} else {
					confDemonTime = config.getDemonTime();
				}
				final long demonTime = confDemonTime * 1000L;
				// demon mode
				while (true) {
					if (interrupted) {
						interrupted = false;
					} else {
						if (grabConfig == null) {
							coreManager.findAndSaveCategory();
							(new ProcessCategory()).execute(config, new ProcessCategoryListener() {

								@Override
								public void getProviderCategories(final String providerName) {
									fireProcessChanged(SearchStateEnum.BUILDING_CATEGORIES, providerName);
								}

								@Override
								public void categoriesSaved(final String grabconfigXmlFile) {
									fireProcessChanged(SearchStateEnum.CATEGORIES_BUILD, grabconfigXmlFile);
								}
							});
						} else {
							coreManager.retreiveEpisode();
						}
					}
					try {
						Thread.sleep(demonTime);
					} catch (final InterruptedException e) {
						// may have been interrupted by a manually start
						interrupted = true;
					}
				}
			}

		};

		demonThread.start();
	}

	public void startDownloadCheck() {

		demonThread.interrupt();
		(new Thread() {
			@Override
			public void run() {
				coreManager.retreiveEpisode();
			}

		}).start();
	}

	public void forceEnd() {
		coreManager.forceEnd();
	}

	public void clear() {
		progressionModel.clear();
	}

	public Config getConfig() {
		return config;
	}

	public void reloadConfig() {
		config = ConfigAccess.initConfig();
		grabConfig = ConfigAccess.initGrabConfig();
		coreManager.reloadGrabConfig(grabConfig);
	}

}
