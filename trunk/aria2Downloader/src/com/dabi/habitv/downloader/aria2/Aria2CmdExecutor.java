package com.dabi.habitv.downloader.aria2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dabi.habitv.framework.plugin.utils.CmdExecutor;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

class Aria2CmdExecutor extends CmdExecutor {

	Aria2CmdExecutor(final String cmdProcessor, final String cmd, final CmdProgressionListener listener) {
		super(cmdProcessor, cmd, listener);
	}

	@Override
	protected String handleProgression(final String line) {
		final Pattern pattern = Pattern.compile("\\[.*\\((.*)\\%\\).*\\]");
		final Matcher matcher = pattern.matcher(line);
		// lancement de la recherche de toutes les occurrences
		final boolean hasMatched = matcher.find();
		// si recherche fructueuse
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
