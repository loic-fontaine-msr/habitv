package com.dabi.habitv.taskmanager;

public class Task {

	private final TaskTypeEnum taskType;

	private final String category;

	private final String identifier;

	private Runnable runnable;

	private boolean added;

	private boolean success;

	public Task(final TaskTypeEnum taskType, final String category, final String identifier) {
		super();
		this.taskType = taskType;
		this.category = category;
		this.identifier = identifier;
		added = false;
	}

	public Task(final TaskTypeEnum taskType, final String identifier) {
		this(taskType, null, identifier);
	}

	public Task(final TaskTypeEnum taskType, final String category, final String identifier, final Runnable runnable) {
		super();
		this.taskType = taskType;
		this.category = category;
		this.identifier = identifier;
		this.runnable = runnable;
		added = false;
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

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(final boolean success) {
		this.success = success;
	}

	public void setJob(final Runnable runnable) {
		this.runnable = runnable;
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
				} finally { // FIXME onTaskFailed
					taskAction.onTaskEnd();
				}
			}
		};
	}

	public boolean isAdded() {
		return added;
	}

	public void add() {
		added = true;
	}

	@Override
	public int hashCode() {
		return identifier.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		boolean ret = false;
		if (obj instanceof Task) {
			ret = identifier.equals(((Task) obj).getIdentifier());
		}
		return ret;
	}

	@Override
	public String toString() {
		final StringBuilder ret = new StringBuilder();
		ret.append("taskType " + taskType);
		ret.append(",category " + category);
		ret.append(",identifier " + identifier);
		ret.append(",added " + added);
		ret.append(",success " + success);
		return ret.toString();
	}

}
