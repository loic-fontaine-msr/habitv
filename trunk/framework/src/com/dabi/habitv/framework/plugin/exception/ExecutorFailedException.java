package com.dabi.habitv.framework.plugin.exception;

public class ExecutorFailedException extends Exception {

	private static final long serialVersionUID = -3244886187302237470L;

	private final String fullOuput;// TODO add last line

	private final String cmd;

	public ExecutorFailedException(final String cmd, final String fullOuput, final Throwable throwable) {
		super(throwable);
		this.fullOuput = fullOuput;
		this.cmd = cmd;
	}

	public String getFullOuput() {
		return fullOuput;
	}

	public String getCmd() {
		return cmd;
	}

}
