package com.dabi.habitv.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public final class FilterUtils {

	private FilterUtils() {

	}

	public static Set<EpisodeDTO> filterByIncludeExcludeAndDownloaded(final Set<EpisodeDTO> episodeList, final List<String> includeList,
			final List<String> excludeList, final Set<String> downloadedFiles) {
		final Iterator<EpisodeDTO> episodeIt = episodeList.iterator();
		EpisodeDTO episode;
		while (episodeIt.hasNext()) {

			episode = episodeIt.next();
			boolean include = true;

			if (downloadedFiles.contains(episode.getName())) {
				// file already downloaded
				include = false;
			} else {
				// manage include
				include = filterInclude(includeList, episode.getName());
				if (include) {
					// manage exclude
					include = filterExclude(excludeList, episode.getName());
				}
			}
			if (!include) {
				episodeIt.remove();
			}
		}
		return episodeList;
	}

	private static boolean filterExclude(final List<String> excludeList, final String episodeName) {
		boolean include = true;
		if (excludeList != null && !excludeList.isEmpty()) {
			boolean match = false;
			for (String excludePattern : excludeList) {
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
			for (String includePattern : includeList) {
				include = episodeName.matches(includePattern);
				if (include) {
					break;
				}
			}
		}
		return include;
	}

}
