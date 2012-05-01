package com.dabi.habitv.launcher.tray.controller;

import java.util.ArrayList;
import java.util.List;

import com.dabi.habitv.config.entities.Exporter;
import com.dabi.habitv.framework.plugin.api.ProviderPluginInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.utils.ProcessingThread;
import com.dabi.habitv.launcher.ConsoleProcessEpisodeListener;
import com.dabi.habitv.launcher.MultipleProcessEpisodeListener;
import com.dabi.habitv.launcher.tray.model.EpisodeStateEnum;
import com.dabi.habitv.launcher.tray.model.HabitTvTrayModel;
import com.dabi.habitv.launcher.tray.model.ProcessStateEnum;
import com.dabi.habitv.process.episode.ProcessEpisodeListener;

public class TrayController implements ProcessEpisodeListener {

	private final HabitTvTrayModel habiModel;

	private final ProcessEpisodeListener listeners;

	public TrayController(HabitTvTrayModel habiModel) {
		this.habiModel = habiModel;
		List<ProcessEpisodeListener> listeners = new ArrayList<>();
		listeners.add(this);
		listeners.add(new ConsoleProcessEpisodeListener());
		this.listeners = new MultipleProcessEpisodeListener(listeners);
	}

	public final HabitTvTrayModel getModel() {
		return habiModel;
	}

	public void start() {
		getModel().startDownloadCheck(listeners);
	}

	public void startDownloadCheckDemon() {
		getModel().startDownloadCheckDemon(listeners);
	}

	private void fireProcessChanged(ProcessStateEnum processStateEnum, String info) {
		getModel().fireProcessChanged(processStateEnum, info);
	}

	private void fireEpisodeChanged(EpisodeStateEnum episodeStateEnum, EpisodeDTO episode) {
		getModel().fireEpisodeChanged(episodeStateEnum, episode);
	}

	@Override
	public void downloadCheckStarted() {
		fireProcessChanged(ProcessStateEnum.CHECKING_EPISODES, null);
	}

	@Override
	public void downloadingEpisode(EpisodeDTO episode, String progress) {
		getModel().getProgressionModel().endAction(episode, EpisodeStateEnum.TO_DOWNLOAD);
		getModel().getProgressionModel().updateActionProgress(episode, EpisodeStateEnum.DOWNLOADING, progress);
	}

	@Override
	public void processDone() {
		fireProcessChanged(ProcessStateEnum.DONE, null);
	}

	@Override
	public void buildEpisodeIndex(CategoryDTO category) {
		fireProcessChanged(ProcessStateEnum.BUILD_INDEX, category.getName());
	}

	@Override
	public void episodeToDownload(EpisodeDTO episode) {
		getModel().getProgressionModel().updateActionProgress(episode, EpisodeStateEnum.TO_DOWNLOAD, "");
		fireEpisodeChanged(EpisodeStateEnum.TO_DOWNLOAD, episode);
	}

	@Override
	public void downloadedEpisode(EpisodeDTO episode) {
		getModel().getProgressionModel().endAction(episode, EpisodeStateEnum.DOWNLOADING);
		getModel().getProgressionModel().updateActionProgress(episode, EpisodeStateEnum.DOWNLOADED, "");
	}

	@Override
	public void downloadFailed(EpisodeDTO episode, ExecutorFailedException e) {
		getModel().getProgressionModel().endAction(episode, EpisodeStateEnum.DOWNLOADING);
		getModel().getProgressionModel().updateActionProgress(episode, EpisodeStateEnum.DOWNLOAD_FAILED, "");
		fireEpisodeChanged(EpisodeStateEnum.DOWNLOAD_FAILED, episode);

	}

	@Override
	public void exportEpisode(EpisodeDTO episode, Exporter exporter, String progression) {
		getModel().getProgressionModel().endAction(episode, EpisodeStateEnum.DOWNLOADED);
		getModel().getProgressionModel().updateActionProgress(episode, EpisodeStateEnum.EXPORTING, progression);
	}

	@Override
	public void exportFailed(EpisodeDTO episode, Exporter exporter, ExecutorFailedException e) {
		getModel().getProgressionModel().endAction(episode, EpisodeStateEnum.EXPORTING);
		getModel().getProgressionModel().updateActionProgress(episode, EpisodeStateEnum.EXPORT_FAILED, "");
		fireEpisodeChanged(EpisodeStateEnum.EXPORT_FAILED, episode);
	}

	@Override
	public void providerDownloadCheckStarted(ProviderPluginInterface provider) {
		fireProcessChanged(ProcessStateEnum.CHECKING_EPISODES, provider.getName());
	}

	@Override
	public void episodeReady(EpisodeDTO episode) {
		getModel().getProgressionModel().endAction(episode, EpisodeStateEnum.EXPORTING);
		getModel().getProgressionModel().updateActionProgress(episode, EpisodeStateEnum.READY,"");
		fireEpisodeChanged(EpisodeStateEnum.READY, episode);
	}

	public void stop() {
		ProcessingThread.killAllProcessing();
		System.exit(0);
	}
}
