package com.dabi.habitv.process.task;

import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public abstract class AbstractEpisodeTask extends AbstractTask<Object> {

	private final EpisodeDTO episode;

	public AbstractEpisodeTask(final EpisodeDTO episode) {
		this.episode = episode;
	}

	protected EpisodeDTO getEpisode() {
		return episode;
	}

}
