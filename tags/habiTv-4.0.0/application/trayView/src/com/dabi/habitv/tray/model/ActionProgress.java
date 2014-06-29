package com.dabi.habitv.tray.model;

import java.util.Collection;

import javafx.collections.ObservableList;

import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;
import com.dabi.habitv.core.event.EpisodeStateEnum;

public class ActionProgress implements Comparable<ActionProgress> {
	private EpisodeStateEnum state;
	private final EpisodeDTO episode;
	private ProcessHolder processHolder;
	private String info;

	public ActionProgress(final EpisodeDTO episode,
			final EpisodeStateEnum state, final String info,
			final ProcessHolder processHolder) {
		super();
		this.state = state;
		this.episode = episode;
		this.processHolder = processHolder;
		this.info = info;
	}

	public EpisodeDTO getEpisode() {
		return episode;
	}

	public EpisodeStateEnum getState() {
		return state;
	}

	public void setState(final EpisodeStateEnum state) {
		this.state = state;
	}

	public String getProgress() {
		return processHolder.getProgression();
	}

	@Override
	public int compareTo(final ActionProgress o) {
		int ret = state.compareTo(o.state);
		if (ret == 0) {
			if (ret == 0) {
				ret = o.episode.compareTo(episode);
			}
		}
		return ret;
	}

	@Override
	public boolean equals(final Object obj) {
		boolean ret = false;
		if (obj instanceof ActionProgress) {
			ret = getEpisode().equals(((ActionProgress) obj).getEpisode());
		}
		return ret;
	}

	@Override
	public int hashCode() {
		return getEpisode().hashCode();
	}

	@Override
	public String toString() {
		return getEpisode().toString() + " " + getState();
	}

	public void setProcessHolder(ProcessHolder processHolder) {
		this.processHolder = processHolder;
	}

	public ProcessHolder getProcessHolder() {
		return processHolder;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public static boolean isInProgress(
			Collection<ActionProgress> actionProgressList) {
		for (ActionProgress actionProgress : actionProgressList) {
			if (actionProgress.getState().isInProgress()) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasFailed(
			Collection<ActionProgress> actionProgressList) {
		for (ActionProgress actionProgress : actionProgressList) {
			if (actionProgress.getState().hasFailed()) {
				return true;
			}
		}
		return false;
	}

}
