package com.dabi.habitv.core.dao;

import java.io.Serializable;
import java.util.ArrayList;

public class EpisodeExportIndexRoot implements Serializable {

	private static final long serialVersionUID = 511460727727263392L;

	private ArrayList<EpisodeExportState> episodeExportStates = new ArrayList<>();

	public ArrayList<EpisodeExportState> getEpisodeExportStates() {
		return episodeExportStates;
	}

	public void addEpisodeExportStates(EpisodeExportState episodeExportState) {
		this.episodeExportStates.add(episodeExportState);
	}

}
