package com.dabi.habitv.process.episode;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.utils.FileUtils;

public class TokenReplacer {

	private TokenReplacer() {

	}

	private interface Replacer {
		String replace(EpisodeDTO episode);
	}

	private static final Map<String, Replacer> ref2Replacer = new HashMap<>();

	static {
		// EPISODE
		ref2Replacer.put("#EPISODE_NAME#", new Replacer() {

			@Override
			public String replace(EpisodeDTO episode) {
				return ensure(episode.getName());
			}
		});

		// EPISODE
		ref2Replacer.put("#EPISODE_NAME_CUT#", new Replacer() {

			@Override
			public String replace(EpisodeDTO episode) {
				final String episodeName = ensure(episode.getName());
				return ensure(episodeName.substring(0, Math.min(40, episodeName.length() - 1)));
			}
		});

		// CHANNEL
		ref2Replacer.put("#CHANNEL_NAME#", new Replacer() {

			@Override
			public String replace(EpisodeDTO episode) {
				return ensure(episode.getCategory().getChannel());
			}
		});

		// TV SHOW
		ref2Replacer.put("#TVSHOW_NAME#", new Replacer() {

			@Override
			public String replace(EpisodeDTO episode) {
				return ensure(episode.getCategory().getName());
			}
		});

		// TV SHOW
		ref2Replacer.put("#EXTENSION#", new Replacer() {

			@Override
			public String replace(EpisodeDTO episode) {
				return ensure(episode.getCategory().getExtension());
			}
		});
	}

	private static String ensure(final String input) {
		return FileUtils.sanitizeFilename(input);
	}

	public static String replaceAll(final String input, final EpisodeDTO episode) {
		String ret = input;
		for (Entry<String, Replacer> toReplace : ref2Replacer.entrySet()) {
			ret = ret.replaceAll(toReplace.getKey(), toReplace.getValue().replace(episode));
		}
		return ret;
	}

	public static String replaceRef(String ref, EpisodeDTO episode) {
		return ref2Replacer.get(ref).replace(episode);
	}
}
