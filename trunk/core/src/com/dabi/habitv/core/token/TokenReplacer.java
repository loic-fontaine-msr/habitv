package com.dabi.habitv.core.token;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.dabi.habitv.core.config.HabitTvConf;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.utils.FileUtils;

public final class TokenReplacer {

	private static final Map<String, Replacer> REF2REPLACER = new HashMap<>();

	static {
		// EPISODE
		final Replacer episodeReplacer = new Replacer() {

			@Override
			public String replace(final EpisodeDTO episode) {
				return ensure(episode.getName());
			}
		};
		REF2REPLACER.put("#EPISODE_NAME#", episodeReplacer);
		REF2REPLACER.put("#EPISODE#", episodeReplacer);

		// CHANNEL
		final Replacer channelReplacer = new Replacer() {

			@Override
			public String replace(final EpisodeDTO episode) {
				return ensure(episode.getCategory().getChannel());
			}
		};
		REF2REPLACER.put("#CHANNEL_NAME#", channelReplacer);
		REF2REPLACER.put("#CHANNEL#", channelReplacer);
		REF2REPLACER.put("#PROVIDER#", channelReplacer);
		REF2REPLACER.put("#PROVIDER_NAME#", channelReplacer);

		// TV SHOW
		final Replacer categoryProvider = new Replacer() {

			@Override
			public String replace(final EpisodeDTO episode) {
				return ensure(episode.getCategory().getName());
			}
		};
		REF2REPLACER.put("#TVSHOW_NAME#", categoryProvider);
		REF2REPLACER.put("#CATEGORY_NAME#", categoryProvider);
		REF2REPLACER.put("#CATEGORY#", categoryProvider);

		// TV SHOW
		REF2REPLACER.put("#EXTENSION#", new Replacer() {

			@Override
			public String replace(final EpisodeDTO episode) {
				return ensure(episode.getCategory().getExtension());
			}
		});

		setCutSize(HabitTvConf.DEFAULT_CUT_SIZE);
	}

	private static String ensure(final String input) {
		return FileUtils.sanitizeFilename(input);
	}

	public static void setCutSize(final int cutSize) {

		for (final Entry<String, Replacer> ref2Replacer : REF2REPLACER.entrySet()) {
			String key = ref2Replacer.getKey();
			// remove last #
			key = key.substring(0, key.length() - 2) + "_CUT#";
			REF2REPLACER.put(key, new Replacer() {
				
				@Override
				String replace(EpisodeDTO episode) {
					final String replaced = ref2Replacer.getValue().replace(episode);
					return replaced.substring(0, Math.min(cutSize, replaced.length() - 1));
				}
			});
		}

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
