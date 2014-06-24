package com.dabi.habitv.provider.tf1;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import com.dabi.habitv.api.plugin.api.PluginProviderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.utils.DownloadUtils;

public class TF1PluginManager extends BasePluginWithProxy implements
		PluginProviderInterface { // NO_UCD

	private static final List<String> CATEGORIES_EXCLUDED = Arrays.asList(
			"Séries étrangères", "Fictions Françaises");

	@Override
	public String getName() {
		return TF1Conf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodes = new LinkedHashSet<>();

		org.jsoup.nodes.Document doc = Jsoup.parse(getUrlContent(category
				.getId()));
		findEpisodes(category, episodes, doc);

		Element navigation = findBlock(doc, "ol", "liste_dizaine");

		if (navigation != null) {
			for (int i = 1; i <= PAGE; i++) {
				Element menuItem = navigation.child(i + 1).child(0);
				String href = menuItem.attr("href");
				doc = Jsoup.parse(getUrlContent(getUrl(href)));

				findEpisodes(category, episodes, doc);
			}
		}

		return episodes;
	}

	private void findEpisodes(final CategoryDTO category,
			final Set<EpisodeDTO> episodes, org.jsoup.nodes.Document doc) {
		Element teaserList = findTeaserList(doc);
		if (teaserList == null) {
			for (final Element articleElement : doc.select("article")) {

				for (final Element aElement : articleElement.select("a")) {
					if (aElement.hasAttr("href")) {
						String url = aElement.attr("href");
						if (!url.equals("#")) {
							final String name = aElement.text();
							episodes.add(new EpisodeDTO(category, name,
									getUrl(url)));
						}
					}
				}
			}
		} else {
			for (final Element liElement : teaserList.children()) {
				final Element descriptionElement = liElement.child(1);
				final Element titreElement = descriptionElement.child(2);
				Element aElement = titreElement.children().size() > 0 ? titreElement
						.child(0) : descriptionElement.child(1).child(0);
				final String name = aElement.text();
				String url = aElement.attr("href");
				episodes.add(new EpisodeDTO(category, name, getUrl(url)));
			}
		}
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categories = new LinkedHashSet<>();

		final org.jsoup.nodes.Document doc = Jsoup
				.parse(getUrlContent(TF1Conf.HOME_URL));

		final Element nav = doc.select("#nav170509").get(0);
		for (final Element liElement : nav.child(0).children()) {
			final Element aElement = liElement.child(0);
			final String url = aElement.attr("href");
			final String name = aElement.text();
			if (!CATEGORIES_EXCLUDED.contains(name)) {
				final CategoryDTO categoryDTO = new CategoryDTO(TF1Conf.NAME,
						name, getUrl(url), TF1Conf.EXTENSION);
				categoryDTO.setDownloadable(true);
				Collection<CategoryDTO> subCategories = findSubCategories(categoryDTO);
				categoryDTO.addSubCategories(subCategories);
				categories.add(categoryDTO);
			}
		}

		return categories;
	}

	private static final int PAGE = 3;

	private Collection<CategoryDTO> findSubCategories(
			final CategoryDTO categoryFather) {
		final Set<CategoryDTO> categories = new LinkedHashSet<>();

		org.jsoup.nodes.Document doc = Jsoup.parse(getUrlContent(categoryFather
				.getId()));
		findSubCategories(categoryFather, categories, doc);

		Element navigation = findBlock(doc, "ol", "liste_dizaine");

		if (navigation != null) {
			for (int i = 1; i <= PAGE; i++) {
				Element menuItem = navigation.child(i + 1).child(0);
				String href = menuItem.attr("href");
				doc = Jsoup.parse(getUrlContent(getUrl(href)));
				findSubCategories(categoryFather, categories, doc);
			}
		}

		return categories;
	}

	private String getUrl(String href) {
		return DownloadUtils.isHttpUrl(href) ? href : (TF1Conf.HOME_URL + href);
	}

	private void findSubCategories(final CategoryDTO categoryFather,
			final Set<CategoryDTO> categories, org.jsoup.nodes.Document doc) {
		Element teaserList = findTeaserList(doc);
		if (teaserList != null) {
			for (final Element liElement : teaserList.children()) {
				final Element descriptionElement = liElement.child(1);

				final String name;
				String url;
				Element aElement = descriptionElement.select("a").get(0);
				url = aElement.attr("href");
				name = aElement.text();

				if (DownloadUtils.isHttpUrl(url)) {
					url = url.replace(TF1Conf.HOME_URL, "");
				}
				final String urlT = url.substring(1, url.length());
				int indexOfSlash = urlT.indexOf("/");
				final String catUrl = urlT.substring(0,
						indexOfSlash >= 0 ? indexOfSlash : urlT.length());

				if (!CATEGORIES_EXCLUDED.contains(name)) {
					final CategoryDTO categoryDTO = new CategoryDTO(
							TF1Conf.NAME, name, TF1Conf.HOME_URL + "/" + catUrl
									+ "/", TF1Conf.EXTENSION);
					categoryDTO.setDownloadable(true);
					categories.add(categoryDTO);
				}
			}
		}
	}

	private Element findTeaserList(final org.jsoup.nodes.Document doc) {
		return findBlock(doc, "ul", "teaserList");
	}

	private Element findBlock(final org.jsoup.nodes.Document doc, String node,
			String id) {
		for (Element ulElement : doc.select(node)) {
			for (Attribute attribute : ulElement.attributes()) {
				if (attribute.getValue().startsWith(id)) {
					return ulElement;
				}
			}
		}
		return null;
	}

}
