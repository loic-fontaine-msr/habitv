package com.dabi.habitv.process.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dabi.habitv.config.ConfigAccess;
import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.config.entities.Downloader;
import com.dabi.habitv.config.entities.Exporter;
import com.dabi.habitv.config.entities.SimultaneousTaskNumber;
import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.PluginExporterInterface;
import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.ExportDTO;
import com.dabi.habitv.framework.plugin.api.dto.ExporterDTO;
import com.dabi.habitv.plugin.PluginFactory;

public final class CoreManager {

	private CategoryManager categoryManager;

	private EpisodeManager episodeManager;

	private final Config config;

	private final Collection<PluginProviderInterface> providerList;

	private final Map<String, Integer> buildTaskName2PoolSizeMap;

	private final GrabConfigDAO grabConfigDAO;

	public CoreManager() {
		config = ConfigAccess.initConfig();
		final PluginFactory<PluginProviderInterface> pluginProviderFactory = new PluginFactory<>(PluginProviderInterface.class, config.getProviderPluginDir());
		providerList = pluginProviderFactory.getAllPlugin();
		buildTaskName2PoolSizeMap = buildTaskName2PoolSizeMap(config.getSimultaneousTaskNumber());
		grabConfigDAO = new GrabConfigDAO();
	}

	private CategoryManager getCategoryManager() {
		if (categoryManager == null) {
			categoryManager = initCategoryManager(providerList, buildTaskName2PoolSizeMap);
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

	private CategoryManager initCategoryManager(final Collection<PluginProviderInterface> pluginProviderList, final Map<String, Integer> taskName2PoolSize) {
		final CategoryManager categoryManager = new CategoryManager(pluginProviderList, taskName2PoolSize);
		return categoryManager;
	}

	private List<ExportDTO> buildExporterListDTO(final List<Exporter> exporterList) {
		final List<ExportDTO> exportDTOList = new ArrayList<>(exporterList.size());
		ExportDTO exportDTO;
		for (final Exporter exporter : exporterList) {
			exportDTO = new ExportDTO(exporter.getCondition().getReference(), exporter.getCondition().getPattern(), exporter.getName(), exporter.getOutput(),
					exporter.getCmd(), buildExporterListDTO(exporter.getExporter()));
			exportDTOList.add(exportDTO);
		}
		return exportDTOList;
	}

	private EpisodeManager getEpisodeManager() {
		if (episodeManager == null) {
			episodeManager = initEpisodeManager(providerList, config.getExporterPluginDir(), buildTaskName2PoolSizeMap);
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

	private EpisodeManager initEpisodeManager(final Collection<PluginProviderInterface> collection, final String exporterPluginDir,
			final Map<String, Integer> taskName2PoolSize) {
		final PluginFactory<PluginDownloaderInterface> pluginDownloaderFactory = new PluginFactory<>(PluginDownloaderInterface.class,
				config.getDownloaderPluginDir());
		final Map<String, PluginDownloaderInterface> downloaderName2downloader = pluginDownloaderFactory.getAllPluginMap();
		final Map<String, String> downloaderName2BinPath = buildDownloadersBinPath(config.getDownloader());
		final DownloaderDTO downloader = new DownloaderDTO(downloaderName2downloader, downloaderName2BinPath, config.getDownloadOuput(), config.getIndexDir());
		final PluginFactory<PluginExporterInterface> pluginExporterFactory = new PluginFactory<>(PluginExporterInterface.class, config.getExporterPluginDir());
		final Map<String, PluginExporterInterface> exporterName2exporter = pluginExporterFactory.getAllPluginMap();
		final ExporterDTO exporter = new ExporterDTO(exporterName2exporter, buildExporterListDTO(config.getExporter()));
		final EpisodeManager episodeManager = new EpisodeManager(downloader, exporter, collection, exporterPluginDir, taskName2PoolSize);
		return episodeManager;
	}

	public void retreiveEpisode() {
		getEpisodeManager().retreiveEpisode(providerList, grabConfigDAO.loadGrabConfig());
	}

	public void findAndSaveCategory() {
		final Map<String, Set<CategoryDTO>> channel2Categories = getCategoryManager().findCategory();
		grabConfigDAO.saveGrabConfig(channel2Categories);
	}

}
