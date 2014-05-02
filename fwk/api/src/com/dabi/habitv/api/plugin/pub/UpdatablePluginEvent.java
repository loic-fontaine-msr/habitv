package com.dabi.habitv.api.plugin.pub;


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

	@Override
	public String toString() {
		return "UpdatablePluginEvent [plugin=" + plugin + ", version=" + version + ", state=" + state + ", getException()=" + getException() + "]";
	}


}
