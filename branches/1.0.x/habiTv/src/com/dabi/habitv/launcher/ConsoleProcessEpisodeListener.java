package com.dabi.habitv.launcher;

import org.apache.log4j.Logger;

import com.dabi.habitv.config.entities.Exporter;
import com.dabi.habitv.framework.plugin.api.ProviderPluginInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.process.episode.ProcessEpisodeListener;

public class ConsoleProcessEpisodeListener implements ProcessEpisodeListener {

	private static final Logger LOG = Logger.getLogger(HabiTvLauncher.class);

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

	@Override
	public void episodeReady(EpisodeDTO episode) {
		LOG.info(episode.getCategory() + " " + episode.getName() + " prêt");
	}

}
