package com.dabi.habitv.core.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dabi.habitv.api.plugin.api.PluginBaseInterface;

public final class PluginFactory {

	private final String pluginDir;
	private List<? extends PluginBaseInterface> plugins;

	private static final Logger LOG = Logger.getLogger(PluginFactory.class);

	public PluginFactory(final String pluginDir) {
		this.pluginDir = pluginDir;
		init();
	}

	public void init() {
		plugins = new PluginsLoader(new File(pluginDir).listFiles()).loadAllPlugins();
	}

	public <P extends PluginBaseInterface> Map<String, P> loadPlugins(final Class<P> pluginClass) {
		final Map<String, P> pluginName2plugins = new HashMap<>();
		for (final PluginBaseInterface plugin : plugins) {
			if (pluginClass.isInstance(plugin)) {
				pluginName2plugins.put(plugin.getName(), pluginClass.cast(plugin));
			}
		}
		if (pluginName2plugins.isEmpty()) {
			LOG.info("Aucun plugin " + pluginClass.getSimpleName());
		}
		return pluginName2plugins;
	}

}
