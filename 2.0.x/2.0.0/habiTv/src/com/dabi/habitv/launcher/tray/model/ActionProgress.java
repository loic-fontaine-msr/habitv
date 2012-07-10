package com.dabi.habitv.launcher.tray.model;

import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public class ActionProgress implements Comparable<ActionProgress> {
	private EpisodeStateEnum state;
	private String progress;
	private String info;
	final private EpisodeDTO episode;

	public ActionProgress(final EpisodeStateEnum state, final String progress, final String info, final EpisodeDTO episode) {
		super();
		this.state = state;
		this.progress = progress;
		this.info = info;
		this.episode = episode;
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
		return progress;
	}

	public void setProgress(final String progress) {
		this.progress = progress;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(final String info) {
		this.info = info;
	}

	@Override
	public int compareTo(final ActionProgress o) {
		int ret = state.compareTo(o.state);
		if (ret == 0) {
			ret = info.compareTo(o.info);
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
			ret = this.getEpisode().equals(((ActionProgress) obj).getEpisode());
		}
		return ret;
	}

	@Override
	public int hashCode() {
		return this.getEpisode().hashCode();
	}

	@Override
	public String toString() {
		return this.getEpisode().toString() + " " + this.getState();
	}

}
