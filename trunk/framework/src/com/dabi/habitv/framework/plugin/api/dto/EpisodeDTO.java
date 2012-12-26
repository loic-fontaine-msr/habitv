package com.dabi.habitv.framework.plugin.api.dto;

import java.io.Serializable;

import com.dabi.habitv.framework.plugin.exception.InvalidEpisodeException;
import com.dabi.habitv.framework.plugin.utils.CheckUtils;

public class EpisodeDTO implements Comparable<EpisodeDTO>, Serializable {

	private static final long serialVersionUID = 1042642346549389101L;

	private final CategoryDTO category;

	private final String name;

	private final String url;

	private int num = 0;

	public EpisodeDTO(final CategoryDTO category, final String name, final String videoUrl) {
		this.category = category;
		this.name = name;
		this.url = videoUrl;
	}

	public void check() throws InvalidEpisodeException {
		if (!CheckUtils.checkMinSize(name)) {
			throw new InvalidEpisodeException(name, InvalidEpisodeException.CauseField.NAME);
		}
		if (!CheckUtils.checkMinSize(url)) {
			throw new InvalidEpisodeException(url, InvalidEpisodeException.CauseField.URL);
		}
	}

	public String getName() {
		return name;
	}

	public CategoryDTO getCategory() {
		return category;
	}

	@Override
	public int hashCode() {
		int ret;
		if (this.url != null) {
			ret = this.url.hashCode();
		} else {
			ret = category.hashCode() + name.hashCode();
		}
		return ret;
	}

	@Override
	public boolean equals(final Object obj) {
		boolean ret = false;
		if (obj instanceof EpisodeDTO) {
			final EpisodeDTO episode = (EpisodeDTO) obj;
			if (this.url != null && this.url.equals(episode.url)) {
				ret = true;
			} else {
				ret = category.equals(episode.getCategory()) && episode.getName().equals(name);
			}
		}
		return ret;
	}

	@Override
	public String toString() {
		return getCategory().getChannel() + "-" + getCategory().getName() + "-" + getName();
	}

	public String getUrl() {
		return url;
	}

	@Override
	public int compareTo(final EpisodeDTO o) {
		int ret = 0;
		if (!equals(o)) {
			ret = category.compareTo(o.category);
			if (ret == 0) {
				ret = name.compareTo(o.name);
			}
		}
		return ret;
	}

	public int getNum() {
		return num;
	}

	public void setNum(final int i) {
		this.num = i;
	}

}
