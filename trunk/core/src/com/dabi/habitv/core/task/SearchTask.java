package com.dabi.habitv.core.task;

import java.util.List;
import java.util.Set;

import com.dabi.habitv.core.dao.DownloadedDAO;
import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchEvent;
import com.dabi.habitv.core.event.SearchStateEnum;
import com.dabi.habitv.core.publisher.Publisher;
import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.dto.ExporterDTO;
import com.dabi.habitv.utils.FilterUtils;

public final class SearchTask extends AbstractTask<Object> {

	private final PluginProviderInterface provider;

	private final List<CategoryDTO> categoryDTOs;

	private final TaskAdder taskAdder;

	private final Publisher<SearchEvent> searchPublisher;

	private final Publisher<RetreiveEvent> retreivePublisher;

	private final DownloaderDTO downloader;

	private final ExporterDTO exporter;

	public SearchTask(final PluginProviderInterface provider, final List<CategoryDTO> categoryDTOs, final TaskAdder taskAdder,
			final Publisher<SearchEvent> searchPublisher, final Publisher<RetreiveEvent> retreivePublisher, final DownloaderDTO downloader,
			final ExporterDTO exporter) {
		this.provider = provider;
		this.categoryDTOs = categoryDTOs;
		this.taskAdder = taskAdder;
		this.searchPublisher = searchPublisher;
		this.retreivePublisher = retreivePublisher;
		this.downloader = downloader;
		this.exporter = exporter;
	}

	@Override
	protected void added() {
		LOG.info("Wainting for Searching episode for " + getCategory());
	}

	@Override
	protected void failed(final Exception e) {
		LOG.error("Searching episode for " + provider.getName() + " failed");
		searchPublisher.addNews(new SearchEvent(provider.getName(), SearchStateEnum.ERROR));
	}

	@Override
	protected void ended() {
		LOG.info("Searching episode for " + provider.getName() + " done");
		searchPublisher.addNews(new SearchEvent(provider.getName(), SearchStateEnum.DONE));
	}

	@Override
	protected void started() {
		LOG.info("Searching episode for " + provider.getName() + " is starting");
		searchPublisher.addNews(new SearchEvent(provider.getName(), SearchStateEnum.CHECKING_EPISODES));
	}

	@Override
	protected Object doCall() {
		findEpisodeByCategories(categoryDTOs);
		return null;
	}

	private void findEpisodeByCategories(final List<CategoryDTO> categoryDTOs2) {
		for (final CategoryDTO category : categoryDTOs) {
			if (!category.getSubCategories().isEmpty()) {
				findEpisodeByCategories(category.getSubCategories());
			} else {
				// dao to find dowloaded episodes
				final DownloadedDAO filesDAO = new DownloadedDAO(provider.getName(), category.getName(), downloader.getIndexDir());
				if (!filesDAO.isIndexCreated()) {
					LOG.info("Creating index for " + getCategory());
					searchPublisher.addNews(new SearchEvent(provider.getName(), category.getName(), SearchStateEnum.BUILD_INDEX));
				}
				// get list of downloadable episodes
				Set<EpisodeDTO> episodeList = provider.findEpisode(category);
				// filter episode lister by include/exclude and already
				// downloaded
				episodeList = FilterUtils.filterByIncludeExcludeAndDownloaded(episodeList, category.getInclude(), category.getExclude(),
						filesDAO.findDownloadedFiles());
				for (final EpisodeDTO episode : episodeList) {
					if (filesDAO.isIndexCreated()) {
						// producer download the file
						taskAdder.addRetreiveTask(new RetreiveTask(episode, retreivePublisher, taskAdder, exporter, provider, downloader));
					} else {
						// if index has not been created the first run will only
						// fill this file
						filesDAO.addDownloadedFiles(episode.getName());
					}
				}
			}
		}
	}
}
