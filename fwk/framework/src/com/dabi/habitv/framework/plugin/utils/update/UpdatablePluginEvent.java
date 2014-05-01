package com.dabi.habitv.framework.plugin.utils.update;

import com.dabi.habitv.framework.pub.AbstractEvent;

public final class UpdatablePluginEvent extends AbstractEvent {

	public enum UpdatablePluginStateEnum {
		CHECKING, DOWNLOADING, DONE, ERROR;
	}

	private final String plugin;

	private final String version;

	private final UpdatablePluginStateEnum state;

	public UpdatablePluginEvent(final String plugin, final String version, final UpdatablePluginStateEnum state) {
		super();
		this.plugin = plugin;
		this.version = version;
		this.state = state;
	}

	public String getPlugin() {
		return plugin;
	}

	public String getVersion() {
		return version;
	}

	public UpdatablePluginStateEnum getState() {
		return state;
	}

}
