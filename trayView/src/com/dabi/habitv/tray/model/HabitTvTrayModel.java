package com.dabi.habitv.tray.model;

import java.util.Observable;

import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.core.config.ConfigAccess;
import com.dabi.habitv.core.config.HabitTvConf;
import com.dabi.habitv.core.dao.GrabConfigDAO;
import com.dabi.habitv.core.mgr.CoreManager;
import com.dabi.habitv.tray.subscriber.CoreSubscriber;
import com.dabi.habitv.tray.subscriber.SubscriberAdapter;

public class HabitTvTrayModel extends Observable {

	private final CoreManager coreManager;

	private final Config config = ConfigAccess.initConfig();

	private final ProgressionModel progressionModel;

	private Thread demonThread;

	private final GrabConfigDAO grabConfigDAO;

	public HabitTvTrayModel() {
		super();
		grabConfigDAO = new GrabConfigDAO(HabitTvConf.GRABCONFIG_XML_FILE);
		coreManager = new CoreManager(config);
		progressionModel = new ProgressionModel();
	}

	public void attach(final CoreSubscriber coreSubscriber) {
		final SubscriberAdapter subscriberAdapter = new SubscriberAdapter(coreSubscriber);
		coreManager.getCategoryManager().getSearchCategoryPublisher().attach(subscriberAdapter.getSearchCategorySubscriber());
		coreManager.getEpisodeManager().getRetreivePublisher().attach(subscriberAdapter.getRetreiveSubscriber());
		coreManager.getEpisodeManager().getSearchPublisher().attach(subscriberAdapter.getSearchSubscriber());
	}

	public ProgressionModel getProgressionModel() {
		return progressionModel;
	}

	public void startDownloadCheckDemon() {

		demonThread = new Thread() {
			@Override
			public void run() {
				boolean interrupted = false;
				final long confDemonTime;
				if (config.getDemonTime() == null) {
					confDemonTime = HabitTvConf.DEFAULT_DEMON_TIME_SEC;
				} else {
					confDemonTime = config.getDemonTime();
				}
				final long demonTime = confDemonTime * 1000L;
				// demon mode
				while (true) {
					if (interrupted) {
						interrupted = false;
					} else {
						if (grabConfigDAO.exist()) {
							coreManager.retreiveEpisode(grabConfigDAO.load());
						} else {
							grabConfigDAO.saveGrabConfig(coreManager.findCategory());
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
				coreManager.retreiveEpisode(grabConfigDAO.load());
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

}
