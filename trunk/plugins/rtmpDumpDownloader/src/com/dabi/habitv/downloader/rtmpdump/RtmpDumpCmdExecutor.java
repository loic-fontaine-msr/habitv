package com.dabi.habitv.downloader.rtmpdump;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dabi.habitv.framework.plugin.utils.CmdExecutor;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class RtmpDumpCmdExecutor extends CmdExecutor {

	private double percentage;

	private static final double MIN_PERCENTAGE = 99D;

	private static final Pattern PROGRESS_PATTERN = Pattern.compile("\\((\\d+\\.\\d)%\\)$");

	public RtmpDumpCmdExecutor(final String cmdProcessor, final String cmd, final CmdProgressionListener listener) {
		super(cmdProcessor, cmd, RtmpDumpConf.MAX_HUNG_TIME, listener);
	}

	@Override
	protected String handleProgression(final String line) {
		final Matcher matcher = PROGRESS_PATTERN.matcher(line);
		final boolean hasMatched = matcher.find();
		String ret = null;
		if (hasMatched) {
			ret = matcher.group(matcher.groupCount());
			percentage = Double.valueOf(ret);
		}
		return ret;
	}

	@Override
	protected boolean isSuccess(final String fullOutput) {
		return getLastOutputLine().contains("Download complete") && percentage > MIN_PERCENTAGE;
	}

	@Override
	protected long getHungProcessTime() {
		return RtmpDumpConf.HUNG_PROCESS_TIME;
	}

}
