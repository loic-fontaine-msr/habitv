package com.dabi.habitv.core.plugin;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dabi.habitv.framework.plugin.api.PluginBase;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.utils.update.UpdatablePluginEvent;
import com.dabi.habitv.framework.pub.Publisher;

public final class PluginFactory<P extends PluginBase> {

	private final Map<String, P> pluginName2Plugin = new HashMap<>();
	private final Class<P> pluginInterface;
	private final String pluginDir;
	private boolean updatePlugin;

	public PluginFactory(final Class<P> pluginInterface, final String pluginDir, boolean updatePlugin) {
		this.pluginInterface = pluginInterface;
		this.pluginDir = pluginDir;
		this.updatePlugin = updatePlugin;
	}

	public Collection<P> getAllPlugin() {
		return pluginName2Plugin.values();
	}

	public Map<String, P> getAllPluginMap() {
		return pluginName2Plugin;
	}

	public void loadPlugins(final DownloaderDTO downloaderDTO, final Publisher<UpdatablePluginEvent> updatePublisher) {
		pluginName2Plugin.clear();
		final List<P> pluginProviderInterList = new PluginsLoader<>(pluginInterface, new File(pluginDir).listFiles(), downloaderDTO, updatePublisher)
				.loadAllProviderPlugins(updatePlugin);
		for (final P plugin : pluginProviderInterList) {
			pluginName2Plugin.put(plugin.getName(), plugin);
		}
	}

}
