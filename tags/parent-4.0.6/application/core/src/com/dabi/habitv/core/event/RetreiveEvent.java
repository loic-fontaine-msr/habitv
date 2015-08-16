package com.dabi.habitv.core.event;

import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;
import com.dabi.habitv.api.plugin.pub.AbstractEvent;

public final class RetreiveEvent extends AbstractEvent {
	private final EpisodeDTO episode;

	private final EpisodeStateEnum state;

	private String operation;

	private ProcessHolder processHolder;

	public RetreiveEvent(final EpisodeDTO episode, final EpisodeStateEnum state) {
		super(null);
		this.episode = episode;
		this.state = state;
	}

	public RetreiveEvent(final EpisodeDTO episode,
			final EpisodeStateEnum state, final Throwable exception,
			final String operation) {
		this(episode, state);
		setException(exception);
		this.operation = operation;
	}

	public RetreiveEvent(final EpisodeDTO episode,
			final EpisodeStateEnum state, final String operation) {
		this(episode, state);
		this.operation = operation;
	}

	public RetreiveEvent(EpisodeDTO episode, EpisodeStateEnum state,
			ProcessHolder processHolder) {
		this(episode, state);
		this.processHolder = processHolder;
	}

	public RetreiveEvent(EpisodeDTO episode, EpisodeStateEnum state,
			String output, ProcessHolder processHolder) {
		this(episode, state, output);
		this.processHolder = processHolder;
	}

	public EpisodeDTO getEpisode() {
		return episode;
	}

	public EpisodeStateEnum getState() {
		return state;
	}

	public String getOperation() {
		return operation;
	}

	public ProcessHolder getProcessHolder() {
		return processHolder;
	}

	@Override
	public int hashCode() {
		int ret = getEpisode().hashCode();
		if (getState() != null) {
			ret += getState().hashCode();
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
		} else {
			ret = false;
		}
		return ret;
	}

	@Override
	public String toString() {
		return episode.toString() + "/" + getOperation() + "/" + "/"
				+ getState();
	}

}
