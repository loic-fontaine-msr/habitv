package com.dabi.habitv.core.event;

import com.dabi.habitv.api.plugin.pub.AbstractEvent;

public final class SearchCategoryEvent extends AbstractEvent {

	private final String channel;

	private final SearchCategoryStateEnum state;

	private final String info;

	public SearchCategoryEvent(final String channel, final SearchCategoryStateEnum state) {
		super(null);
		this.channel = channel;
		this.state = state;
		info = null;
	}

	public SearchCategoryEvent(final SearchCategoryStateEnum state, final String info) {
		super(null);
		channel = null;
		this.state = state;
		this.info = info;
	}

	public String getChannel() {
		return channel;
	}

	public SearchCategoryStateEnum getState() {
		return state;
	}

	public String getInfo() {
		return info;
	}

}
