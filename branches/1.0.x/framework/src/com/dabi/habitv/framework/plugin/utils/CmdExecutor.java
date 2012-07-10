package com.dabi.habitv.framework.plugin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;

public class CmdExecutor {

	private static final Logger LOG = Logger.getLogger(CmdExecutor.class);

	private final String cmd;

	private final CmdProgressionListener listener;

	private String lastOutputLine = null;

	public CmdExecutor(final String cmd, final CmdProgressionListener listener) {
		super();
		this.cmd = cmd;
		this.listener = listener;
	}

	public void execute() throws ExecutorFailedException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("cmd : " + cmd);
		}
		final Runtime runtime = Runtime.getRuntime();
		Process process = null;

		final StringBuffer fullOutput = new StringBuffer();

		try {
			process = runtime.exec(cmd);
			ProcessingThread.addProcessing(process);
			// consume standard output in a thread
			final Thread outputThread = treatCmdOutput(process.getInputStream(), fullOutput);
			outputThread.start();

			// consume error output in a thread
			final Thread errorThread = treatCmdOutput(process.getErrorStream(), fullOutput);
			errorThread.start();

			// wait for both thread
			outputThread.join();
			errorThread.join();
		} catch (IOException | InterruptedException e) {
			throw new TechnicalException(e);
		} finally {
			if (process != null) {
				ProcessingThread.removeProcessing(process);
			}
		}

		if (getLastOutputLine() != null && !isSuccess()) {
			throw new ExecutorFailedException(cmd, fullOutput.toString());
		}
	}

	private Thread treatCmdOutput(final InputStream inputStream, final StringBuffer fullOutput) {
		final Thread tread = new Thread() {
			@Override
			public void run() {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					String line = "";
					try {
						long lastTime = 0;
						while ((line = reader.readLine()) != null) {
							fullOutput.append(line);
							fullOutput.append("\n");
							lastOutputLine = line;
							String handledLine = handleProgression(line);
							if (listener != null && handledLine != null && (System.currentTimeMillis() - lastTime) > 2000) {//FIXME en conf
								listener.listen(handledLine);
								lastTime = System.currentTimeMillis();
							}
						}
					} finally {
						reader.close();
					}
				} catch (IOException ioe) {
					throw new TechnicalException(ioe);
				}
			}
		};
		return tread;
	}

	protected String getLastOutputLine() {
		return lastOutputLine;
	}

	protected boolean isSuccess() {
		return true;
	}

	protected String handleProgression(final String line) {
		return null;
	}

}
