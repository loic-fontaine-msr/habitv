package com.dabi.habitv.core.mgr;

import java.util.HashMap;
import java.util.Map;

import com.dabi.habitv.core.config.UserConfig;
import com.dabi.habitv.core.event.UpdatePluginEvent;
import com.dabi.habitv.core.plugin.PluginFactory;
import com.dabi.habitv.core.updater.UpdateManager;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.PluginBase;
import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.PluginExporterInterface;
import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.api.dto.ProxyDTO;
import com.dabi.habitv.framework.plugin.api.dto.ProxyDTO.ProtocolEnum;
import com.dabi.habitv.framework.plugin.api.update.UpdatablePluginInterface;
import com.dabi.habitv.framework.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.framework.plugin.holder.ExporterPluginHolder;
import com.dabi.habitv.framework.plugin.holder.ProviderPluginHolder;
import com.dabi.habitv.framework.plugin.utils.update.UpdatablePluginEvent;
import com.dabi.habitv.framework.pub.Publisher;

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

	private void updateUpdatablePlugin(final PluginBase plugin) {
		if (UpdatablePluginInterface.class.isInstance(plugin)) {
			((UpdatablePluginInterface) plugin).update(updatablePluginPublisher, getParameters(plugin.getName()));
		}
	}

	private void loadPlugins() {
		providersHolder.setPlugins(pluginProviderFactory.loadPlugins());
		downloadersHolder.setPlugins(pluginDownloaderFactory.loadPlugins());
		exportersHolder.setPlugins(pluginExporterFactory.loadPlugins());
	}

	private Map<String, String> getParameters(final String downloaderName) {
		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloadersHolder.getBinPath(downloaderName));
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloadersHolder.getCmdProcessor());
		return parameters;
	}

	public void setProxy(final Map<ProtocolEnum, ProxyDTO> defaultProxyMap, final Map<String, Map<ProtocolEnum, ProxyDTO>> plugin2protocol2proxy) {
		// set the proxy to each plugin provider
		for (final PluginProviderInterface provider : providersHolder.getPlugins()) {
			Map<ProtocolEnum, ProxyDTO> pluginProxy = plugin2protocol2proxy.get(provider.getName());
			if (pluginProxy == null) {
				pluginProxy = defaultProxyMap;
			}
			provider.setProxy(pluginProxy);
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
