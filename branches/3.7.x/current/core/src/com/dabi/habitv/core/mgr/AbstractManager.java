package com.dabi.habitv.core.mgr;

import java.util.Collection;

import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;

abstract class AbstractManager {

private final Collection<PluginProviderInterface> pluginProviderList;

	AbstractManager(final Collection<PluginProviderInterface> pluginProviderList) {
		// plugin provider
		this.pluginProviderList = pluginProviderList;
	}

	protected Collection<PluginProviderInterface> getPluginProviderList() {
		return pluginProviderList;
	}

}
