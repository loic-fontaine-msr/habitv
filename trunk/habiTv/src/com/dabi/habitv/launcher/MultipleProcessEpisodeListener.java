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
	public void downloadingEpisode(final EpisodeDTO episode, final String progress) {
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
	public void buildEpisodeIndex(final CategoryDTO category) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.buildEpisodeIndex(category);
		}
	}

	@Override
	public void episodeToDownload(final EpisodeDTO episode) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.episodeToDownload(episode);
		}
	}

	@Override
	public void downloadedEpisode(final EpisodeDTO episode) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.downloadedEpisode(episode);
		}
	}

	@Override
	public void downloadFailed(final EpisodeDTO episode, final ExecutorFailedException exception) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.downloadFailed(episode, exception);
		}
	}

	@Override
	public void exportEpisode(final EpisodeDTO episode, final Exporter exporter, final String progression) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.exportEpisode(episode, exporter, progression);
		}
	}

	@Override
	public void exportFailed(final EpisodeDTO episode, final Exporter exporter, final ExecutorFailedException exception) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.exportFailed(episode, exporter, exception);
		}
	}

	@Override
	public void providerDownloadCheckStarted(final ProviderPluginInterface provider) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.providerDownloadCheckStarted(provider);
		}
	}

	@Override
	public void episodeReady(final EpisodeDTO episode) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.episodeReady(episode);
		}
	}

	@Override
	public void providerDownloadCheckDone(ProviderPluginInterface provider) {
		for (ProcessEpisodeListener listener : listeners) {
			listener.providerDownloadCheckDone(provider);
		}		
	}

}
