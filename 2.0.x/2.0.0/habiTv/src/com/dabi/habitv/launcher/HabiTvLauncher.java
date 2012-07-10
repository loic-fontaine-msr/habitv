package com.dabi.habitv.launcher;

import org.apache.log4j.Logger;

import com.dabi.habitv.config.ConfigAccess;
import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.framework.plugin.utils.ProcessingThread;
import com.dabi.habitv.grabconfig.entities.GrabConfig;
import com.dabi.habitv.process.category.ProcessCategory;
import com.dabi.habitv.process.category.ProcessCategoryListener;
import com.dabi.habitv.process.episode.ProcessEpisodeListener;
import com.dabi.habitv.process.episode.RetrieveAndExport;
import com.dabi.habitv.taskmanager.TaskMgr;

public final class HabiTvLauncher {

	private static final Logger LOG = Logger.getLogger(HabiTvLauncher.class);

	private HabiTvLauncher() {

	}

	public static void main(final String[] args) throws InterruptedException {
		final Config config = ConfigAccess.initConfig();
		final GrabConfig grabConfig = ConfigAccess.initGrabConfig();

		final TaskMgr taskMgr = new TaskMgr(ConfigAccess.buildTaskType2ThreadPool(config));
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				LOG.info("Interrompus, fermeture des traitements");
				taskMgr.forceEnd();
				ProcessingThread.killAllProcessing();
			}

		});

		if (grabConfig == null) {
			LOG.info("Génération des catégories à télécharger");
			(new ProcessCategory()).execute(config, new ProcessCategoryListener() {

				@Override
				public void getProviderCategories(final String providerName) {
					LOG.info(providerName);
				}

				@Override
				public void categoriesSaved(final String grabconfigXmlFile) {
					LOG.info("Catégories sauvegardées dans " + grabconfigXmlFile);
				}
			});
		} else {

			final ProcessEpisodeListener listener = new ConsoleProcessEpisodeListener();

			final RetrieveAndExport retrieveAndExport = new RetrieveAndExport(config, grabConfig);
			if (config.getDemonTime() == null) {
				retrieveAndExport.execute(listener, taskMgr);
				taskMgr.waitForEndTasks(config.getAllDownloadTimeout());
			} else {
				final long demonTime = config.getDemonTime() * 1000L;
				// demon mode
				while (true) {
					retrieveAndExport.execute(listener, taskMgr);
					Thread.sleep(demonTime);
				}
			}
		}
	}
}
