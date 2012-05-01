package com.dabi.habitv.process.episode;

import java.util.EventListener;

import com.dabi.habitv.config.entities.Exporter;
import com.dabi.habitv.framework.plugin.api.ProviderPluginInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;

public interface ProcessEpisodeListener extends EventListener {

	void downloadCheckStarted();

	void downloadingEpisode(EpisodeDTO episode, String progress);

	void processDone();

	void buildEpisodeIndex(CategoryDTO category);

	void episodeToDownload(EpisodeDTO episode);

	void downloadedEpisode(EpisodeDTO episode);

	void downloadFailed(EpisodeDTO episode, ExecutorFailedException e);

	void exportEpisode(EpisodeDTO episode, Exporter exporter, String progression);

	void exportFailed(EpisodeDTO episode, Exporter exporter, ExecutorFailedException e);

	void providerDownloadCheckStarted(ProviderPluginInterface provider);

}
