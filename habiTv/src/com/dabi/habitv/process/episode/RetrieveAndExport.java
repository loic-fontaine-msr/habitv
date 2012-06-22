package com.dabi.habitv.process.episode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.config.entities.Downloader;
import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.PluginExporterInterface;
import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloadersDTO;
import com.dabi.habitv.grabconfig.entities.Category;
import com.dabi.habitv.grabconfig.entities.Channel;
import com.dabi.habitv.grabconfig.entities.GrabConfig;
import com.dabi.habitv.plugin.PluginFactory;
import com.dabi.habitv.taskmanager.Task;
import com.dabi.habitv.taskmanager.TaskMgr;
import com.dabi.habitv.taskmanager.TaskTypeEnum;

public class RetrieveAndExport {

	private final PluginFactory<PluginProviderInterface> pluginProviderFactory;

	private final DownloadersDTO downloaders;

	private final PluginFactory<PluginExporterInterface> pluginExporterFactory;

	private final Config config;

	private final GrabConfig grabConfig;

	public RetrieveAndExport(final Config config, final GrabConfig grabConfig) {
		this.config = config;
		this.grabConfig = grabConfig;
		pluginProviderFactory = new PluginFactory<>(PluginProviderInterface.class, config.getProviderPluginDir());

		final PluginFactory<PluginDownloaderInterface> pluginDownloaderFactory = new PluginFactory<>(PluginDownloaderInterface.class,
				config.getDownloaderPluginDir());

		downloaders = new DownloadersDTO(pluginDownloaderFactory.getAllPluginMap(), buildDownloadersBinPath(config.getDownloader()));
		pluginExporterFactory = new PluginFactory<>(PluginExporterInterface.class, config.getExporterPluginDir());
	}

	private Map<String, String> buildDownloadersBinPath(final List<Downloader> downloaders) {
		final Map<String, String> downloaderName2BinPath = new HashMap<>(downloaders.size());
		for (final Downloader downloader : downloaders) {
			downloaderName2BinPath.put(downloader.getName(), downloader.getBinPath());
		}
		return downloaderName2BinPath;
	}

	public void execute(final ProcessEpisodeListener listener, final TaskMgr taskMgr) {

		for (final Channel channel : grabConfig.getChannel()) {
			final PluginProviderInterface provider = pluginProviderFactory.findPlugin(channel.getName(), null);
			taskMgr.addTask(buildChannelSearchTask(channel, listener, provider, taskMgr));
		}
		listener.processDone();
	}

	private Task buildChannelSearchTask(final Channel channel, final ProcessEpisodeListener listener, final PluginProviderInterface provider,
			final TaskMgr taskMgr) {
		final ChannelDownloader channelDownloader = new ChannelDownloader(channel.getName(), taskMgr, listener, buildCategoryListDTO(channel.getName(),
				channel.getCategory()), config, provider, pluginExporterFactory, downloaders);
		return new Task(TaskTypeEnum.SEARCH, channel.getName(), channelDownloader);
	}

	private static List<CategoryDTO> buildCategoryListDTO(final String channelName, final List<Category> categories) {
		final List<CategoryDTO> categoryDTOs = new ArrayList<>(categories.size());
		CategoryDTO categoryDTO;
		for (final Category category : categories) {
			categoryDTO = new CategoryDTO(channelName, category.getName(), category.getId(), category.getInclude(), category.getExclude(),
					category.getExtension());
			categoryDTO.addSubCategories(buildCategoryListDTO(channelName, category.getCategory()));
			categoryDTOs.add(categoryDTO);
		}
		return categoryDTOs;
	}

}
