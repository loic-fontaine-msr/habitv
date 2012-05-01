package com.dabi.habitv.launcher;

import org.apache.log4j.Logger;

import com.dabi.habitv.config.ConfigAccess;
import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.config.entities.Exporter;
import com.dabi.habitv.framework.plugin.api.ProviderPluginInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.utils.ProcessingThread;
import com.dabi.habitv.grabconfig.entities.GrabConfig;
import com.dabi.habitv.process.category.ProcessCategory;
import com.dabi.habitv.process.category.ProcessCategoryListener;
import com.dabi.habitv.process.episode.ProcessEpisodeListener;
import com.dabi.habitv.process.episode.RetrieveAndExport;

public final class HabiTvLauncher {

	private static final Logger LOG = Logger.getLogger(HabiTvLauncher.class);

	private HabiTvLauncher() {

	}

	public static void main(final String[] args) throws InterruptedException {
		final Config config = ConfigAccess.initConfig();
		final GrabConfig grabConfig = ConfigAccess.initGrabConfig();

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				LOG.info("Interrompus, fermeture des traitements");
				ProcessingThread.killAllProcessing();
			}

		});

		if (grabConfig == null) {
			LOG.info("Génération des catégories à télécharger");
			(new ProcessCategory()).execute(config, new ProcessCategoryListener() {

				@Override
				public void getProviderCategories(String providerName) {
					LOG.info(providerName);
				}

				@Override
				public void categoriesSaved(String grabconfigXmlFile) {
					LOG.info("Catégories sauvegardées dans " + grabconfigXmlFile);
				}
			});
		} else {

			ProcessEpisodeListener listener = new ProcessEpisodeListener() {

				@Override
				public void downloadCheckStarted() {
					LOG.info("Recherche des épiodes à télécharger...");
				}

				@Override
				public void downloadingEpisode(EpisodeDTO episode, String progress) {
					LOG.info("Dowloading " + episode.getName() + " " + progress + "%");
				}

				@Override
				public void processDone() {
					LOG.info("Terminé");
				}

				@Override
				public void buildEpisodeIndex(CategoryDTO category) {
					LOG.info("Construction de l'index pour " + category.getName());
				}

				@Override
				public void episodeToDownload(EpisodeDTO episode) {

				}

				@Override
				public void downloadedEpisode(EpisodeDTO episode) {
					LOG.info(episode.getName() + "Downloaded");
				}

				@Override
				public void downloadFailed(EpisodeDTO episode, ExecutorFailedException e) {
					LOG.error("download of " + episode.getName() + "failed");
					LOG.error("cmd was" + e.getCmd());
					LOG.error(e.getFullOuput());
				}

				@Override
				public void exportEpisode(EpisodeDTO episode, Exporter exporter, String progression) {
					LOG.info(exporter.getOutput() + " " + episode.getName() + " " + progression + "%");
				}

				@Override
				public void exportFailed(EpisodeDTO episode, Exporter exporter, ExecutorFailedException e) {
					LOG.error("export of " + episode.getName() + "failed");
					LOG.error("cmd was" + e.getCmd());
					LOG.error(e.getFullOuput());
				}

				@Override
				public void providerDownloadCheckStarted(ProviderPluginInterface provider) {
					LOG.info(provider.getName());
				}

			};

			if (config.getDemonTime() == null) {
				(new RetrieveAndExport()).execute(config, grabConfig, listener);
			} else {
				final long demonTime = config.getDemonTime() * 1000L;
				// demon mode
				while (true) {
					(new RetrieveAndExport()).execute(config, grabConfig, listener);
					Thread.sleep(demonTime);
				}
			}
		}
	}
}
