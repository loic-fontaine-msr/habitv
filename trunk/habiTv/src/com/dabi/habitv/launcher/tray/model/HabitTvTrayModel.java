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
import com.dabi.habitv.process.category.ProcessCategory;
import com.dabi.habitv.process.category.ProcessCategoryListener;
import com.dabi.habitv.process.episode.ProcessEpisodeListener;
import com.dabi.habitv.process.episode.RetrieveAndExport;
import com.dabi.habitv.taskmanager.TaskMgr;

public class HabitTvTrayModel extends Observable {

	private final RetrieveAndExport retrieveAndExport;

	private final TaskMgr taskMgr;

	private final Config config = ConfigAccess.initConfig();

	private final GrabConfig grabConfig = ConfigAccess.initGrabConfig();

	private final EventListenerList listeners;

	//TODO rapprocher le progressionModel de TaskMgr ?
	private final ProgressionModel progressionModel;

	private Thread demonThread;

	public HabitTvTrayModel() {
		super();
		retrieveAndExport = new RetrieveAndExport(config, grabConfig);
		progressionModel = new ProgressionModel();
		listeners = new EventListenerList();
		taskMgr = new TaskMgr(ConfigAccess.buildTaskType2ThreadPool(config));

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
		final HabiTvListener[] listenerList = (HabiTvListener[]) listeners.getListeners(HabiTvListener.class);

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

	public void startDownloadCheckDemon(final ProcessEpisodeListener listener) {

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
							(new ProcessCategory()).execute(config, new ProcessCategoryListener() {

								@Override
								public void getProviderCategories(final String providerName) {
									fireProcessChanged(ProcessStateEnum.BUILDING_CATEGORIES, providerName);
								}

								@Override
								public void categoriesSaved(final String grabconfigXmlFile) {
									fireProcessChanged(ProcessStateEnum.CATEGORIES_BUILD, grabconfigXmlFile);
								}
							});
						} else {
							retrieveAndExport.execute(listener, taskMgr);
						}
					}
					try {
						Thread.sleep(demonTime);
					} catch (InterruptedException e) {
						// may have been interrupted by a manually start
						interrupted = true;
					}
				}
			}

		};

		demonThread.start();
	}

	public void startDownloadCheck(final ProcessEpisodeListener listener) {

		demonThread.interrupt();
		(new Thread() {
			@Override
			public void run() {
				retrieveAndExport.execute(listener, taskMgr);
			}

		}).start();
	}

	public void forceEnd() {
		taskMgr.forceEnd();
	}

}
