package com.dabi.habitv.core.mgr;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchEvent;
import com.dabi.habitv.core.event.SearchStateEnum;
import com.dabi.habitv.core.publisher.Publisher;
import com.dabi.habitv.core.task.DownloadTask;
import com.dabi.habitv.core.task.ExportTask;
import com.dabi.habitv.core.task.RetreiveTask;
import com.dabi.habitv.core.task.SearchTask;
import com.dabi.habitv.core.task.TaskAdder;
import com.dabi.habitv.core.task.TaskMgr;
import com.dabi.habitv.core.task.TaskMgrListener;
import com.dabi.habitv.core.task.TaskTypeEnum;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.ExporterDTO;
import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;

public final class EpisodeManager extends AbstractManager implements TaskAdder {

	private final TaskMgr<RetreiveTask, Object> retreiveMgr;

	private final TaskMgr<DownloadTask, Object> downloadMgr;

	private final TaskMgr<ExportTask, Object> exportMgr;

	private final TaskMgr<SearchTask, Object> searchMgr;

	private final DownloaderDTO downloader;

	private final ExporterDTO exporter;

	private final Publisher<RetreiveEvent> retreivePublisher;

	private final Publisher<SearchEvent> searchPublisher;

	public EpisodeManager(final DownloaderDTO downloader, final ExporterDTO exporter, final Collection<PluginProviderInterface> collection,
			final Map<String, Integer> taskName2PoolSize) {
		super(collection);

		// task mgrs
		retreiveMgr = new TaskMgr<RetreiveTask, Object>(taskName2PoolSize.get(TaskTypeEnum.retreive.toString()), buildRetreiveTaskMgrListener());
		downloadMgr = new TaskMgr<DownloadTask, Object>(taskName2PoolSize.get(TaskTypeEnum.download.toString()), buildDownloadTaskMgrListener());
		exportMgr = new TaskMgr<ExportTask, Object>(taskName2PoolSize.get(TaskTypeEnum.export.toString()), buildExportTaskMgrListener());
		searchMgr = new TaskMgr<SearchTask, Object>(taskName2PoolSize.get(TaskTypeEnum.search.toString()), buildSearchTaskMgrListener());
		// publisher
		retreivePublisher = new Publisher<>();
		searchPublisher = new Publisher<>();
		this.downloader = downloader;
		this.exporter = exporter;
	}

	public void retreiveEpisode(final Collection<PluginProviderInterface> providerList, final Map<String, List<CategoryDTO>> channel2Categories) {
		for (final PluginProviderInterface provider : providerList) {
			// method must be asynchronous
			searchMgr.addTask(new SearchTask(provider, channel2Categories.get(provider.getName()), this, searchPublisher, retreivePublisher, downloader,
					exporter));
		}
	}

	private TaskMgrListener buildExportTaskMgrListener() {
		return new TaskMgrListener() {

			@Override
			public void onFailed(final Throwable throwable) {
				searchPublisher.addNews(new SearchEvent(SearchStateEnum.ERROR, throwable));
			}

			@Override
			public void onAllTreatmentDone() {

			}
		};
	}

	private TaskMgrListener buildDownloadTaskMgrListener() {
		return new TaskMgrListener() {

			@Override
			public void onFailed(final Throwable throwable) {
				searchPublisher.addNews(new SearchEvent(SearchStateEnum.ERROR, throwable));
			}

			@Override
			public void onAllTreatmentDone() {

			}
		};
	}

	private TaskMgrListener buildRetreiveTaskMgrListener() {
		return new TaskMgrListener() {

			@Override
			public void onFailed(final Throwable throwable) {
				searchPublisher.addNews(new SearchEvent(SearchStateEnum.ERROR, throwable));
			}

			@Override
			public void onAllTreatmentDone() {
				searchPublisher.addNews(new SearchEvent(SearchStateEnum.ALL_RETREIVE_DONE));
			}
		};
	}

	private TaskMgrListener buildSearchTaskMgrListener() {
		return new TaskMgrListener() {

			@Override
			public void onFailed(final Throwable throwable) {

			}

			@Override
			public void onAllTreatmentDone() {
				searchPublisher.addNews(new SearchEvent(SearchStateEnum.ALL_SEARCH_DONE));
			}
		};
	}

	@Override
	public Future<Object> addDownloadTask(final DownloadTask downloadTask) {
		return downloadMgr.addTask(downloadTask);
	}

	@Override
	public Future<Object> addRetreiveTask(final RetreiveTask retreiveTask) {
		return retreiveMgr.addTask(retreiveTask);
	}

	@Override
	public Future<Object> addExportTask(final ExportTask exportTask, final String category) {
		return exportMgr.addTask(exportTask, category);
	}

	public Publisher<RetreiveEvent> getRetreivePublisher() {
		return retreivePublisher;
	}

	public Publisher<SearchEvent> getSearchPublisher() {
		return searchPublisher;
	}

	public void forceEnd() {
		exportMgr.shutdownNow();
		retreiveMgr.shutdownNow();
		searchMgr.shutdownNow();
	}

}
