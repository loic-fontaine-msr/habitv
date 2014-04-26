package com.dabi.habitv.framework.plugin.exception;


public class InvalidCategoryException extends Exception {

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
