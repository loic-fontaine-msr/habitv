package com.dabi.habitv.core.mgr;

import java.util.Collection;
import java.util.Map;

import com.dabi.habitv.api.plugin.api.PluginBaseInterface;
import com.dabi.habitv.api.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.api.plugin.api.PluginExporterInterface;
import com.dabi.habitv.api.plugin.api.PluginProviderInterface;
import com.dabi.habitv.api.plugin.api.PluginWithProxyInterface;
import com.dabi.habitv.api.plugin.api.UpdatablePluginInterface;
import com.dabi.habitv.api.plugin.dto.ProxyDTO;
import com.dabi.habitv.api.plugin.dto.ProxyDTO.ProtocolEnum;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.holder.ExporterPluginHolder;
import com.dabi.habitv.api.plugin.holder.ProviderPluginHolder;
import com.dabi.habitv.api.plugin.pub.Publisher;
import com.dabi.habitv.api.plugin.pub.UpdatablePluginEvent;
import com.dabi.habitv.core.config.UserConfig;
import com.dabi.habitv.core.event.UpdatePluginEvent;
import com.dabi.habitv.core.plugin.PluginFactory;
import com.dabi.habitv.core.updater.UpdateManager;

public class PluginManager {

	private final UpdateManager updateManager;

	private final Publisher<UpdatablePluginEvent> updatablePluginPublisher = new Publisher<>();

	private final PluginFactory<PluginProviderInterface> pluginProviderFactory;

	private final PluginFactory<PluginDownloaderInterface> pluginDownloaderFactory;

	private final PluginFactory<PluginExporterInterface> pluginExporterFactory;

	private final DownloaderPluginHolder downloadersHolder;

	private final ExporterPluginHolder exportersHolder;

	private final ProviderPluginHolder providersHolder;

	public PluginManager(final UserConfig config) {
		updateManager = new UpdateManager(config.autoriseSnapshot());

		// downloader
		pluginDownloaderFactory = new PluginFactory<>(PluginDownloaderInterface.class, config.getDownloaderPluginDir());
		downloadersHolder = new DownloaderPluginHolder(config.getCmdProcessor(), pluginDownloaderFactory.loadPlugins(), config.getDownloader(),
				config.getDownloadOuput(), config.getIndexDir());

		// provider
		pluginProviderFactory = new PluginFactory<>(PluginProviderInterface.class, config.getProviderPluginDir());
		providersHolder = new ProviderPluginHolder(pluginProviderFactory.loadPlugins());

		// exporter
		pluginExporterFactory = new PluginFactory<>(PluginExporterInterface.class, config.getExporterPluginDir());
		// export DTO
		exportersHolder = new ExporterPluginHolder(pluginExporterFactory.loadPlugins(), config.getExporter());

	}

	public void update() {
		updateManager.process();
		loadPlugins();
		pluginAutoUpdate();
	}

	private void pluginAutoUpdate() {
		for (final PluginDownloaderInterface downloader : downloadersHolder.getPlugins()) {
			updateUpdatablePlugin(downloader);
		}
		for (final PluginExporterInterface exporter : exportersHolder.getPlugins()) {
			updateUpdatablePlugin(exporter);
		}
		for (final PluginProviderInterface provider : providersHolder.getPlugins()) {
			updateUpdatablePlugin(provider);
		}
	}

	private void updateUpdatablePlugin(final PluginBaseInterface plugin) {
		if (UpdatablePluginInterface.class.isInstance(plugin)) {
			((UpdatablePluginInterface) plugin).update(updatablePluginPublisher, downloadersHolder);
		}
	}

	private void loadPlugins() {
		providersHolder.setPlugins(pluginProviderFactory.loadPlugins());
		downloadersHolder.setPlugins(pluginDownloaderFactory.loadPlugins());
		exportersHolder.setPlugins(pluginExporterFactory.loadPlugins());
	}

	public void setProxy(final Map<ProtocolEnum, ProxyDTO> defaultProxyMap, final Map<String, Map<ProtocolEnum, ProxyDTO>> plugin2protocol2proxy) {
		setProxy(defaultProxyMap, plugin2protocol2proxy, providersHolder.getPlugins());
	}

	private void setProxy(final Map<ProtocolEnum, ProxyDTO> defaultProxyMap, final Map<String, Map<ProtocolEnum, ProxyDTO>> plugin2protocol2proxy,
			final Collection<? extends PluginBaseInterface> plugins) {
		// set the proxy to each plugin
		for (final PluginBaseInterface plugin : plugins) {
			if (PluginWithProxyInterface.class.isInstance(plugin)) {
				final PluginWithProxyInterface pluginWithProxy = (PluginWithProxyInterface) plugin;
				Map<ProtocolEnum, ProxyDTO> proxies = plugin2protocol2proxy.get(plugin.getName());
				if (proxies == null) {
					proxies = defaultProxyMap;
				}
				pluginWithProxy.setProxies(proxies);
			}
		}
	}

	public DownloaderPluginHolder getDownloadersHolder() {
		return downloadersHolder;
	}

	public ExporterPluginHolder getExportersHolder() {
		return exportersHolder;
	}

	public ProviderPluginHolder getProvidersHolder() {
		return providersHolder;
	}

	public Publisher<UpdatablePluginEvent> getUpdatablePluginPublisher() {
		return updatablePluginPublisher;
	}

	public Publisher<UpdatePluginEvent> getUpdatePluginPublisher() {
		return updateManager.getUpdatePublisher();
	}

}
