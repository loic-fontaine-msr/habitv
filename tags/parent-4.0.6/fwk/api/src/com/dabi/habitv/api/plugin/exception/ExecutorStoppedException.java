package com.dabi.habitv.api.plugin.exception;

import org.apache.log4j.Logger;

public class ExecutorStoppedException extends RuntimeException {

	protected static final Logger LOG = Logger
			.getLogger(ExecutorStoppedException.class);

	private static final long serialVersionUID = -3244886187302237470L;

	private final String cmd;

	public ExecutorStoppedException(final String cmd) {
		super("Command stopped");
		this.cmd = cmd;
	}

	public String getCmd() {
		return cmd;
	}

}
