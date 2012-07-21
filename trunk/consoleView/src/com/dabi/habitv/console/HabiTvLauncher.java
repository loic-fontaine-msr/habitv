package com.dabi.habitv.console;

import org.apache.log4j.Logger;

import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.core.config.ConfigAccess;
import com.dabi.habitv.core.mgr.CoreManager;
import com.dabi.habitv.framework.plugin.utils.ProcessingThread;
import com.dabi.habitv.grabconfig.entities.GrabConfig;

public final class HabiTvLauncher {

	private static final Logger LOG = Logger.getLogger(HabiTvLauncher.class);

	private HabiTvLauncher() {

	}

	public static void main(final String[] args) throws InterruptedException {
		final Config config = ConfigAccess.initConfig();
		final GrabConfig grabConfig = ConfigAccess.initGrabConfig();

		final CoreManager coreManager = new CoreManager(config, grabConfig);

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				LOG.info("Interrupted, closing all treatments");
				coreManager.forceEnd();
				ProcessingThread.killAllProcessing();
			}

		});

		if (grabConfig == null) {
			LOG.info("Génération des catégories à télécharger");
			coreManager.findAndSaveCategory();
		} else {
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
		}
	}
}
