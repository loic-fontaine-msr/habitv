package com.dabi.habitv.api.plugin.pub;


public final class UpdatablePluginEvent extends AbstractEvent {

	public enum UpdatablePluginStateEnum {
		CHECKING, DOWNLOADING, DONE, ERROR, STARTING_ALL, ALL_DONE;
	}

	private final String plugin;

	private final String version;

	private final UpdatablePluginStateEnum state;

	private final String info;

	public UpdatablePluginEvent(final String plugin, final String version,
			final UpdatablePluginStateEnum state) {
		super();
		this.plugin = plugin;
		this.version = version;
		this.state = state;
		this.info = null;
	}

	public UpdatablePluginEvent(UpdatablePluginStateEnum state) {
		this.plugin = null;
		this.version = null;
		this.state = state;
		this.info = null;
	}

	public UpdatablePluginEvent(UpdatablePluginStateEnum state,
			final String info) {
		this.plugin = null;
		this.version = null;
		this.state = state;
		this.info = info;
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

	public String getInfo() {
		return info;
	}

	@Override
	public String toString() {
		return "UpdatablePluginEvent [plugin=" + plugin + ", version="
				+ version + ", state=" + state + ", getException()="
				+ getException() + "]";
	}

}
