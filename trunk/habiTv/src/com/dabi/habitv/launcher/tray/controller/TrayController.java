package com.dabi.habitv.launcher.tray.controller;

import com.dabi.habitv.config.entities.Exporter;
import com.dabi.habitv.framework.plugin.api.ProviderPluginInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.launcher.tray.model.EpisodeStateEnum;
import com.dabi.habitv.launcher.tray.model.HabitTvTrayModel;
import com.dabi.habitv.launcher.tray.model.ProcessStateEnum;
import com.dabi.habitv.process.episode.ProcessEpisodeListener;

public class TrayController implements ProcessEpisodeListener {

	private final HabitTvTrayModel habiModel;

	public TrayController(HabitTvTrayModel habiModel) {
		this.habiModel = habiModel;
	}

	public final HabitTvTrayModel getModel() {
		return habiModel;
	}

	public void start() {
		getModel().startDownloadCheck(this);
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
		getModel().getProgressionModel().endAction(episode.getName(), EpisodeStateEnum.TO_DOWNLOAD);
		getModel().getProgressionModel().updateActionProgress(episode.getName(), EpisodeStateEnum.DOWNLOADING, progress);
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
		getModel().getProgressionModel().updateActionProgress(episode.getName(), EpisodeStateEnum.TO_DOWNLOAD, "");
	}

	@Override
	public void downloadedEpisode(EpisodeDTO episode) {
		getModel().getProgressionModel().endAction(episode.getName(), EpisodeStateEnum.DOWNLOADING);
		getModel().getProgressionModel().updateActionProgress(episode.getName(), EpisodeStateEnum.DOWNLOADED, "");
	}

	@Override
	public void downloadFailed(EpisodeDTO episode, ExecutorFailedException e) {
		getModel().getProgressionModel().endAction(episode.getName(), EpisodeStateEnum.DOWNLOADING);
		getModel().getProgressionModel().updateActionProgress(episode.getName(), EpisodeStateEnum.DOWNLOAD_FAILED, "");
		fireEpisodeChanged(EpisodeStateEnum.DOWNLOAD_FAILED, episode);

	}

	@Override
	public void exportEpisode(EpisodeDTO episode, Exporter exporter, String progression) {
		getModel().getProgressionModel().endAction(episode.getName(), EpisodeStateEnum.DOWNLOADED);
		getModel().getProgressionModel().updateActionProgress(episode.getName(), EpisodeStateEnum.EXPORTING, "");
	}

	@Override
	public void exportFailed(EpisodeDTO episode, Exporter exporter, ExecutorFailedException e) {
		getModel().getProgressionModel().endAction(episode.getName(), EpisodeStateEnum.EXPORTING);
		getModel().getProgressionModel().updateActionProgress(episode.getName(), EpisodeStateEnum.EXPORT_FAILED, "");
		fireEpisodeChanged(EpisodeStateEnum.EXPORT_FAILED, episode);
	}

	@Override
	public void providerDownloadCheckStarted(ProviderPluginInterface provider) {
		fireProcessChanged(ProcessStateEnum.CHECKING_EPISODES, provider.getName());
	}
}
