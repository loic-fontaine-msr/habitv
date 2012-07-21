package com.dabi.habitv.process.publisher;

public final class SearchEvent extends AbstractEvent {
	private final String channel;

	private final String category;

	private final SearchStateEnum state;

	private final String info;

	public SearchEvent(final String channel, final String category, final SearchStateEnum state, final String info) {
		super();
		this.channel = channel;
		this.category = category;
		this.state = state;
		this.info = info;
	}

	public SearchEvent(final String channel, final String category, final SearchStateEnum state) {
		super();
		this.channel = channel;
		this.category = category;
		this.state = state;
		info = null;
	}

	public SearchEvent(final String channel, final SearchStateEnum state) {
		this.channel = channel;
		category = null;
		this.state = state;
		info = null;
	}

	public String getChannel() {
		return channel;
	}

	public String getCategory() {
		return category;
	}

	public SearchStateEnum getState() {
		return state;
	}

	public String getInfo() {
		return info;
	}

}
