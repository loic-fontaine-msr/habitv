package com.dabi.habitv.launcher.tray.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.dabi.habitv.config.entities.Exporter;
import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.ProcessingThread;
import com.dabi.habitv.launcher.ConsoleProcessEpisodeListener;
import com.dabi.habitv.launcher.MultipleProcessEpisodeListener;
import com.dabi.habitv.launcher.tray.model.EpisodeStateEnum;
import com.dabi.habitv.launcher.tray.model.HabitTvTrayModel;
import com.dabi.habitv.launcher.tray.model.ProcessStateEnum;
import com.dabi.habitv.process.episode.ProcessEpisodeListener;

public class TrayController implements ProcessEpisodeListener {

	private static final Logger LOG = Logger.getLogger(TrayController.class);

	private final HabitTvTrayModel habiModel;

	private final ProcessEpisodeListener listeners;

	public TrayController(final HabitTvTrayModel habiModel) {
		this.habiModel = habiModel;
		final List<ProcessEpisodeListener> listeners = new ArrayList<>();
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

	private void printStack() {
		if (LOG.isDebugEnabled()) {
			int limit = 3;
			int i = 0;
			for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
				if (i > 2) {
					LOG.debug(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "L" + stackTraceElement.getLineNumber());
				}
				if (i >= limit) {
					break;
				}
				i++;
			}
		}
	}

	private void fireProcessChanged(final ProcessStateEnum processStateEnum, final String info) {
		LOG.debug("fireProcessChanged " + processStateEnum + " " + info);
		printStack();
		getModel().fireProcessChanged(processStateEnum, info);
	}

	private void fireEpisodeChanged(final EpisodeStateEnum episodeStateEnum, final EpisodeDTO episode) {
		LOG.debug("fireProcessChanged " + episodeStateEnum + " " + episode);
		getModel().fireEpisodeChanged(episodeStateEnum, episode);
	}

	@Override
	public void downloadCheckStarted() {
		fireProcessChanged(ProcessStateEnum.CHECKING_EPISODES, null);
	}

	@Override
	public void downloadingEpisode(final EpisodeDTO episode, final String progress) {
		getModel().getProgressionModel().updateActionProgress(episode, EpisodeStateEnum.DOWNLOADING, "", progress);
	}

	@Override
	public void processDone() {
		// must wait for the next test since the model may not have been update
		// yet
		try {
			Thread.sleep(7000);
		} catch (InterruptedException e) {
			throw new TechnicalException(e);
		}
		if (getModel().getProgressionModel().isAllActionDone()) {
			fireProcessChanged(ProcessStateEnum.DONE, null);
		}
	}

	@Override
	public void buildEpisodeIndex(final CategoryDTO category) {
		fireProcessChanged(ProcessStateEnum.BUILD_INDEX, category.getName());
	}

	@Override
	public void episodeToDownload(final EpisodeDTO episode) {
		getModel().getProgressionModel().updateActionProgress(episode, EpisodeStateEnum.TO_DOWNLOAD, "", "");
		fireEpisodeChanged(EpisodeStateEnum.TO_DOWNLOAD, episode);
	}

	@Override
	public void downloadedEpisode(final EpisodeDTO episode) {
		getModel().getProgressionModel().updateActionProgress(episode, EpisodeStateEnum.DOWNLOADED, "", "");
	}

	@Override
	public void downloadFailed(final EpisodeDTO episode, final ExecutorFailedException exception) {
		getModel().getProgressionModel().updateActionProgress(episode, EpisodeStateEnum.DOWNLOAD_FAILED, exception.getMessage(), "");
		fireEpisodeChanged(EpisodeStateEnum.DOWNLOAD_FAILED, episode);
	}

	@Override
	public void exportEpisode(final EpisodeDTO episode, final Exporter exporter, final String progression) {
		getModel().getProgressionModel().updateActionProgress(episode, EpisodeStateEnum.EXPORTING, exporter.getOutput(), progression);
	}

	@Override
	public void exportFailed(final EpisodeDTO episode, final Exporter exporter, final ExecutorFailedException exception) {
		getModel().getProgressionModel().updateActionProgress(episode, EpisodeStateEnum.EXPORT_FAILED, exporter.getOutput(), "");
		fireEpisodeChanged(EpisodeStateEnum.EXPORT_FAILED, episode);
	}

	@Override
	public void providerDownloadCheckStarted(final PluginProviderInterface provider) {
		fireProcessChanged(ProcessStateEnum.CHECKING_EPISODES, provider.getName());
	}

	@Override
	public void providerDownloadCheckDone(PluginProviderInterface provider) {

	}

	@Override
	public void episodeReady(final EpisodeDTO episode) {
		getModel().getProgressionModel().updateActionProgress(episode, EpisodeStateEnum.READY, "", "");
		fireEpisodeChanged(EpisodeStateEnum.READY, episode);
		if (getModel().getProgressionModel().isAllActionDone()) {
			fireProcessChanged(ProcessStateEnum.DONE, null);
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

	public void reloadConfig() {
		getModel().reloadConfig();
	}
}
