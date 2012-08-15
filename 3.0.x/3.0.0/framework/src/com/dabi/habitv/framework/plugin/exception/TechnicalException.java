package com.dabi.habitv.framework.plugin.exception;


public class TechnicalException extends RuntimeException {

	public TechnicalException(final Throwable throwable) {
		super(throwable);
	}

	public TechnicalException(final String string) {
		super(string);
	}

	private static final long serialVersionUID = -6072107997453620547L;

}
