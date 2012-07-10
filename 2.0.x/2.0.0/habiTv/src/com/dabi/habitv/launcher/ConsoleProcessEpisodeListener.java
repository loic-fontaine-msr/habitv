package com.dabi.habitv.launcher;

import org.apache.log4j.Logger;

import com.dabi.habitv.config.entities.Exporter;
import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.process.episode.ProcessEpisodeListener;

public class ConsoleProcessEpisodeListener implements ProcessEpisodeListener {

	private static final Logger LOG = Logger.getLogger(HabiTvLauncher.class);

	@Override
	public void downloadCheckStarted() {
		LOG.info("Recherche des épisodes à télécharger...");
	}

	@Override
	public void downloadingEpisode(final EpisodeDTO episode, final String progress) {
		LOG.info("Dowloading " + episode.getCategory() + " - " + episode.getName() + " " + progress + "%");
	}

	@Override
	public void processDone() {
		LOG.info("Terminé");
	}

	@Override
	public void buildEpisodeIndex(final CategoryDTO category) {
		LOG.info("Construction de l'index pour " + category.getName());
	}

	@Override
	public void episodeToDownload(final EpisodeDTO episode) {
		LOG.info(episode.getCategory() + " - " + episode.getName() + " à télécharger");
	}

	@Override
	public void downloadedEpisode(final EpisodeDTO episode) {
		LOG.info(episode.getCategory() + " - " + episode.getName() + "Downloaded");
	}

	@Override
	public void downloadFailed(final EpisodeDTO episode, final ExecutorFailedException e) {
		LOG.error("download of " + episode.getCategory() + " - " + episode.getName() + "failed");
		LOG.error("cmd was" + e.getCmd());
		LOG.error(e.getFullOuput());
	}

	@Override
	public void exportEpisode(final EpisodeDTO episode, final Exporter exporter, final String progression) {
		LOG.info(exporter.getOutput() + " " + episode.getCategory() + " - " + episode.getName() + " " + progression + "%");
	}

	@Override
	public void exportFailed(final EpisodeDTO episode, final Exporter exporter, final ExecutorFailedException e) {
		LOG.error("export of " + episode.getCategory() + " - " + episode.getName() + "failed");
		LOG.error("cmd was" + e.getCmd());
		LOG.error(e.getFullOuput());
	}

	@Override
	public void providerDownloadCheckStarted(final PluginProviderInterface provider) {
		LOG.info(provider.getName());
	}

	@Override
	public void episodeReady(final EpisodeDTO episode) {
		LOG.info(episode.getCategory() + " " + episode.getName() + " prêt");
	}

	@Override
	public void providerDownloadCheckDone(PluginProviderInterface provider) {
		LOG.info(provider.getName()+" done");
	}

}
