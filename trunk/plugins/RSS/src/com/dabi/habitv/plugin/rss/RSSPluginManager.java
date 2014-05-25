package com.dabi.habitv.plugin.rss;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dabi.habitv.api.plugin.api.PluginProviderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RSSPluginManager extends BasePluginWithProxy implements
		PluginProviderInterface {

	private static final int MIN_TITLE_SIZE = 5;

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList;
		try {
			final SyndFeedInput input = new SyndFeedInput();
			final SyndFeed feed = input.build(new XmlReader(
					getInputStreamFromUrl(category.getId())));
			episodeList = convertFeedToEpisodeList(feed, category);
		} catch (IllegalArgumentException | FeedException | IOException e) {
			throw new TechnicalException(e);
		}
		return episodeList;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categoryList = new HashSet<>();
		addCategoryTemplate(categoryList, "RSS",
				"§ID§||Saisissez l'URL d'un flux RSS");
		addCategoryTemplate(
				categoryList,
				"Dailymotion",
				"http://www.dailymotion.com/rss/user/§ID§/1||Saisissez l'identifiant d'un utilisateur dailymotion");
		addCategoryTemplate(
				categoryList,
				"Vimeo",
				"http://vimeo.com/§ID§/videos/rss||Saisissez l'identifiant d'un utilisateur vimeo");
		addCategoryTemplate(
				categoryList,
				"Youtube",
				"http://gdata.youtube.com/feeds/base/users/§ID§/uploads?alt=rss&amp;v=1&amp;orderby=published&amp;client=ytapi-youtube-profile||Saisissez l'identifiant d'un utilisateur youtube");
		return categoryList;
	}

	private void addCategoryTemplate(final Set<CategoryDTO> categoryList,
			String name, String id) {
		final CategoryDTO categoryDTO = new CategoryDTO(RSSConf.NAME, name, id,
				null, null, null);
		categoryDTO.setTemplate(true);
		categoryList.add(categoryDTO);
	}

	@Override
	public String getName() {
		return RSSConf.NAME;
	}

	private static Set<EpisodeDTO> convertFeedToEpisodeList(
			final SyndFeed feed, final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new HashSet<EpisodeDTO>();
		final List<?> entries = feed.getEntries();
		if (!entries.isEmpty()) {
			for (final Object object : entries) {
				final SyndEntry entry = (SyndEntry) object;
				final List<?> enclosures = entry.getEnclosures();
				String url;
				if (!enclosures.isEmpty()) {
					url = ((SyndEnclosure) enclosures.get(0)).getUrl();
				} else {
					url = entry.getLink();
				}

				String safeTtitle = entry.getTitle()
						.replaceAll("[^\\x00-\\x7F]", "").trim();
				if (safeTtitle.length() <= 5
						&& entry.getTitle().length() >= MIN_TITLE_SIZE) {
					safeTtitle = entry.getAuthor()
							+ "-"
							+ (new SimpleDateFormat("yyyyMMddHHmmss")
									.format(entry.getPublishedDate()));
				}
				episodeList.add(new EpisodeDTO(category, safeTtitle, url));
			}
		}
		return episodeList;
	}

}
