package com.dabi.habitv.utils;

import java.util.List;

import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public final class FilterUtils {

	private FilterUtils() {

	}

	public static boolean filterByIncludeExcludeAndDownloaded(final EpisodeDTO episode, final List<String> includeList, final List<String> excludeList) {
		boolean include = true;
		// manage include
		include = filterInclude(includeList, episode.getName());
		if (include) {
			// manage exclude
			include = filterExclude(excludeList, episode.getName());
		}
		return include;
	}

	private static boolean filterExclude(final List<String> excludeList, final String episodeName) {
		boolean include = true;
		if (excludeList != null && !excludeList.isEmpty()) {
			boolean match = false;
			for (final String excludePattern : excludeList) {
				match = episodeName.matches(excludePattern);
				if (match) {
					include = false;
					break;
				}
			}

		}
		return include;
	}

	private static boolean filterInclude(final List<String> includeList, final String episodeName) {
		boolean include = true;
		if (includeList != null && !includeList.isEmpty()) {
			include = false;
			for (final String includePattern : includeList) {
				include = episodeName.matches(includePattern);
				if (include) {
					break;
				}
			}
		}
		return include;
	}

}
