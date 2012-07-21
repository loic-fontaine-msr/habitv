package com.dabi.habitv.process.task;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;

public abstract class AbstractManager {

	private static final Logger LOGGER = Logger.getLogger(EpisodeManager.class);

	private final Collection<PluginProviderInterface> pluginProviderList;

	protected AbstractManager(final Collection<PluginProviderInterface> pluginProviderList) {
		// plugin provider
		this.pluginProviderList = pluginProviderList;
	}

	protected static Logger getLogger() {
		return LOGGER;
	}

	protected Collection<PluginProviderInterface> getPluginProviderList() {
		return pluginProviderList;
	}

}
