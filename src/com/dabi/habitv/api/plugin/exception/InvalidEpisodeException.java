package com.dabi.habitv.api.plugin.exception;

public class InvalidEpisodeException extends Exception {

	private static final long serialVersionUID = 300707895096865066L;

	private final String errorField;

	public String getErrorField() {
		return errorField;
	}

	private CauseField causeField;

	public enum CauseField {
		CATEGORY, NAME, URL;
	}

	public InvalidEpisodeException(final String errorField, final CauseField cause) {
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
