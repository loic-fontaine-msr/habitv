package com.dabi.habitv.core.event;

import com.dabi.habitv.api.plugin.pub.AbstractEvent;

public final class UpdatePluginEvent extends AbstractEvent {
	private final String plugin;

	private final String version;

	private final UpdatePluginStateEnum state;

	private final String info;

	public UpdatePluginEvent(final Throwable exception, final String plugin,
			final String version, final String info) {
		super(exception);
		this.plugin = plugin;
		this.version = version;
		this.info = info;
		this.state = UpdatePluginStateEnum.ERROR;
	}

	public UpdatePluginEvent(final String plugin, final String version,
			final UpdatePluginStateEnum state) {
		super();
		this.plugin = plugin;
		this.version = version;
		this.state = state;
		this.info = null;
	}

	public UpdatePluginEvent(final UpdatePluginStateEnum state) {
		super();
		this.state = state;
		this.version = null;
		this.plugin = null;
		this.info = null;
	}

	public UpdatePluginEvent(UpdatePluginStateEnum state, int length) {
		super();
		this.state = state;
		this.version = null;
		this.plugin = null;
		this.info = String.valueOf(length);
	}

	public String getPlugin() {
		return plugin;
	}

	public String getVersion() {
		return version;
	}

	public UpdatePluginStateEnum getState() {
		return state;
	}

	public String getInfo() {
		return info;
	}

}
