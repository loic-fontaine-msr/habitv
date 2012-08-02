package com.dabi.habitv.core.mgr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.config.entities.Downloader;
import com.dabi.habitv.config.entities.Exporter;
import com.dabi.habitv.config.entities.SimultaneousTaskNumber;
import com.dabi.habitv.core.plugin.PluginFactory;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.ExportDTO;
import com.dabi.habitv.framework.plugin.api.dto.ExporterDTO;
import com.dabi.habitv.framework.plugin.api.exporter.PluginExporterInterface;
import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;

public final class CoreManager {

	private CategoryManager categoryManager;

	private EpisodeManager episodeManager;

	private final Config config;

	private final Collection<PluginProviderInterface> providerList;

	private final Map<String, Integer> buildTaskName2PoolSizeMap;

	public CoreManager(final Config config) {
		this.config = config;
		final PluginFactory<PluginProviderInterface> pluginProviderFactory = new PluginFactory<>(PluginProviderInterface.class, config.getProviderPluginDir());
		providerList = pluginProviderFactory.getAllPlugin();
		buildTaskName2PoolSizeMap = buildTaskName2PoolSizeMap(config.getSimultaneousTaskNumber());
	}

	public CategoryManager getCategoryManager() {
		if (categoryManager == null) {
			categoryManager = new CategoryManager(providerList, buildTaskName2PoolSizeMap);
		}
		return categoryManager;
	}

	private Map<String, Integer> buildTaskName2PoolSizeMap(final List<SimultaneousTaskNumber> simultaneousTaskNumberList) {
		final Map<String, Integer> taskName2PoolSizeMap = new HashMap<>();
		for (final SimultaneousTaskNumber simultaneousTaskNumber : simultaneousTaskNumberList) {
			taskName2PoolSizeMap.put(simultaneousTaskNumber.getTaskName(), simultaneousTaskNumber.getSize());
		}
		return taskName2PoolSizeMap;
	}

	private List<ExportDTO> buildExporterListDTO(final List<Exporter> exporterList) {
		final List<ExportDTO> exportDTOList = new ArrayList<>(exporterList.size());
		ExportDTO exportDTO;
		String reference;
		String pattern;
		for (final Exporter exporter : exporterList) {

			if (exporter.getCondition() != null) {
				reference = exporter.getCondition().getReference();
				pattern = exporter.getCondition().getPattern();
			} else {
				reference = null;
				pattern = null;
			}
			exportDTO = new ExportDTO(reference, pattern, exporter.getName(), exporter.getOutput(), exporter.getCmd(),
					buildExporterListDTO(exporter.getExporter()));
			exportDTOList.add(exportDTO);
		}
		return exportDTOList;
	}

	public EpisodeManager getEpisodeManager() {
		if (episodeManager == null) {
			initEpisodeManager(providerList, buildTaskName2PoolSizeMap);
		}
		return episodeManager;
	}

	private Map<String, String> buildDownloadersBinPath(final List<Downloader> downloaders) {
		final Map<String, String> downloaderName2BinPath = new HashMap<>(downloaders.size());
		for (final Downloader downloader : downloaders) {
			downloaderName2BinPath.put(downloader.getName(), downloader.getBinPath());
		}
		return downloaderName2BinPath;
	}

	private void initEpisodeManager(final Collection<PluginProviderInterface> collection, final Map<String, Integer> taskName2PoolSize) {
		// downloaders factory
		final PluginFactory<PluginDownloaderInterface> pluginDownloaderFactory = new PluginFactory<>(PluginDownloaderInterface.class,
				config.getDownloaderPluginDir());
		// map of downloaders by name
		final Map<String, PluginDownloaderInterface> downloaderName2downloader = pluginDownloaderFactory.getAllPluginMap();
		// downloaders bin path
		final Map<String, String> downloaderName2BinPath = buildDownloadersBinPath(config.getDownloader());
		// DL DTO
		final DownloaderDTO downloader = new DownloaderDTO(downloaderName2downloader, downloaderName2BinPath, config.getDownloadOuput(), config.getIndexDir());
		// exporters factory
		final PluginFactory<PluginExporterInterface> pluginExporterFactory = new PluginFactory<>(PluginExporterInterface.class, config.getExporterPluginDir());
		// map of exporters by name
		final Map<String, PluginExporterInterface> exporterName2exporter = pluginExporterFactory.getAllPluginMap();
		// export DTO
		final ExporterDTO exporter = new ExporterDTO(exporterName2exporter, buildExporterListDTO(config.getExporter()));

		// manager
		episodeManager = new EpisodeManager(downloader, exporter, collection, taskName2PoolSize);
	}

	public void retreiveEpisode(final Map<String, Set<CategoryDTO>> categoriesToGrab) {
		getEpisodeManager().retreiveEpisode(providerList, categoriesToGrab);
	}

	public Map<String, Set<CategoryDTO>> findCategory() {
		return getCategoryManager().findCategory();
	}

	public void forceEnd() {
		if (episodeManager != null) {
			episodeManager.forceEnd();
		}
		if (categoryManager != null) {
			categoryManager.forceEnd();
		}
	}

}
