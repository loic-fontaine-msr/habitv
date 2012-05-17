package com.dabi.habitv.launcher.tray;

import java.util.EventObject;

import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.launcher.tray.model.EpisodeStateEnum;
import com.dabi.habitv.launcher.tray.model.HabitTvTrayModel;

public class EpisodeChangedEvent extends EventObject {

	private static final long serialVersionUID = -3874255183750587032L;

	private final EpisodeStateEnum state;
	
	private final EpisodeDTO episode;

	public EpisodeChangedEvent(final HabitTvTrayModel source, final EpisodeDTO episode, final EpisodeStateEnum state) {
		super(source);
		this.state = state;
		this.episode = episode;
	}

	public EpisodeDTO getEpisode() {
		return episode;
	}

	public EpisodeStateEnum getState() {
		return state;
	}
}
