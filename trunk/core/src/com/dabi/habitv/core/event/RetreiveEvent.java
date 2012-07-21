package com.dabi.habitv.core.event;

import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public final class RetreiveEvent extends AbstractEvent {
	private final EpisodeDTO episode;

	private final EpisodeStateEnum state;

	private final String progress;

	private String operation;

	public RetreiveEvent(final EpisodeDTO episode, final EpisodeStateEnum state, final String progress) {
		super(null);
		this.episode = episode;
		this.state = state;
		this.progress = progress;
	}

	public RetreiveEvent(final EpisodeDTO episode, final EpisodeStateEnum state) {
		this(episode, state, (String) null);
	}

	public RetreiveEvent(final EpisodeDTO episode, final EpisodeStateEnum state, final Exception exception, final String operation) {
		this(episode, state);
		setException(exception);
		this.operation = operation;
	}

	public RetreiveEvent(final EpisodeDTO episode, final EpisodeStateEnum state, final String operation, final String progression) {
		this(episode, state, progression);
		this.operation = operation;
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

	public String getOperation() {
		return operation;
	}

}
