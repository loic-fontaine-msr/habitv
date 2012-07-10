package com.dabi.habitv.launcher;

import java.util.List;

import com.dabi.habitv.config.entities.Exporter;
import com.dabi.habitv.framework.plugin.api.ProviderPluginInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.process.episode.ProcessEpisodeListener;

public class MultipleProcessEpisodeListener implements ProcessEpisodeListener {

	private final List<ProcessEpisodeListener> listeners;

	public MultipleProcessEpisodeListener(final List<ProcessEpisodeListener> listeners) {
		this.listeners = listeners;
	}

	@Override
	public void downloadCheckStarted() {
		for (ProcessEpisodeListener listener : listeners) {
			listener.downloadCheckStarted();
		}
	}

	@Override
	public void downloadingEpisode(EpisodeDTO episode, String progress) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.downloadingEpisode(episode, progress);
		}
	}

	@Override
	public void processDone() {
		for (ProcessEpisodeListener listener : listeners) {
			listener.processDone();
		}
	}

	@Override
	public void buildEpisodeIndex(CategoryDTO category) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.buildEpisodeIndex(category);
		}
	}

	@Override
	public void episodeToDownload(EpisodeDTO episode) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.episodeToDownload(episode);
		}
	}

	@Override
	public void downloadedEpisode(EpisodeDTO episode) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.downloadedEpisode(episode);
		}
	}

	@Override
	public void downloadFailed(EpisodeDTO episode, ExecutorFailedException e) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.downloadFailed(episode, e);
		}
	}

	@Override
	public void exportEpisode(EpisodeDTO episode, Exporter exporter, String progression) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.exportEpisode(episode, exporter, progression);
		}
	}

	@Override
	public void exportFailed(EpisodeDTO episode, Exporter exporter, ExecutorFailedException e) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.exportFailed(episode, exporter, e);
		}
	}

	@Override
	public void providerDownloadCheckStarted(ProviderPluginInterface provider) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.providerDownloadCheckStarted(provider);
		}
	}

	@Override
	public void episodeReady(EpisodeDTO episode) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.episodeReady(episode);
		}
	}

}
