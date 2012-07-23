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

	@Override
	public int hashCode() {
		return getException().hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		boolean ret;
		if (obj instanceof AbstractEvent) {
			final AbstractEvent abstractEvent = (AbstractEvent) obj;
			ret = getException().equals(abstractEvent.getException());
		} else {
			ret = false;
		}
		return ret;
	}

}
