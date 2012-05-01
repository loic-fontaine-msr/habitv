package com.dabi.habitv.launcher.tray.model;

import java.util.HashMap;
import java.util.Map;

public class ProgressionModel {

	private final Map<String, Map<EpisodeStateEnum, String>> episodeName2ActionProgress = new HashMap<>();

	public void updateActionProgress(String episodeName, EpisodeStateEnum episodeStateEnum, String progress) {
		Map<EpisodeStateEnum, String> actionInProgress = episodeName2ActionProgress.get(episodeName);
		if (actionInProgress == null) {
			actionInProgress = new HashMap<>();
		}
		actionInProgress.put(episodeStateEnum, progress);
	}

	public void endAction(String episodeName, EpisodeStateEnum action) {
		Map<EpisodeStateEnum, String> actionInProgress = episodeName2ActionProgress.get(episodeName);
		if (actionInProgress != null) {
			actionInProgress.remove(action);
		}
	}

	public Map<EpisodeStateEnum, String> getAction(String name) {
		return episodeName2ActionProgress.get(name);
	}
	
	public Map<String, Map<EpisodeStateEnum, String>> getEpisodeName2ActionProgress(){
		return episodeName2ActionProgress;
	}

}
