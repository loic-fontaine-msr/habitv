package com.dabi.habitv.core.dao;

import java.io.Serializable;

import com.dabi.habitv.api.plugin.dto.EpisodeDTO;

public class EpisodeExportState implements Serializable {

	private static final long serialVersionUID = -2893666909826680016L;

	private final EpisodeDTO episode;

	private final Integer state;

	public EpisodeExportState(EpisodeDTO episode, Integer state) {
		super();
		this.episode = episode;
		this.state = state;
	}

	public EpisodeDTO getEpisode() {
		return episode;
	}

	public Integer getState() {
		return state;
	}

	@Override
	public boolean equals(Object arg0) {
		EpisodeExportState other = (EpisodeExportState) arg0;
		return getEpisode().equals(other.getEpisode()) && getState().equals(other.getState());
	}

	@Override
	public int hashCode() {
		return getEpisode().hashCode() + getState().hashCode();
	}

}
