package com.dabi.habitv.framework.plugin.exception;

//TODO ne pas hériter, prendre une exception en parametre
public class DownloadFailedException extends ExecutorFailedException {

	private static final long serialVersionUID = 7423803197905025435L;

	public DownloadFailedException(final ExecutorFailedException executorFailedException) {
		super(executorFailedException.getCmd(), executorFailedException.getFullOuput(), null);
	}

}
