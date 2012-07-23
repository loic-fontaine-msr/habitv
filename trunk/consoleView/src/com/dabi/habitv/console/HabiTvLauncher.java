package com.dabi.habitv.console;

import org.apache.log4j.Logger;

import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.core.config.ConfigAccess;
import com.dabi.habitv.core.config.HabitTvConf;
import com.dabi.habitv.core.dao.GrabConfigDAO;
import com.dabi.habitv.core.mgr.CoreManager;
import com.dabi.habitv.framework.plugin.utils.ProcessingThread;

public final class HabiTvLauncher {

	private static final Logger LOG = Logger.getLogger(HabiTvLauncher.class);

	private HabiTvLauncher() {

	}

	public static void main(final String[] args) throws InterruptedException {
		final Config config = ConfigAccess.initConfig();
		final GrabConfigDAO grabConfigDAO = new GrabConfigDAO(HabitTvConf.GRABCONFIG_XML_FILE);
		final CoreManager coreManager = new CoreManager(config, grabConfigDAO.load());

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				LOG.info("Interrupted, closing all treatments");
				coreManager.forceEnd();
				ProcessingThread.killAllProcessing();
			}

		});

		if (grabConfigDAO.exist()) {
			if (config.getDemonTime() == null) {
				coreManager.retreiveEpisode();
			} else {
				final long demonTime = config.getDemonTime() * 1000L;
				// demon mode
				while (true) {
					coreManager.retreiveEpisode();
					Thread.sleep(demonTime);
				}
			}
		} else {
			LOG.info("Génération des catégories à télécharger");
			grabConfigDAO.saveGrabConfig(coreManager.findCategory());
		}
	}
}
