package com.dabi.habitv.tray.model;

import java.util.Observable;

import org.apache.log4j.Logger;

import com.dabi.habitv.api.plugin.pub.Subscriber;
import com.dabi.habitv.core.config.HabitTvConf;
import com.dabi.habitv.core.config.UserConfig;
import com.dabi.habitv.core.config.XMLUserConfig;
import com.dabi.habitv.core.dao.GrabConfigDAO;
import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchEvent;
import com.dabi.habitv.core.event.SearchStateEnum;
import com.dabi.habitv.core.mgr.CoreManager;
import com.dabi.habitv.tray.subscriber.CoreSubscriber;
import com.dabi.habitv.tray.subscriber.SubscriberAdapter;

public class HabitTvViewManager extends Observable {

	private static final Logger LOG = Logger.getLogger(HabitTvViewManager.class);

	private final CoreManager coreManager;

	private final UserConfig userConfig = XMLUserConfig.initConfig();

	private final ProgressionModel progressionModel;

	private Thread demonThread;

	private final GrabConfigDAO grabConfigDAO;

	public HabitTvViewManager() {
		super();
		grabConfigDAO = new GrabConfigDAO(HabitTvConf.GRABCONFIG_XML_FILE);
		coreManager = new CoreManager(userConfig);
		progressionModel = new ProgressionModel();
		if (userConfig.updateOnStartup()) {
			coreManager.update();
		}
	}

	public void attach(final CoreSubscriber coreSubscriber) {
		final SubscriberAdapter subscriberAdapter = new SubscriberAdapter(coreSubscriber);
		coreManager.getCategoryManager().getSearchCategoryPublisher().attach(subscriberAdapter.buildSearchCategorySubscriber());
		coreManager.getEpisodeManager().getRetreivePublisher().attach(subscriberAdapter.buildRetreiveSubscriber());
		coreManager.getEpisodeManager().getSearchPublisher().attach(subscriberAdapter.buildSearchSubscriber());
		coreManager.getPluginManager().getUpdatePluginPublisher().attach(subscriberAdapter.buildUpdateSubscriber());
		coreManager.getPluginManager().getUpdatablePluginPublisher().attach(subscriberAdapter.buildUpdatablePluginSubscriber());
	}
	

	public void attachRetreiveSubscriber(Subscriber<RetreiveEvent> retreiveSubscriber) {
		coreManager.getEpisodeManager().getRetreivePublisher().attach(retreiveSubscriber);
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
				confDemonTime = userConfig.getDemonCheckTime();
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
