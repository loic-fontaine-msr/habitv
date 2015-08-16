package com.dabi.habitv.core.mgr;

import com.dabi.habitv.api.plugin.holder.ProviderPluginHolder;

abstract class AbstractManager {

	private final ProviderPluginHolder providerPluginHolder;

	AbstractManager(final ProviderPluginHolder providerPluginHolder) {
		// plugin provider
		this.providerPluginHolder = providerPluginHolder;
	}

	public ProviderPluginHolder getProviderPluginHolder() {
		return providerPluginHolder;
	}

}
