package com.dabi.habitv.core.event;

public final class SearchEvent extends AbstractEvent {
	private final String channel;

	private final String category;

	private final SearchStateEnum state;

	private final String info;

	SearchEvent(final String channel, final String category, final SearchStateEnum state, final String info) {
		super(null);
		this.channel = channel;
		this.category = category;
		this.state = state;
		this.info = info;
	}

	public SearchEvent(final String channel, final String category, final SearchStateEnum state) {
		this(channel, category, state, null);
	}

	public SearchEvent(final String channel, final SearchStateEnum state) {
		this(channel, null, state);
	}

	public SearchEvent(final SearchStateEnum state) {
		this(null, state);
	}

	public SearchEvent(final SearchStateEnum state, final Throwable throwable) {
		super(throwable);
		channel = null;
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
