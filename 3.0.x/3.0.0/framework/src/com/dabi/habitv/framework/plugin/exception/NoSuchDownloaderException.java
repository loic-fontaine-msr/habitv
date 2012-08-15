package com.dabi.habitv.framework.plugin.exception;

public class NoSuchDownloaderException extends Exception {

	private static final long serialVersionUID = 4746386758348861105L;

	public NoSuchDownloaderException(final String downloaderName) {
		super(downloaderName + "not found");
	}

}
