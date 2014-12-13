package com.dabi.habitv.core.task;

import com.dabi.habitv.api.plugin.dto.EpisodeDTO;

abstract class AbstractEpisodeTask extends AbstractTask<Object> {

	private final EpisodeDTO episode;

	AbstractEpisodeTask(final EpisodeDTO episode) {
		this.episode = episode;
	}

	public EpisodeDTO getEpisode() {
		return episode;
	}

}
