package com.dabi.habitv.api.plugin.exception;


public class ExportFailedException extends ExecutorFailedException {

	private static final long serialVersionUID = -4573862853212185451L;

	public ExportFailedException(final ExecutorFailedException executorFailedException) {
		super(executorFailedException.getCmd(), executorFailedException.getFullOuput(), executorFailedException.getLastLine(), executorFailedException);
	}

}
