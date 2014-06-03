package com.dabi.habitv.provider.clubic;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabi.habitv.api.plugin.api.PluginProviderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;

public class ClubicPluginManager extends BasePluginWithProxy implements
		PluginProviderInterface { // NO_UCD

	@Override
	public String getName() {
		return ClubicConf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodes = new HashSet<>();

		org.jsoup.nodes.Document doc = Jsoup.parse(getUrlContent(
				category.getId(), ClubicConf.ENCODING));

		final Element nav = doc.select(".listingVideo").get(0);

		for (Element liElement : nav.children()) {
			final Elements asElement = liElement.select("a");
			for (Element aElement : asElement) {
				if (aElement.hasAttr("href") && aElement.hasAttr("title")) {
					final String url = aElement.attr("href");
					final String name = aElement.attr("title");
					episodes.add(new EpisodeDTO(category, name, getUrl(url)));
					break;
				}
			}
		}

		return episodes;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categories = new HashSet<>();

		final org.jsoup.nodes.Document doc = Jsoup.parse(getUrlContent(
				ClubicConf.HOME_VIDEO_URL, ClubicConf.ENCODING));

		final Element nav = doc.select(".listingChaine").get(0);
		for (final Element chaineElement : nav.children()) {
			final Elements asElement = chaineElement.select("a");
			for (Element aElement : asElement) {
				if (aElement.hasAttr("href") && aElement.hasAttr("title")) {
					final String url = aElement.attr("href");
					final String name = aElement.attr("title");
					final CategoryDTO categoryDTO = new CategoryDTO(
							ClubicConf.NAME, name, getUrl(url),
							ClubicConf.EXTENSION);
					categories.add(categoryDTO);
					break;
				}
			}
		}

		return categories;
	}

	private String getUrl(String href) {
		return href.startsWith(FrameworkConf.HTTP_PREFIX) ? href
				: (ClubicConf.HOME_URL + href);
	}

}
