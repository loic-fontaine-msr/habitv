package com.dabi.habitv.exporter.ffmpeg;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dabi.habitv.framework.plugin.utils.CmdExecutor;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class FFMPEGCmdExecutor extends CmdExecutor {

	private static final Pattern PERCENTAGE_PATTERN = Pattern.compile(".*\\((\\d+\\.+\\d+)%\\).*");

	private static final Pattern TIME_PATTERN = Pattern.compile("time=(.*?) bitrate");

	private double percentage;

	private static final double MIN_PERCENTAGE = 99D;

	public static final String NAME = "ffmpeg";
	private Long duration = null;

	public FFMPEGCmdExecutor(final String cmdProcessor, final String cmd, final CmdProgressionListener listener) {
		super(cmdProcessor, cmd, FFMPEGConf.MAX_HUNG_TIME, listener);
	}

	@Override
	protected String handleProgression(final String line) {
		if (duration == null) {
			duration = findDuration(line);
		}
		final Matcher matcher = TIME_PATTERN.matcher(line);
		// lancement de la recherche de toutes les occurrences
		final boolean hasMatched = matcher.find();
		String ret = null;
		// si recherche fructueuse
		if (hasMatched && duration != null) {
			final long currentDuration = Double.valueOf(Double.parseDouble(matcher.group(matcher.groupCount()))).longValue();
			ret = String.valueOf((currentDuration * PERCENTAGE / duration));
			percentage = Double.valueOf(ret);
		} else {
			ret = matchPercentage(line);
		}
		return ret;
	}

	private String matchPercentage(String line) {
		// création d’un moteur de recherche
		final Matcher matcher = PERCENTAGE_PATTERN.matcher(line);
		// lancement de la recherche de toutes les occurrences
		final boolean hasMatched = matcher.find();
		String ret = null;
		// si recherche fructueuse
		if (hasMatched) {
			ret = matcher.group(matcher.groupCount());
		}
		return ret;
	}

	private static Long findDuration(final String line) {
		// création d’un moteur de recherche
		final Matcher matcher = PERCENTAGE_PATTERN.matcher(line);
		// lancement de la recherche de toutes les occurrences
		final boolean hasMatched = matcher.find();
		Long ret = null;
		// si recherche fructueuse
		if (hasMatched) {
			final String durationFormatted = matcher.group(matcher.groupCount());
			final String[] durationSplitted = durationFormatted.split(":");
			final long hours = Long.valueOf(durationSplitted[0]);
			final long minutes = Long.valueOf(durationSplitted[1]);
			final long seconds = Double.valueOf(durationSplitted[2]).longValue();
			ret = TimeUnit.SECONDS.convert(hours, TimeUnit.HOURS) + TimeUnit.SECONDS.convert(minutes, TimeUnit.MINUTES) + seconds;
		}
		return ret;
	}

	@Override
	protected boolean isSuccess(final String fullOutput) {
		return percentage > MIN_PERCENTAGE;
	}

}
