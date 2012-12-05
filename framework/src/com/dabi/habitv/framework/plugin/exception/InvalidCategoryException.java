package com.dabi.habitv.framework.plugin.exception;

import org.apache.log4j.Logger;

public class InvalidCategoryException extends Exception {

	private static final Logger LOG = Logger.getLogger(InvalidCategoryException.class);

	private static final long serialVersionUID = 3244569227497186902L;

	private final String errorField;

	public String getErrorField() {
		return errorField;
	}

	private CauseField causeField;

	public enum CauseField {
		NAME, IDENTIFIER;
	}

	public InvalidCategoryException(final String errorField, final CauseField cause) {
		super();
		LOG.error("invalid category "+ errorField);
		this.errorField = errorField;
		this.causeField = cause;
	}

	public CauseField getCauseField() {
		return causeField;
	}

	public void setCause(final CauseField cause) {
		this.causeField = cause;
	}
}
