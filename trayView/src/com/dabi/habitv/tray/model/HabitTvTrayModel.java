package com.dabi.habitv.tray.model;

import java.util.Observable;

import org.apache.log4j.Logger;

import com.dabi.habitv.core.config.HabitTvConf;
import com.dabi.habitv.core.config.UserConfig;
import com.dabi.habitv.core.config.XMLUserConfig;
import com.dabi.habitv.core.dao.GrabConfigDAO;
import com.dabi.habitv.core.event.SearchEvent;
import com.dabi.habitv.core.event.SearchStateEnum;
import com.dabi.habitv.core.mgr.CoreManager;
import com.dabi.habitv.tray.subscriber.CoreSubscriber;
import com.dabi.habitv.tray.subscriber.SubscriberAdapter;

public class HabitTvTrayModel extends Observable {

	private static final Logger LOG = Logger.getLogger(HabitTvTrayModel.class);

	private final CoreManager coreManager;

	private final UserConfig userConfig = XMLUserConfig.initConfig();

	private final ProgressionModel progressionModel;

	private Thread demonThread;

	private final GrabConfigDAO grabConfigDAO;

	public HabitTvTrayModel() {
		super();
		grabConfigDAO = new GrabConfigDAO(HabitTvConf.GRABCONFIG_XML_FILE);
		coreManager = new CoreManager(userConfig);
		progressionModel = new ProgressionModel();
	}

	public void attach(final CoreSubscriber coreSubscriber) {
		final SubscriberAdapter subscriberAdapter = new SubscriberAdapter(coreSubscriber);
		coreManager.getCategoryManager().getSearchCategoryPublisher().attach(subscriberAdapter.buildSearchCategorySubscriber());
		coreManager.getEpisodeManager().getRetreivePublisher().attach(subscriberAdapter.buildRetreiveSubscriber());
		coreManager.getEpisodeManager().getSearchPublisher().attach(subscriberAdapter.buildSearchSubscriber());
		coreManager.getUpdateManager().getUpdatePublisher().attach(subscriberAdapter.buildUpdateSubscriber());
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
				confDemonTime = userConfig.getDemonTime();
				final long demonTime = confDemonTime * 1000L;
				boolean still = true;
				// demon mode
				while (still) {
					if (interrupted) {
						interrupted = false;
					} else {
						try {
							if (grabConfigDAO.exist()) {
								coreManager.retreiveEpisode(grabConfigDAO.load());
							} else {
								grabConfigDAO.saveGrabConfig(coreManager.findCategory());
							}
						} catch (final Exception e) {
							LOG.error("", e);
							coreManager.getEpisodeManager().getSearchPublisher().addNews(new SearchEvent(SearchStateEnum.ERROR, e));
							still = false;
						}
					}
					if (still) {
						try {
							Thread.sleep(demonTime);
						} catch (final InterruptedException e) {
							// may have been interrupted by a manually start
							interrupted = true;
						}
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

	public UserConfig getUserConfig() {
		return userConfig;
	}

	public void updateGrabConfig() {
		grabConfigDAO.updateGrabConfig(coreManager.findCategory());
	}

	public void reDoExport() {
		coreManager.reDoExport();
	}

	public boolean hasExportToResume() {
		return coreManager.hasExportToResume();
	}

	public void clearExport() {
		coreManager.clearExport();
	}

	public void update() {
		coreManager.update();
	}

}
