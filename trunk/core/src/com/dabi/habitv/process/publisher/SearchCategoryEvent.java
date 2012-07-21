package com.dabi.habitv.process.publisher;

public final class SearchCategoryEvent extends AbstractEvent {

	private final String channel;

	private final String category;

	private final SearchCategoryStateEnum state;

	private final String info;

	public SearchCategoryEvent(final String channel, final String category, final SearchCategoryStateEnum state, final String info) {
		super();
		this.channel = channel;
		this.category = category;
		this.state = state;
		this.info = info;
	}

	public SearchCategoryEvent(final String channel, final String category, final SearchCategoryStateEnum state) {
		super();
		this.channel = channel;
		this.category = category;
		this.state = state;
		info = null;
	}

	public SearchCategoryEvent(final SearchCategoryStateEnum state, final String info) {
		channel = null;
		category = null;
		this.state = state;
		this.info = info;
	}

	public String getChannel() {
		return channel;
	}

	public String getCategory() {
		return category;
	}

	public SearchCategoryStateEnum getState() {
		return state;
	}

	public String getInfo() {
		return info;
	}

}
