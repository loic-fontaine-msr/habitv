package com.dabi.habitv.core.task;


public interface TaskAdder {

	void addRetreiveTask(RetreiveTask retreiveTask);

	void addExportTask(ExportTask exportTask, String category);

	void addDownloadTask(DownloadTask downloadTask, String channel);

}
