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

	@Override
	public int hashCode() {
		int ret = getEpisode().hashCode();
		if (getState() != null) {
			ret += getState().hashCode();
		}
		if (getProgress() != null) {
			ret += getProgress().hashCode();
		}
		if (getOperation() != null) {
			ret += getOperation().hashCode();
		}
		return ret;
	}

	@Override
	public boolean equals(final Object obj) {
		boolean ret;
		if (obj instanceof RetreiveEvent) {
			final RetreiveEvent event = (RetreiveEvent) obj;
			ret = getEpisode().equals(event.getEpisode());
			if (getState() != null) {
				ret = ret && getState().equals(event.getState());
			}
			if (getOperation() != null) {
				ret = ret && getOperation().equals(event.getOperation());
			}
			if (getProgress() != null) {
				ret = ret && getProgress().equals(event.getProgress());
			}
		} else {
			ret = false;
		}
		return ret;
	}

	@Override
	public String toString() {
		return episode.toString() + "/" + getOperation() + "/" + getProgress() + "/" + getState();
	}

}
