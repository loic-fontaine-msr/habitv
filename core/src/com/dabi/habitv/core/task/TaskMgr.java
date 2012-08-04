package com.dabi.habitv.core.task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dabi.habitv.framework.plugin.exception.TechnicalException;

public class TaskMgr<T extends AbstractTask<R>, R> {

	private static final int DEFAULT_KEEP_ALIVE_TIME_SEC = 10;

	private static final String DEFAULT = "default";

	private final int poolSize;

	private final Map<String, ExecutorService> category2ExecutorService = new HashMap<String, ExecutorService>();

	private final TaskMgrListener taskMgrListener;

	public TaskMgr(final int poolSize, final TaskMgrListener taskMgrListener) {
		super();
		this.poolSize = poolSize;
		this.taskMgrListener = taskMgrListener;
	}

	public Future<R> addTask(final T task) {
		return addTask(task, DEFAULT);
	}

	public Future<R> addTask(final T task, final String category) {
		ExecutorService executorService = category2ExecutorService.get(category);
		if (executorService == null) {
			executorService = initExecutor();
			category2ExecutorService.put(category, executorService);
		}
		task.addedTo(category);
		return executorService.submit(task);
	}

	private ExecutorService initExecutor() {
		return new ThreadPoolExecutor(poolSize, poolSize, DEFAULT_KEEP_ALIVE_TIME_SEC, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()) {

			@Override
			public void afterExecute(final Runnable r, final Throwable t) {
				super.afterExecute(r, t);
				if (getActiveCount() <= 1) {
					taskMgrListener.onAllTreatmentDone();
				}
			}
		};
	}

	public void shutdown(final int timeoutMs) {
		for (final ExecutorService executorService : category2ExecutorService.values()) {
			try {
				executorService.awaitTermination(timeoutMs, TimeUnit.MILLISECONDS);
			} catch (final InterruptedException e) {
				throw new TechnicalException(e);
			}
		}
	}

	public void shutdownNow() {
		for (final ExecutorService executorService : category2ExecutorService.values()) {
			executorService.shutdownNow();
		}
	}
}
