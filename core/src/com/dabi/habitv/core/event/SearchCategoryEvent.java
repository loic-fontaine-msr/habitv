package com.dabi.habitv.core.event;

import com.dabi.habitv.api.plugin.pub.AbstractEvent;

public final class SearchCategoryEvent extends AbstractEvent {

	private final String plugin;

	private final SearchCategoryStateEnum state;

	private final String info;

	public SearchCategoryEvent(final String channel,
			final SearchCategoryStateEnum state) {
		super(null);
		this.plugin = channel;
		this.state = state;
		info = null;
	}

	public SearchCategoryEvent(final SearchCategoryStateEnum state,
			final String info) {
		super(null);
		plugin = null;
		this.state = state;
		this.info = info;
	}

	public SearchCategoryEvent(SearchCategoryStateEnum state) {
		this(state, null);
	}

	public String getPlugin() {
		return plugin;
	}

	public SearchCategoryStateEnum getState() {
		return state;
	}

	public String getInfo() {
		return info;
	}

}
