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
		// compilation de la regex
		final Pattern pattern = Pattern.compile("\\((\\d+\\.\\d)%\\)$");
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
	protected boolean isSuccess() {
		return getLastOutputLine().contains("Download complete");
	}

}
