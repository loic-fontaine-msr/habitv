package com.dabi.habitv.plugin.youtube;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.utils.CmdExecutor;

public class YoutubeDLCmdExecutor extends CmdExecutor {

	private static final Pattern PROGRESS_PATTERN = Pattern
			.compile(".*\\s(\\d+.\\d+)%.*");

	public YoutubeDLCmdExecutor(final String cmdProcessor, final String cmd) {
		super(cmdProcessor, cmd, YoutubeConf.MAX_HUNG_TIME);
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
		return true;
	}

	@Override
	protected long getHungProcessTime() {
		return FrameworkConf.HUNG_PROCESS_TIME;
	}

}
