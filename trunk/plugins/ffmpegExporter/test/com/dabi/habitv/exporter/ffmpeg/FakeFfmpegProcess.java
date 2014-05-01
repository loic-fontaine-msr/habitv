package com.dabi.habitv.exporter.ffmpeg;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FakeFfmpegProcess extends Process {

	private final int ret;

	private final String inputString;

	public FakeFfmpegProcess(final int ret, final String inputString) {
		this.ret = ret;
		this.inputString = inputString;
	}

	@Override
	public OutputStream getOutputStream() {
		return null;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(inputString.getBytes());
	}

	@Override
	public InputStream getErrorStream() {
		return new ByteArrayInputStream(new byte[0]);
	}

	@Override
	public int waitFor() throws InterruptedException {
		return 0;
	}

	@Override
	public int exitValue() {
		return ret;
	}

	@Override
	public void destroy() {

	}

}
