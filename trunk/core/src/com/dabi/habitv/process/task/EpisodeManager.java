package com.dabi.habitv.process.task;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.ExporterDTO;
import com.dabi.habitv.process.mgr.TaskMgr;
import com.dabi.habitv.process.mgr.TaskMgrListener;
import com.dabi.habitv.process.publisher.Publisher;
import com.dabi.habitv.process.publisher.RetreiveEvent;
import com.dabi.habitv.process.publisher.SearchEvent;

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
			final String exporterPLuginDir, final Map<String, Integer> taskName2PoolSize) {
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
			public void onFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAllTreatmentDone() {
				// TODO Auto-generated method stub

			}
		};
	}

	private TaskMgrListener buildDownloadTaskMgrListener() {
		return new TaskMgrListener() {

			@Override
			public void onFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAllTreatmentDone() {
				// TODO Auto-generated method stub

			}
		};
	}

	private TaskMgrListener buildRetreiveTaskMgrListener() {
		return new TaskMgrListener() {

			@Override
			public void onFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAllTreatmentDone() {
				// TODO Auto-generated method stub

			}
		};
	}

	private TaskMgrListener buildSearchTaskMgrListener() {
		return new TaskMgrListener() {

			@Override
			public void onFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAllTreatmentDone() {
				// TODO Auto-generated method stub

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
}
