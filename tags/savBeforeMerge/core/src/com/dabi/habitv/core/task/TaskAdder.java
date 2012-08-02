package com.dabi.habitv.core.task;

import java.util.concurrent.Future;


public interface TaskAdder {

	Future<Object> addDownloadTask(DownloadTask downloadTask);

	Future<Object> addRetreiveTask(RetreiveTask retreiveTask);

	Future<Object> addExportTask(ExportTask exportTask, String category);

}
