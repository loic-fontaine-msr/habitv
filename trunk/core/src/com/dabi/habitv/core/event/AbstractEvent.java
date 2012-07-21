package com.dabi.habitv.core.event;

public abstract class AbstractEvent {

	private Throwable exception;

	public AbstractEvent(final Throwable exception) {
		super();
		this.exception = exception;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(final Throwable exception) {
		this.exception = exception;
	}
}
