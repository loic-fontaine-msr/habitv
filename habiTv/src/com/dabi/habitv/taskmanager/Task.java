package com.dabi.habitv.taskmanager;

public class Task {

	private final TaskTypeEnum taskType;

	private final String category;

	private final String identifier;

	private final Runnable runnable;

	public Task(final TaskTypeEnum taskType, final String category, final String identifier, final Runnable runnable) {
		super();
		this.taskType = taskType;
		this.category = category;
		this.identifier = identifier;
		this.runnable = runnable;
	}

	public Task(final TaskTypeEnum taskType, final String identifier, final Runnable runnable) {
		this(taskType, null, identifier, runnable);
	}

	public TaskTypeEnum getTaskType() {
		return taskType;
	}

	public String getCategory() {
		return category;
	}

	public String getIdentifier() {
		return identifier;
	}

	interface TaskAction {
		void onTaskEnd();
	}

	public Runnable getRunnable(final TaskAction taskAction) {
		return new Runnable() {

			@Override
			public void run() {
				try {
					final String threadName = taskType + "/" + category + "/" + identifier;
					Thread.currentThread().setName(threadName);
					runnable.run();
				} finally {
					taskAction.onTaskEnd();
				}
			}
		};
	}

	@Override
	public int hashCode() {
		return identifier.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof Task) {
			ret = identifier.equals(((Task) obj).getIdentifier());
		}
		return ret;
	}

}
