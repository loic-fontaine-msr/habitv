package com.dabi.habitv.taskmanager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.dabi.habitv.config.HabitTvConf;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.taskmanager.Task.TaskAction;

public class TaskMgr {

	private static final Logger LOG = Logger.getLogger(TaskMgr.class);

	private static final String DEFAULT_CATEGORY = "DEFAULT";

	private final Map<TaskTypeEnum, Map<String, ExecutorService>> taskType2Category2ThreadPool;

	private final Map<TaskTypeEnum, Map<String, Set<Task>>> taskType2Category2Tasks;

	private final Map<TaskTypeEnum, Integer> taskType2ThreadPool;

	public TaskMgr(final Map<TaskTypeEnum, Integer> taskType2ThreadPool) {
		this.taskType2ThreadPool = taskType2ThreadPool;
		this.taskType2Category2ThreadPool = new HashMap<>(TaskTypeEnum.values().length);
		this.taskType2Category2Tasks = new HashMap<>();
	}

	private int getPoolSize(final TaskTypeEnum taskType) {
		Integer size = taskType2ThreadPool.get(taskType);
		if (size == null) {
			size = HabitTvConf.DEFAULT_POOL_SIZE;
		}
		return size;
	}

	public synchronized void addTask(final Task task) {
		final ExecutorService executorService = findExecutorService(task.getTaskType(), task.getCategory());
		if (!findCurrentTask(task)) {
			task.add();
			LOG.debug("Task added" + task);
			executorService.execute(task.getRunnable(new TaskAction() {

				@Override
				public void onTaskEnd() {
					removeCurrentTask(task);
					LOG.debug("Task ended" + task);
				}

			}));
			addCurrentTask(task);
		} else {
			LOG.debug("Task refused" + task);
		}
	}

	private boolean findCurrentTask(final Task task) {
		return findOrRemoveCurrentTask(task, false);
	}

	private boolean findOrRemoveCurrentTask(final Task task, final boolean remove) {
		final Map<String, Set<Task>> category2Tasks = taskType2Category2Tasks.get(task.getTaskType());
		boolean ret = false;
		if (category2Tasks != null) {
			String category = task.getCategory();
			if (category == null) {
				category = DEFAULT_CATEGORY;
			}
			final Set<Task> tasks = category2Tasks.get(category);
			if (tasks != null) {
				if (remove) {
					ret = tasks.remove(task);
					if (tasks.isEmpty()) {
						category2Tasks.remove(category);
						if (category2Tasks.isEmpty()) {
							taskType2Category2Tasks.remove(task.getTaskType());
						}
					}
				} else {
					ret = tasks.contains(task);
				}
			}
		}
		return ret;
	}

	private void removeCurrentTask(final Task task) {
		findOrRemoveCurrentTask(task, true);
	}

	private void addCurrentTask(final Task task) {
		Map<String, Set<Task>> category2Tasks = taskType2Category2Tasks.get(task.getTaskType());
		if (category2Tasks == null) {
			category2Tasks = new HashMap<>();
			taskType2Category2Tasks.put(task.getTaskType(), category2Tasks);
		}

		String category = task.getCategory();
		if (category == null) {
			category = DEFAULT_CATEGORY;
		}

		Set<Task> tasks = category2Tasks.get(category);
		if (tasks == null) {
			tasks = new HashSet<>();
			category2Tasks.put(category, tasks);
		}
		tasks.add(task);
	}

	private ExecutorService findExecutorService(final TaskTypeEnum taskType, final String category) {
		Map<String, ExecutorService> category2ThreadPool = taskType2Category2ThreadPool.get(taskType);
		if (category2ThreadPool == null) {
			category2ThreadPool = new HashMap<String, ExecutorService>();
			taskType2Category2ThreadPool.put(taskType, category2ThreadPool);
		}

		final String searchCategory;
		if (category == null) {
			searchCategory = DEFAULT_CATEGORY;
		} else {
			searchCategory = category;
		}

		ExecutorService executorService = category2ThreadPool.get(searchCategory);
		if (executorService == null) {
			executorService = initExecutorService(getPoolSize(taskType));
			category2ThreadPool.put(category, executorService);
		}
		return executorService;
	}

	private ExecutorService initExecutorService(int poolSize) {
		return Executors.newFixedThreadPool(poolSize);
	}

	public void waitForEndTasks(final int timeOut) {
		for (Map<String, ExecutorService> category2ExecutorService : taskType2Category2ThreadPool.values()) {
			for (ExecutorService executorService : category2ExecutorService.values()) {
				endThreadPool(executorService, timeOut);
			}
		}
	}

	private static void endThreadPool(final ExecutorService threadPool, final int timeOut) {
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(timeOut, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new TechnicalException(e);
		}
	}

	public void waitForEndTasks(final int timeOut, final TaskTypeEnum taskType) {
		Map<String, ExecutorService> category2ExecutorService = taskType2Category2ThreadPool.get(taskType);
		if (category2ExecutorService != null) {
			for (ExecutorService executorService : category2ExecutorService.values()) {
				endThreadPool(executorService, timeOut);
			}
		}
	}

	public void waitForEndTasks(final int timeOut, final TaskTypeEnum taskType, final String category) {
		endThreadPool(findExecutorService(taskType, category), timeOut);
	}

	public void forceEnd() {
		for (Map<String, ExecutorService> category2ExecutorService : taskType2Category2ThreadPool.values()) {
			for (ExecutorService executorService : category2ExecutorService.values()) {
				executorService.shutdownNow();
			}
		}
	}

}
