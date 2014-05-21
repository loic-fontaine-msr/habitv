package com.dabi.habitv.tray.controller;

import com.dabi.habitv.api.plugin.pub.UpdatablePluginEvent;
import com.dabi.habitv.core.event.EpisodeStateEnum;
import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchCategoryEvent;
import com.dabi.habitv.core.event.SearchEvent;
import com.dabi.habitv.core.event.UpdatePluginEvent;
import com.dabi.habitv.framework.plugin.utils.ProcessingThreads;
import com.dabi.habitv.tray.model.HabitTvTrayModel;
import com.dabi.habitv.tray.subscriber.CoreSubscriber;

public class TrayController implements CoreSubscriber {

	private final HabitTvTrayModel habiModel;

	public TrayController(final HabitTvTrayModel habiModel) {
		this.habiModel = habiModel;
	}

	public final HabitTvTrayModel getModel() {
		return habiModel;
	}

	public void start() {
		getModel().startDownloadCheck();
	}

	public void startDownloadCheckDemon() {
		getModel().startDownloadCheckDemon();
	}

	@Override
	public void update(final SearchEvent event) {
		switch (event.getState()) {
		case ALL_RETREIVE_DONE:

			break;
		case ALL_SEARCH_DONE:

			break;
		case BUILD_INDEX:

			break;
		case CHECKING_EPISODES:

			break;
		case DONE:

			break;
		case ERROR:

			break;
		case IDLE:

			break;
		default:
			break;
		}
	}

	@Override
	public void update(final RetreiveEvent event) {
		switch (event.getState()) {
		case BUILD_INDEX:

			break;
		case DOWNLOAD_FAILED:
			getModel().getProgressionModel().updateActionProgress(
					event.getEpisode(), EpisodeStateEnum.DOWNLOAD_FAILED,
					event.getException().getMessage(), null);
			break;
		case DOWNLOADED:
			getModel().getProgressionModel().updateActionProgress(
					event.getEpisode(), EpisodeStateEnum.DOWNLOADED, "", null);
			break;
		case DOWNLOAD_STARTING:
			getModel().getProgressionModel().updateActionProgress(
					event.getEpisode(), EpisodeStateEnum.DOWNLOAD_STARTING, "",
					event.getProcessHolder());
			break;
		case EXPORT_FAILED:
			getModel().getProgressionModel().updateActionProgress(
					event.getEpisode(), EpisodeStateEnum.EXPORT_FAILED,
					event.getOperation(), null);
			break;
		case EXPORT_STARTING:
			getModel().getProgressionModel().updateActionProgress(
					event.getEpisode(), EpisodeStateEnum.EXPORT_STARTING,
					event.getOperation(), event.getProcessHolder());
			break;
		case FAILED:

			break;
		case READY:
			getModel().getProgressionModel().updateActionProgress(
					event.getEpisode(), EpisodeStateEnum.READY, "", null);
			break;
		case TO_DOWNLOAD:
			getModel().getProgressionModel().updateActionProgress(
					event.getEpisode(), EpisodeStateEnum.TO_DOWNLOAD, "", null);
			break;
		case TO_EXPORT:

			break;
		default:
			break;
		}
	}

	@Override
	public void update(final SearchCategoryEvent event) {
		switch (event.getState()) {
		case BUILDING_CATEGORIES:
			break;
		case DONE:

			break;
		case ERROR:

			break;
		case IDLE:

			break;
		default:
			break;
		}
	}

	public void stop() {
		getModel().forceEnd();
		ProcessingThreads.killAllProcessing();
		System.exit(0);
	}

	public void clear() {
		getModel().clear();
	}

	public void updateGrabConfig() {
		getModel().updateGrabConfig();
	}

	public void reDoExport() {
		getModel().reDoExport();
	}

	public boolean hasExportToResume() {
		return getModel().hasExportToResume();
	}

	public void clearExport() {
		getModel().clearExport();
	}

	public void update() {
		getModel().update();
	}

	@Override
	public void update(final UpdatePluginEvent event) {
	}

	@Override
	public void update(UpdatablePluginEvent event) {

	}

}
