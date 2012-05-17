package com.dabi.habitv.launcher.tray.model;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public class ProgressionModel {

	private final Collection<ActionProgress> episodeName2ActionProgress = Collections.synchronizedCollection(new TreeSet<ActionProgress>());

	public void updateActionProgress(final EpisodeDTO episode, final EpisodeStateEnum state, final String info, final String progression) {
		ActionProgress actionInProgress = getAction(episode);
		if (actionInProgress == null) {
			actionInProgress = new ActionProgress(state, progression, info, episode);
			episodeName2ActionProgress.add(actionInProgress);
		} else {
			actionInProgress.setState(state);
			actionInProgress.setProgress(progression);
			actionInProgress.setInfo(info);
		}
	}

	public ActionProgress getAction(final EpisodeDTO episode) {
		ActionProgress ret = null;
		for (ActionProgress actionProgress : episodeName2ActionProgress) {
			if (actionProgress.getEpisode().equals(episode)) {
				ret = actionProgress;
			}
		}
		return ret;
	}

	public Collection<ActionProgress> getEpisodeName2ActionProgress() {
		return episodeName2ActionProgress;
	}

}
