package com.dabi.habitv.process.episode;

import java.util.EventListener;

import com.dabi.habitv.config.entities.Exporter;
import com.dabi.habitv.framework.plugin.api.ProviderPluginInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;

public interface ProcessEpisodeListener extends EventListener {

	void downloadCheckStarted();

	void downloadingEpisode(final EpisodeDTO episode, final String progress);

	void processDone();

	void buildEpisodeIndex(final CategoryDTO category);

	void episodeToDownload(final EpisodeDTO episode);

	void downloadedEpisode(final EpisodeDTO episode);

	void downloadFailed(final EpisodeDTO episode, final ExecutorFailedException exception);

	void exportEpisode(final EpisodeDTO episode, final Exporter exporter, final String progression);

	void exportFailed(final EpisodeDTO episode, final Exporter exporter, final ExecutorFailedException exception);

	void providerDownloadCheckStarted(final ProviderPluginInterface provider);

	void episodeReady(final EpisodeDTO episode);

	void providerDownloadCheckDone(final ProviderPluginInterface provider);

}
