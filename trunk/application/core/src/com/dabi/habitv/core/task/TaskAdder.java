package com.dabi.habitv.core.task;


public interface TaskAdder {

	TaskAdResult addRetreiveTask(RetrieveTask retreiveTask);

	TaskAdResult addExportTask(ExportTask exportTask, String category);

	TaskAdResult addDownloadTask(DownloadTask downloadTask, String channel);

}
