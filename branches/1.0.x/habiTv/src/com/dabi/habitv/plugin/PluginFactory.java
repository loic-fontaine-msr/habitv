package com.dabi.habitv.plugin;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dabi.habitv.framework.plugin.api.PluginBase;

public final class PluginFactory<P extends PluginBase> {

	private final Map<String, P> pluginName2Plugin = new HashMap<>();

	public PluginFactory(final Class<P> pluginInterface, final String pluginDir) {
		final List<P> pluginProviderInterList = new PluginsLoader<>(pluginInterface, new File(pluginDir).listFiles()).loadAllProviderPlugins();
		for (P plugin : pluginProviderInterList) {
			pluginName2Plugin.put(plugin.getName(), plugin);
		}
	}

	public P findPlugin(final String pluginName, final String defaultValue) {
		P plugin = pluginName2Plugin.get(pluginName);
		if (plugin == null) {
			if (defaultValue == null) {
				throw new IllegalArgumentException("Plugin not found " + pluginName);
			}
			plugin = findPlugin(defaultValue, null);
		}
		return plugin;
	}

	public Collection<P> getAllPlugin() {
		return pluginName2Plugin.values();
	}

	public Map<String, P> getAllPluginMap() {
		return pluginName2Plugin;
	}
}
