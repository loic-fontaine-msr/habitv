package com.dabi.habitv.launcher.tray.model;

import java.util.HashMap;
import java.util.Map;

import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public class ProgressionModel {

	public class ActionProgress {
		private EpisodeStateEnum state;
		private String progress;
		private String info;

		public ActionProgress(final EpisodeStateEnum state, final String progress, final String info) {
			super();
			this.state = state;
			this.progress = progress;
			this.info = info;
		}

		public EpisodeStateEnum getState() {
			return state;
		}

		public void setState(EpisodeStateEnum state) {
			this.state = state;
		}

		public String getProgress() {
			return progress;
		}

		public void setProgress(String progress) {
			this.progress = progress;
		}

		public String getInfo() {
			return info;
		}

		public void setInfo(String info) {
			this.info = info;
		}
		
	}

	private final Map<EpisodeDTO, ActionProgress> episodeName2ActionProgress = new HashMap<>();

	public void updateActionProgress(final EpisodeDTO episode, final EpisodeStateEnum state, final String info, final String progression) {
		ActionProgress actionInProgress = episodeName2ActionProgress.get(episode);
		if (actionInProgress == null) {
			actionInProgress = new ActionProgress(state, progression, info);
		} else {
			actionInProgress.setState(state);
			actionInProgress.setProgress(progression);
			actionInProgress.setInfo(info);
		}
		episodeName2ActionProgress.put(episode, actionInProgress);
	}

	public ActionProgress getAction(String name) {
		return episodeName2ActionProgress.get(name);
	}

	public Map<EpisodeDTO, ActionProgress> getEpisodeName2ActionProgress() {
		return episodeName2ActionProgress;
	}

}
