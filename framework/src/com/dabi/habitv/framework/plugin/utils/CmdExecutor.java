package com.dabi.habitv.framework.plugin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;

public class CmdExecutor {

	private static final Logger LOG = Logger.getLogger(CmdExecutor.class);

	protected static final int PERCENTAGE = 100;

	private final String cmd;

	private final CmdProgressionListener listener;

	private String lastOutputLine = null;

	private final String cmdProcessor;

	private static final String CMD_TOKEN = "#CMD#";

	public CmdExecutor(final String cmdProcessor, final String cmd, final CmdProgressionListener listener) {
		super();
		this.cmdProcessor = cmdProcessor;
		this.cmd = cmd;
		this.listener = listener;
	}

	public void execute() throws ExecutorFailedException {
		final StringBuffer fullOutput = new StringBuffer();

		final Process process = buildProcess(cmd);

		try {
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
			process.waitFor();
		} catch (final InterruptedException e) {
			throw new ExecutorFailedException(cmd, fullOutput.toString(), lastOutputLine, e);
		} finally {
			if (process != null) {
				ProcessingThread.removeProcessing(process);
			}
		}

		if (process.exitValue() != 0 || (getLastOutputLine() != null && !isSuccess(fullOutput.toString()))) {
			throw new ExecutorFailedException(cmd, fullOutput.toString(), lastOutputLine, null);
		}
	}

	protected Process buildProcess(final String cmd) throws ExecutorFailedException {
		try {
			if (cmdProcessor == null || cmdProcessor.isEmpty()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("cmd : " + cmd);
				}
				return Runtime.getRuntime().exec(cmd);
			} else {
				final String[] cmdArgs = cmdProcessor.split(" ");
				for (int i = 0; i < cmdArgs.length; i++) {
					if (cmdArgs[i].contains(CMD_TOKEN)) {
						cmdArgs[i] = cmdArgs[i].replace(CMD_TOKEN, cmd);
					}
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug("cmd : " + cmdArgs);
				}
				return Runtime.getRuntime().exec(cmdArgs);
			}
		} catch (final IOException e) {
			throw new ExecutorFailedException(cmd, e.getMessage(), e.getMessage(), e);
		}
	}

	private Thread treatCmdOutput(final InputStream inputStream, final StringBuffer fullOutput) {
		final Thread tread = new Thread() {
			@Override
			public void run() {
				setName("ThreadOut" + cmd);
				try {
					final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					String line = "";
					try {
						long lastTime = 0;
						while ((line = reader.readLine()) != null) {
							fullOutput.append(line);
							fullOutput.append("\n");
							lastOutputLine = line;
							final String handledLine = handleProgression(line);
							LOG.debug(line);
							if (listener != null && handledLine != null && (System.currentTimeMillis() - lastTime) > FrameworkConf.TIME_BETWEEN_LOG) {
								lastTime = System.currentTimeMillis();
								listener.listen(handledLine);
							}
						}
					} finally {
						reader.close();
					}
				} catch (final IOException ioe) {
					throw new TechnicalException(ioe);
				}
			}
		};
		return tread;
	}

	protected String getLastOutputLine() {
		return lastOutputLine;
	}

	protected boolean isSuccess(final String fullOutput) {
		return true;
	}

	protected String handleProgression(final String line) {
		return null;
	}

}
