package com.dabi.habitv.plugin.adobeHDS;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dabi.habitv.framework.plugin.utils.CmdExecutor;

public class AdobeHDSCmdExecutor extends CmdExecutor {

	private double percentage;

	private static final double MIN_PERCENTAGE = 99D;

	private static final Pattern PROGRESS_PATTERN = Pattern
			.compile("Downloading (\\d+)\\/(\\d+) fragments$");

	public AdobeHDSCmdExecutor(final String cmdProcessor, final String cmd) {
		super(cmdProcessor, cmd, AdobeHDSConf.MAX_HUNG_TIME);
	}

	@Override
	protected String handleProgression(final String line) {
		final Matcher matcher = PROGRESS_PATTERN.matcher(line);
		final boolean hasMatched = matcher.find();
		String ret = null;
		if (hasMatched) {
			if (matcher.groupCount() > 1) {
				String nbrFragTotal = matcher.group(matcher.groupCount());
				String nbrFrag = matcher.group(matcher.groupCount() - 1);
				percentage = Double.valueOf(nbrFrag) * 100
						/ Double.valueOf(nbrFragTotal);
				ret = String.valueOf(percentage);
			}
		}
		return ret;
	}

	@Override
	protected boolean isSuccess(final String fullOutput) {
		return (getLastOutputLine().contains(
				"All fragments downloaded successfully") || getLastOutputLine()
				.contains("Finished")) && percentage > MIN_PERCENTAGE;
	}

	@Override
	protected long getHungProcessTime() {
		return AdobeHDSConf.HUNG_PROCESS_TIME;
	}

}
