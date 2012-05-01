package com.dabi.habitv.launcher.tray.model;

import java.util.HashMap;
import java.util.Map;

import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public class ProgressionModel {

	private final Map<EpisodeDTO, Map<EpisodeStateEnum, String>> episodeName2ActionProgress = new HashMap<>();

	public void updateActionProgress(EpisodeDTO episode, EpisodeStateEnum episodeStateEnum, String progress) {
		Map<EpisodeStateEnum, String> actionInProgress = episodeName2ActionProgress.get(episode);
		if (actionInProgress == null) {
			actionInProgress = new HashMap<>();
		}
		actionInProgress.put(episodeStateEnum, progress);
		episodeName2ActionProgress.put(episode, actionInProgress);
	}

	public void endAction(EpisodeDTO episode, EpisodeStateEnum action) {
		Map<EpisodeStateEnum, String> actionInProgress = episodeName2ActionProgress.get(episode);
		if (actionInProgress != null) {
			actionInProgress.remove(action);
			if (actionInProgress.isEmpty()) {
				episodeName2ActionProgress.remove(episode);
			}
		}
	}

	public Map<EpisodeStateEnum, String> getAction(String name) {
		return episodeName2ActionProgress.get(name);
	}

	public Map<EpisodeDTO, Map<EpisodeStateEnum, String>> getEpisodeName2ActionProgress() {
		return episodeName2ActionProgress;
	}

}
