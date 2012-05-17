package com.dabi.habitv.framework.plugin.api.dto;

import java.io.Serializable;

import com.dabi.habitv.framework.plugin.exception.InvalidEpisodeException;
import com.dabi.habitv.framework.plugin.utils.CheckUtils;

public class EpisodeDTO implements Comparable<EpisodeDTO>, Serializable {

	private static final long serialVersionUID = -202519171479125349L;

	private final String category;

	private final String name;

	private final String videoUrl;

	public EpisodeDTO(final String category, final String name, final String videoUrl) {
		this.category = category;
		this.name = name;
		this.videoUrl = videoUrl;
	}

	public void check() throws InvalidEpisodeException {
		if (!CheckUtils.checkMinSize(category)) {
			throw new InvalidEpisodeException(category, InvalidEpisodeException.CauseField.CATEGORY);
		}
		if (!CheckUtils.checkMinSize(name)) {
			throw new InvalidEpisodeException(name, InvalidEpisodeException.CauseField.NAME);
		}
		if (!CheckUtils.checkMinSize(videoUrl)) {
			throw new InvalidEpisodeException(videoUrl, InvalidEpisodeException.CauseField.URL);
		}
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	@Override
	public int hashCode() {
		return this.category.hashCode() + this.name.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		boolean ret = false;
		if (obj instanceof EpisodeDTO) {
			final EpisodeDTO episode = (EpisodeDTO) obj;
			ret = this.category.equals(episode.getCategory()) && episode.getName().equals(this.name);
		}
		return ret;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	@Override
	public int compareTo(final EpisodeDTO o) {
		int ret = category.compareTo(o.category);
		if (ret == 0) {
			ret = name.compareTo(o.name);
		}
		return ret;
	}

}
