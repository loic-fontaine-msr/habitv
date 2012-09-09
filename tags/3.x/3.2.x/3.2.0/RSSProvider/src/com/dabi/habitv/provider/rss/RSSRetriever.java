package com.dabi.habitv.provider.rss;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public final class RSSRetriever {

	private RSSRetriever() {

	}

	public static Set<EpisodeDTO> findEpisodeByCategory(final ClassLoader classLoader, final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList;
		URL feedUrl;
		try {
			feedUrl = new URL(category.getId());

			final SyndFeedInput input = new SyndFeedInput();
			final SyndFeed feed = input.build(new XmlReader(feedUrl));
			episodeList = convertFeedToEpisodeList(feed, category);
		} catch (IllegalArgumentException | FeedException | IOException e) {
			throw new TechnicalException(e);
		}
		return episodeList;
	}

	private static Set<EpisodeDTO> convertFeedToEpisodeList(final SyndFeed feed, final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new HashSet<EpisodeDTO>();
		final List<?> entries = feed.getEntries();
		if (!entries.isEmpty()) {
			for (final Object object : entries) {// FIXME error ici non affichée
				final SyndEntry entry = (SyndEntry) object;
				final List<?> enclosures = entry.getEnclosures();
				String url;
				if (!enclosures.isEmpty()) {
					url = ((SyndEnclosure) enclosures.get(0)).getUrl();
				} else {
					url = entry.getLink();
				}

				episodeList.add(new EpisodeDTO(category, entry.getTitle(), url));
			}
		}
		return episodeList;
	}
}
