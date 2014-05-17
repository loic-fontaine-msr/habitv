package com.dabi.habitv.console;

import org.apache.log4j.Logger;

import com.dabi.habitv.core.config.HabitTvConf;
import com.dabi.habitv.core.config.UserConfig;
import com.dabi.habitv.core.config.XMLUserConfig;
import com.dabi.habitv.core.dao.GrabConfigDAO;
import com.dabi.habitv.core.mgr.CoreManager;
import com.dabi.habitv.framework.plugin.utils.ProcessingThread;

public final class ConsoleLauncher { // NO_UCD (unused code)

	private static final Logger LOG = Logger.getLogger(ConsoleLauncher.class);

	private ConsoleLauncher() {

	}

	public static void main(final String[] args) throws InterruptedException {
		try {
			final UserConfig config = XMLUserConfig.initConfig();

			final GrabConfigDAO grabConfigDAO = new GrabConfigDAO(HabitTvConf.GRABCONFIG_XML_FILE);
			final CoreManager coreManager = new CoreManager(config);
			if (config.updateOnStartup()) {
				coreManager.update();
			}

			Runtime.getRuntime().addShutdownHook(new Thread() {

				@Override
				public void run() {
					LOG.info("Interrupted, closing all treatments");
					coreManager.forceEnd();
					ProcessingThread.killAllProcessing();
				}

			});

			if (grabConfigDAO.exist()) {
				grabConfigDAO.updateGrabConfig(coreManager.findCategory());
				if (config.getDemonCheckTime() == null) {
					coreManager.update();
					coreManager.retreiveEpisode(grabConfigDAO.load());
				} else {
					final long demonTime = config.getDemonCheckTime() * 1000L;
					// demon mode
					while (true) {
						coreManager.update();
						coreManager.retreiveEpisode(grabConfigDAO.load());
						Thread.sleep(demonTime);
					}
				}
			} else {
				LOG.info("Génération des catégories à télécharger");
				grabConfigDAO.saveGrabConfig(coreManager.findCategory());
			}
		} catch (final Exception e) {
			LOG.error("", e);
			System.exit(1);
		}
	}

}
