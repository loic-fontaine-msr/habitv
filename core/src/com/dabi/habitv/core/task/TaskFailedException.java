package com.dabi.habitv.core.task;

public final class TaskFailedException extends RuntimeException {
	private static final long serialVersionUID = -5643616531292779318L;

	public TaskFailedException(final Throwable arg0) {
		super(arg0);
	}

}
