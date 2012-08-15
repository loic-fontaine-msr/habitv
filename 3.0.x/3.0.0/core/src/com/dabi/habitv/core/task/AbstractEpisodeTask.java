package com.dabi.habitv.core.task;

import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public abstract class AbstractEpisodeTask extends AbstractTask<Object> {

	private final EpisodeDTO episode;

	public AbstractEpisodeTask(final EpisodeDTO episode) {
		this.episode = episode;
	}

	public EpisodeDTO getEpisode() {
		return episode;
	}

}
