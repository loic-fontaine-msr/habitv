package com.dabi.habitv.process.task;

import java.util.concurrent.Callable;

import com.dabi.habitv.framework.plugin.exception.TechnicalException;

public abstract class AbstractTask<R> implements Callable<R> {

	private boolean added = false;

	private boolean done = false;

	private String category = null;

	@Override
	public final R call() {
		R result;
		try {
			started();
			result = doCall();
		} catch (final Exception e) {
			failed();
			throw new TechnicalException(e);
		}
		ended();
		done = true;
		return result;
	}

	protected abstract void added();

	protected abstract void failed();

	protected abstract void ended();

	protected abstract void started();

	protected abstract R doCall() throws Exception;

	public final void addedTo(final String category) {
		this.category = category;
		added = true;
		added();
	}

	public final boolean isAdded() {
		return added;
	}

	public final boolean isDone() {
		return done;
	}

	public final String getCategory() {
		return category;
	}

}
