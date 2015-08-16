package com.dabi.habitv.api.plugin.holder;

import java.util.Map;

import com.dabi.habitv.api.plugin.api.PluginProviderInterface;

public final class ProviderPluginHolder extends AbstractPluginHolder<PluginProviderInterface> {

	public ProviderPluginHolder(final Map<String, PluginProviderInterface> providerName2Provider) {
		super(providerName2Provider);
	}

}
