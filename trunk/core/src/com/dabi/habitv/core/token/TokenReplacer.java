package com.dabi.habitv.core.token;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.dabi.habitv.core.config.HabitTvConf;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.utils.FileUtils;

public final class TokenReplacer {

	private final Integer tokenCutValueMaxSize;

	public TokenReplacer(final Integer tokenCutValueMaxSize) {

	}

	private interface Replacer {
		String replace(EpisodeDTO episode);
	}

	private static final Map<String, Replacer> REF2REPLACER = new HashMap<>();

	static {
		// EPISODE
		REF2REPLACER.put("#EPISODE_NAME#", new Replacer() {

			@Override
			public String replace(final EpisodeDTO episode) {
				return ensure(episode.getName());
			}
		});

		// EPISODE
		REF2REPLACER.put("#EPISODE_NAME_CUT#", new Replacer() {

			@Override
			public String replace(final EpisodeDTO episode) {
				final String episodeName = ensure(episode.getName());
				return ensure(episodeName.substring(0, Math.min(HabitTvConf.CUT_SIZE, episodeName.length() - 1)));
			}
		});

		// CHANNEL
		REF2REPLACER.put("#CHANNEL_NAME#", new Replacer() {

			@Override
			public String replace(final EpisodeDTO episode) {
				return ensure(episode.getCategory().getChannel());
			}
		});

		// TV SHOW
		REF2REPLACER.put("#TVSHOW_NAME#", new Replacer() {

			@Override
			public String replace(final EpisodeDTO episode) {
				return ensure(episode.getCategory().getName());
			}
		});

		// TV SHOW
		REF2REPLACER.put("#EXTENSION#", new Replacer() {

			@Override
			public String replace(final EpisodeDTO episode) {
				return ensure(episode.getCategory().getExtension());
			}
		});
	}

	private static String ensure(final String input) {
		return FileUtils.sanitizeFilename(input);
	}

	public static String replaceAll(final String input, final EpisodeDTO episode) {
		String ret = input;
		for (final Entry<String, Replacer> toReplace : REF2REPLACER.entrySet()) {
			ret = ret.replaceAll(toReplace.getKey(), toReplace.getValue().replace(episode));
		}
		return ret;
	}

	public static String replaceRef(final String ref, final EpisodeDTO episode) {
		return REF2REPLACER.get(ref).replace(episode);
	}
}
