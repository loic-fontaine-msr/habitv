package com.dabi.habitv.tray.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.core.event.EpisodeStateEnum;

public class ProgressionModel {

	private final List<ActionProgress> episodeName2ActionProgress = Collections
			.synchronizedList(new LinkedList<ActionProgress>());

	public void updateActionProgress(final EpisodeDTO episode,
			final EpisodeStateEnum state, final String info,
			final String progression) {
		ActionProgress actionInProgress = getAction(episode);
		if (actionInProgress == null) {
			actionInProgress = new ActionProgress(state, progression, info,
					episode);
			episodeName2ActionProgress.add(actionInProgress);
		} else {
			actionInProgress.setState(state);
			actionInProgress.setProgress(progression);
			actionInProgress.setInfo(info);
		}
		Collections.sort(episodeName2ActionProgress);
	}

	public ActionProgress getAction(final EpisodeDTO episode) {
		ActionProgress ret = null;
		for (final ActionProgress actionProgress : getEpisodeName2ActionProgress()) {
			if (actionProgress.getEpisode().equals(episode)) {
				ret = actionProgress;
			}
		}
		return ret;
	}

	public Collection<ActionProgress> getEpisodeName2ActionProgress() {
		return new ArrayList<>(episodeName2ActionProgress);
	}

	public boolean isAllActionDone() {
		boolean ret = true;
		for (final ActionProgress actionProgress : getEpisodeName2ActionProgress()) {
			if (!isActionDone(actionProgress)) {
				ret = false;
				break;
			}
		}
		return ret;
	}

	private boolean isActionDone(final ActionProgress actionProgress) {
		final EpisodeStateEnum state = actionProgress.getState();
		return state != null
				&& (state.equals(EpisodeStateEnum.READY)
						|| state.equals(EpisodeStateEnum.EXPORT_FAILED) || state
						.equals(EpisodeStateEnum.DOWNLOAD_FAILED));
	}

	public void clear() {
		final Iterator<ActionProgress> it = episodeName2ActionProgress
				.iterator();
		ActionProgress actionProgress;
		while (it.hasNext()) {
			actionProgress = it.next();
			if (isActionDone(actionProgress)) {
				it.remove();
			}
		}
	}

}
