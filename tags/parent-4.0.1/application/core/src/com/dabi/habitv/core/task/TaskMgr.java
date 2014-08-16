package com.dabi.habitv.core.task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dabi.habitv.api.plugin.exception.TechnicalException;

public class TaskMgr<T extends AbstractTask<R>, R> {

	private static final int DEFAULT_KEEP_ALIVE_TIME_SEC = 10;

	private static final String DEFAULT = "default";

	private final Map<String, ExecutorService> category2ExecutorService = new HashMap<String, ExecutorService>();

	private final TaskMgrListener taskMgrListener;

	private final int defaultPoolSize;

	private final Map<String, Integer> category2PoolSize;

	public TaskMgr(final int defaultPoolSize, final TaskMgrListener taskMgrListener, final Map<String, Integer> category2PoolSize) {
		super();
		this.defaultPoolSize = defaultPoolSize;
		this.taskMgrListener = taskMgrListener;
		this.category2PoolSize = category2PoolSize;
	}

	public void addTask(final T task) {
		addTask(task, DEFAULT);
	}

	public synchronized void addTask(final T task, final String category) {
		task.adding();
		ExecutorService executorService = category2ExecutorService.get(category);
		if (executorService == null) {
			executorService = initExecutor(category);
			category2ExecutorService.put(category, executorService);
		}
		task.addedTo(category, executorService.submit(task));
	}

	private ExecutorService initExecutor(final String category) {
		final int poolSize = findPoolSizeByCategory(category);
		final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(poolSize, poolSize, DEFAULT_KEEP_ALIVE_TIME_SEC, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>()) {

			@Override
			public void afterExecute(final Runnable r, final Throwable t) {
				super.afterExecute(r, t);
				if (getActiveCount() <= 1) {
					taskMgrListener.onAllTreatmentDone();
				}
			}
		};
		threadPoolExecutor.allowCoreThreadTimeOut(true);
		return threadPoolExecutor;
	}

	private int findPoolSizeByCategory(final String category) {
		Integer ret;
		if (DEFAULT.equals(category) || category2PoolSize == null || !category2PoolSize.containsKey(category)) {
			ret = defaultPoolSize;
		} else {
			ret = category2PoolSize.get(category);
		}
		return ret;
	}

	void shutdown(final int timeoutMs) {
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
