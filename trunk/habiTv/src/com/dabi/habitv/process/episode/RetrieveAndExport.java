package com.dabi.habitv.process.episode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.ExporterPluginInterface;
import com.dabi.habitv.framework.plugin.api.ProviderPluginInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.grabconfig.entities.Category;
import com.dabi.habitv.grabconfig.entities.Channel;
import com.dabi.habitv.grabconfig.entities.GrabConfig;
import com.dabi.habitv.plugin.PluginFactory;

public class RetrieveAndExport {

	public void execute(final Config config, final GrabConfig grabConfig, final ProcessEpisodeListener listener) {

		final ExecutorService exportThreadPool = Executors.newFixedThreadPool(config.getSimultaneousExport());
		final ExecutorService channelThreadPool = Executors.newFixedThreadPool(config.getSimultaneousChannelDownload());

		final PluginFactory<ProviderPluginInterface> pluginProviderFactory = new PluginFactory<>(ProviderPluginInterface.class, config.getProviderPluginDir());
		final PluginFactory<PluginDownloaderInterface> pluginDownloaderFactory = new PluginFactory<>(PluginDownloaderInterface.class,
				config.getDownloaderPluginDir());
		final PluginFactory<ExporterPluginInterface> pluginExporterFactory = new PluginFactory<>(ExporterPluginInterface.class, config.getExporterPluginDir());

		for (final Channel channel : grabConfig.getChannel()) {
			final ProviderPluginInterface provider = pluginProviderFactory.findPlugin(channel.getName(), null);
			channelThreadPool.execute(new ChannelDownloader(listener, buildCategoryListDTO(channel.getCategory()), config, exportThreadPool, provider,
					pluginExporterFactory, pluginDownloaderFactory));
		}

		endThreadPool(channelThreadPool, config.getAllDownloadTimeout());
		endThreadPool(exportThreadPool, config.getExportTimeout());
		listener.processDone();
	}

	private List<CategoryDTO> buildCategoryListDTO(final List<Category> categories) {
		final List<CategoryDTO> categoryDTOs = new ArrayList<>(categories.size());
		CategoryDTO categoryDTO;
		for (Category category : categories) {
			categoryDTO = new CategoryDTO(category.getName(), category.getId(), category.getInclude(), category.getExclude());
			categoryDTO.addSubCategories(buildCategoryListDTO(category.getCategory()));
			categoryDTOs.add(categoryDTO);
		}
		return categoryDTOs;
	}

	public static void endThreadPool(final ExecutorService threadPool, final int timeOut) {
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(timeOut, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new TechnicalException(e);
		}
	}

}
