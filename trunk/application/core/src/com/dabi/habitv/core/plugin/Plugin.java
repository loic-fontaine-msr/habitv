package com.dabi.habitv.core.plugin;

import com.dabi.habitv.api.plugin.api.PluginBaseInterface;

class Plugin {
	private final Class<PluginBaseInterface> classPluginProvider;
	private final ClassLoader classLoaders;

	public Plugin(final Class<PluginBaseInterface> classPluginProvider, final ClassLoader classLoaders) {
		super();
		this.classPluginProvider = classPluginProvider;
		this.classLoaders = classLoaders;
	}

	public Class<PluginBaseInterface> getClassPluginProvider() {
		return classPluginProvider;
	}

	public ClassLoader getClassLoaders() {
		return classLoaders;
	}

}