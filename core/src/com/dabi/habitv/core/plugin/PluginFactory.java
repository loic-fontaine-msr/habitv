package com.dabi.habitv.core.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dabi.habitv.framework.plugin.api.PluginBase;

public final class PluginFactory<P extends PluginBase> {

	private final Map<String, P> pluginName2Plugin = new HashMap<>();
	private final Class<P> pluginInterface;
	private final String pluginDir;

	public PluginFactory(final Class<P> pluginInterface, final String pluginDir) {
		this.pluginInterface = pluginInterface;
		this.pluginDir = pluginDir;
	}


	public Map<String, P> loadPlugins() {
		pluginName2Plugin.clear();
		final List<P> pluginProviderInterList = new PluginsLoader<>(pluginInterface, new File(pluginDir).listFiles()).loadAllPlugins();
		for (final P plugin : pluginProviderInterList) {
			pluginName2Plugin.put(plugin.getName(), plugin);
		}
		return pluginName2Plugin;
	}

}
