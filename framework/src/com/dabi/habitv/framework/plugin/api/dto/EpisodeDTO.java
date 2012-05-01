package com.dabi.habitv.framework.plugin.api.dto;

public class EpisodeDTO {

	private final String category;

	private final String name;

	private final String videoUrl;

	public EpisodeDTO(final String category, final String name, final String videoUrl) {
		this.category = category;
		this.name = name;
		this.videoUrl = videoUrl;
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

}
