package com.dabi.habitv.downloader.rtmpdump;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.utils.CmdExecutor;

public class RtmpDumpCmdExecutor extends CmdExecutor {

	public RtmpDumpCmdExecutor(final String cmd,
			final CmdProgressionListener listener) {
		super(cmd, listener);
	}

	@Override
	protected String handleProgression(final String line) {
		final Pattern pattern = Pattern.compile("\\((\\d+\\.\\d)%\\)$");
		final Matcher matcher = pattern.matcher(line);
		final boolean hasMatched = matcher.find();
		String ret = null;
		if (hasMatched) {
			ret = matcher.group(matcher.groupCount());
		}
		return ret;
	}

	@Override
	protected boolean isSuccess(final String fullOutput) {
		return getLastOutputLine().contains("Download complete");
	}

}
