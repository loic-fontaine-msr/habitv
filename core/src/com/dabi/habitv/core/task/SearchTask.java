package com.dabi.habitv.core.task;

import java.util.Collection;
import java.util.Set;

import com.dabi.habitv.core.dao.DlErrorDAO;
import com.dabi.habitv.core.dao.DownloadedDAO;
import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchEvent;
import com.dabi.habitv.core.event.SearchStateEnum;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.dto.ExporterDTO;
import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;
import com.dabi.habitv.framework.pub.Publisher;
import com.dabi.habitv.utils.FilterUtils;

public class SearchTask extends AbstractTask<Object> {

	private final PluginProviderInterface provider;

	private final Set<CategoryDTO> categoryDTOs;

	private final TaskAdder taskAdder;

	private final Publisher<SearchEvent> searchPublisher;

	private final Publisher<RetreiveEvent> retreivePublisher;

	private final DownloaderDTO downloader;

	private final ExporterDTO exporter;

	public SearchTask(final PluginProviderInterface provider, final Set<CategoryDTO> categoryDTOs, final TaskAdder taskAdder,
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
		LOG.info("Waiting for Searching episode for " + getCategory());
	}

	@Override
	protected void failed(final Throwable e) {
		LOG.error("Searching episode for " + provider.getName() + " failed", e);
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

	private void findEpisodeByCategories(final Collection<CategoryDTO> categoryDTOs) {
		for (final CategoryDTO category : categoryDTOs) {
			if (!category.getSubCategories().isEmpty()) {
				findEpisodeByCategories(category.getSubCategories());
			} else {
				// dao to find dowloaded episodes
				final DownloadedDAO dlDAO = buildDownloadDAO(category.getName());
				final Set<String> dlFiles = dlDAO.findDownloadedFiles();
				final boolean indexCreated = dlDAO.isIndexCreated();
				// dao to find error download episodes
				final DlErrorDAO errorDAO = new DlErrorDAO();
				final Set<String> errorFiles = errorDAO.findDownloadedErrorFiles();

				// get list of downloadable episodes
				final Set<EpisodeDTO> episodeList = provider.findEpisode(category);
				if (!indexCreated && !episodeList.isEmpty()) {
					LOG.info("Creating index for " + category.getName());
					searchPublisher.addNews(new SearchEvent(provider.getName(), category.getName(), SearchStateEnum.BUILD_INDEX));
				}
				// reinit index to purge non available files from index
				// dlDAO.initIndex(); provider may re add an old dl file, so we
				// shoudl'nt reinit index
				// filter episode lister by include/exclude and already
				// downloaded
				boolean isDownloaded;
				boolean isErrorDownloaded;
				int i = 0;
				for (final EpisodeDTO episode : episodeList) {
					episode.setNum(i);
					isDownloaded = dlFiles.contains(episode.getName());
					isErrorDownloaded = errorFiles.contains(episode.getFullName());
					if (indexCreated && FilterUtils.filterByIncludeExcludeAndDownloaded(episode, category.getInclude(), category.getExclude()) && !isDownloaded
							&& !isErrorDownloaded) {
						// producer download the file
						final TaskAdResult state = taskAdder.addRetreiveTask(new RetrieveTask(episode, retreivePublisher, taskAdder, exporter, provider,
								downloader, dlDAO));
						if (TaskState.TO_MANY_FAILED.equals(state.getState())) {
							errorDAO.addDownloadErrorFiles(episode.getFullName());
						}
					} else {
						// if index has not been created the first run will only
						// fill this file
						if (!isDownloaded && !isErrorDownloaded) {
							dlDAO.addDownloadedFiles(episode.getName());
						}
					}
					i++;
				}
			}
		}
	}

	protected DownloadedDAO buildDownloadDAO(final String categoryName) {
		return new DownloadedDAO(provider.getName(), categoryName, downloader.getIndexDir());
	}

	@Override
	public String toString() {
		return "Searching" + provider.getName();
	}
}
