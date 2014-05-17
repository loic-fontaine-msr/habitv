package com.dabi.habitv.tray.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.api.plugin.pub.UpdatablePluginEvent;
import com.dabi.habitv.core.config.UserConfig;
import com.dabi.habitv.core.config.XMLUserConfig;
import com.dabi.habitv.core.event.EpisodeStateEnum;
import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchCategoryEvent;
import com.dabi.habitv.core.event.SearchEvent;
import com.dabi.habitv.core.event.UpdatePluginEvent;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.utils.ProcessingThread;
import com.dabi.habitv.tray.model.HabitTvViewManager;
import com.dabi.habitv.tray.subscriber.CoreSubscriber;

public class ViewController implements CoreSubscriber {

	private final HabitTvViewManager habitvModel;

	public ViewController(final HabitTvViewManager habitvModel) {
		this.habitvModel = habitvModel;
	}

	public final HabitTvViewManager getModel() {
		return habitvModel;
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
					event.getException().getMessage(), "");
			break;
		case DOWNLOADED:
			getModel().getProgressionModel().updateActionProgress(
					event.getEpisode(), EpisodeStateEnum.DOWNLOADED, "", "");
			break;
		case DOWNLOADING:
			getModel().getProgressionModel().updateActionProgress(
					event.getEpisode(), EpisodeStateEnum.DOWNLOADING, "",
					event.getProgress());
			break;
		case EXPORT_FAILED:
			getModel().getProgressionModel().updateActionProgress(
					event.getEpisode(), EpisodeStateEnum.EXPORT_FAILED,
					event.getOperation(), "");
			break;
		case EXPORTING:
			getModel().getProgressionModel().updateActionProgress(
					event.getEpisode(), EpisodeStateEnum.EXPORTING,
					event.getOperation(), event.getProgress());
			break;
		case FAILED:

			break;
		case READY:
			getModel().getProgressionModel().updateActionProgress(
					event.getEpisode(), EpisodeStateEnum.READY, "", "");
			break;
		case TO_DOWNLOAD:
			getModel().getProgressionModel().updateActionProgress(
					event.getEpisode(), EpisodeStateEnum.TO_DOWNLOAD, "", "");
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
		ProcessingThread.killAllProcessing();
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

	public void openErrorFile() {
		open(FrameworkConf.ERROR_FILE);
	}

	public void openIndexDir() {
		final UserConfig config = getModel().getUserConfig();
		open(config.getIndexDir());
	}

	private static void open(final String toOpen) {
		try {
			final String canonicalPath = new File(toOpen).getCanonicalPath();
			if (!Desktop.isDesktopSupported()) {
				return;
			}
			final Desktop desktop = Desktop.getDesktop();

			File file = new File(canonicalPath);
			if (file.isFile()) {
				try {
					desktop.open(file);
				} catch (Exception e) {
					ProcessBuilder pb = new ProcessBuilder("Notepad.exe",
							canonicalPath);
					pb.start();
				}
			} else {
				desktop.open(file);
			}

		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
	}

	public void openDownloadDir() {
		final UserConfig config = getModel().getUserConfig();
		open(config.getDownloadOuput().substring(0,
				config.getDownloadOuput().indexOf("#"))); //$NON-NLS-1$
	}

	public void openConfig() {
		open(XMLUserConfig.CONF_FILE);
	}

	public void openGrabConfig() {
		open(XMLUserConfig.GRAB_CONF_FILE);
	}

}
