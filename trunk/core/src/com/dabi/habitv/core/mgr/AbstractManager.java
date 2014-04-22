package com.dabi.habitv.core.mgr;

import java.util.Collection;

import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;

abstract class AbstractManager {

	private Collection<PluginProviderInterface> pluginProviderList;

	AbstractManager(final Collection<PluginProviderInterface> pluginProviderList) {
		// plugin provider
		this.pluginProviderList = pluginProviderList;
	}

	public Collection<PluginProviderInterface> getPluginProviderList() {
		return pluginProviderList;
	}

	public void setPluginProviderList(final Collection<PluginProviderInterface> pluginProviderList) {
		this.pluginProviderList = pluginProviderList;
	}

}
