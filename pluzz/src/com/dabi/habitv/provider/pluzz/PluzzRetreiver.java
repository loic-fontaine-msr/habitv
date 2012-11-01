package com.dabi.habitv.provider.pluzz;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class PluzzRetreiver {

	public static Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categories = new HashSet<>();

		Connection con = null;
		try {
			con = Jsoup.connect(PluzzConf.HOME_URL);

			final Elements select = con.get().select(".rub");
			CategoryDTO fatherCategory = null;
			final Map<String, CategoryDTO> fatherSet = new HashMap<>();
			for (final Element element : select) {
				for (final Element ul : element.children()) {
					for (final Element li : ul.children()) {
						final String liClass = li.attr("class");
						if ("section dontend".equals(liClass)) {
							final String name = li.text();
							fatherCategory = fatherSet.get(name);
							if (fatherCategory == null) {
								fatherCategory = new CategoryDTO(PluzzConf.NAME, name, name, PluzzConf.EXTENSION);
								categories.add(fatherCategory);
								fatherSet.put(name, fatherCategory);
							}
						} else if (fatherCategory != null) {
							final Element link = li.child(0);
							final String name = link.text();
							fatherCategory.addSubCategory(new CategoryDTO(PluzzConf.NAME, name, name, PluzzConf.EXTENSION));
						}
					}
				}
			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
		return categories;
	}

	public static Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList;
		URL feedUrl;
		try {
			feedUrl = new URL(PluzzConf.RSS_URL.replace("#CATEGORY#", category.getFatherCategory().getName()));

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
		for (final Object object : entries) {
			final SyndEntry entry = (SyndEntry) object;
			if (entry.getTitle().equals(category.getName())) {
				episodeList.add(new EpisodeDTO(category, (new SimpleDateFormat("YYYYMMddHHmm")).format(entry.getPublishedDate()), entry.getLink()));
			}
		}
		return episodeList;
	}
}
