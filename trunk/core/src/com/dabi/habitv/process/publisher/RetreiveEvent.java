package com.dabi.habitv.process.publisher;

import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public final class RetreiveEvent extends AbstractEvent {
	private final EpisodeDTO episode;

	private final EpisodeStateEnum state;

	private final String progress;

	public RetreiveEvent(final EpisodeDTO episode, final EpisodeStateEnum state, final String progress) {
		super();
		this.episode = episode;
		this.state = state;
		this.progress = progress;
	}

	public RetreiveEvent(final EpisodeDTO episode, final EpisodeStateEnum state) {
		super();
		this.episode = episode;
		this.state = state;
		progress = null;
	}

	public EpisodeDTO getEpisode() {
		return episode;
	}

	public EpisodeStateEnum getState() {
		return state;
	}

	public String getProgress() {
		return progress;
	}

}
