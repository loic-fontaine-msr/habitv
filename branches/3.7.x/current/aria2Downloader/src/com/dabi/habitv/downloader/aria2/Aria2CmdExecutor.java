package com.dabi.habitv.downloader.aria2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dabi.habitv.framework.plugin.utils.CmdExecutor;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

class Aria2CmdExecutor extends CmdExecutor {

	private static final Pattern PROGRESS_PATTERN = Pattern.compile("\\[.*\\((.*)\\%\\).*\\]");

	Aria2CmdExecutor(final String cmdProcessor, final String cmd, final CmdProgressionListener listener) {
		super(cmdProcessor, cmd, Aria2Conf.MAX_HUNG_TIME, listener);
	}

	@Override
	protected String handleProgression(final String line) {
		final Matcher matcher = PROGRESS_PATTERN.matcher(line);
		final boolean hasMatched = matcher.find();
		String ret = null;
		if (hasMatched) {
			ret = matcher.group(matcher.groupCount());
		}
		return ret;
	}

	@Override
	protected boolean isSuccess(final String fullOutput) {
		return fullOutput.contains("(OK):download completed");
	}

}
