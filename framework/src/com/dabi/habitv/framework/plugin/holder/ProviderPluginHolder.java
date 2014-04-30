package com.dabi.habitv.framework.plugin.holder;

import java.util.Map;

import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;

public final class ProviderPluginHolder extends AbstractPluginHolder<PluginProviderInterface> {

	public ProviderPluginHolder(final Map<String, PluginProviderInterface> providerName2Provider) {
		super(providerName2Provider);
	}

}
