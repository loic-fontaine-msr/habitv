package com.dabi.habitv.provider.wat;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabi.habitv.api.plugin.api.PluginProviderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;

public class WatPluginManager extends BasePluginWithProxy implements PluginProviderInterface { // NO_UCD

	@Override
	public String getName() {
		return WatConf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodes = new LinkedHashSet<>();

		Document doc = Jsoup.parse(getUrlContent(category.getId()));
		for (Element aElement : doc.select("div.video-small a.title")) {
			final String name = aElement.text();
			String url = getFullUrl(aElement.attr("href"));
			if (!StringUtils.isEmpty(name) && !"#".equals(url)) {
				episodes.add(new EpisodeDTO(category, name, url));
			}
		}
		return episodes;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categories = new LinkedHashSet<>();

		final Document doc = Jsoup.parse(getUrlContent(WatConf.HOME_URL));

		final Element nav = doc.select("div.submenu ul.navigation").first();
		for (final Element liElement : nav.children()) {
			Elements aMainElement = liElement.select("a");
			String mainCatTitle = aMainElement.first().text();
			String mainCatUrl = WatConf.HOME_URL + aMainElement.attr("href");
			final CategoryDTO mainCat = new CategoryDTO(WatConf.NAME, mainCatTitle, mainCatUrl, WatConf.EXTENSION);
			mainCat.setDownloadable(true);
			addSubCategoriesFromMenu(liElement, mainCat);
			addSubCategoriesFromPage(mainCatUrl, mainCat);
			if (!mainCat.getSubCategories().isEmpty()) {
				categories.add(mainCat);
			}
		}

		return categories;
	}

	private void addSubCategoriesFromPage(String mainCatUrl, CategoryDTO mainCat) {
		final Document doc = Jsoup.parse(getUrlContent(mainCatUrl));
		for (final Element shelfElement : doc.select("div.shelf")) {
			Element aElement = shelfElement.select("h2 a").first();
			if (aElement != null) {
				String title = aElement.text();
				String url = WatConf.HOME_URL + aElement.attr("href");
				final CategoryDTO subCat = new CategoryDTO(WatConf.NAME, title, getAllVidUrl(url), WatConf.EXTENSION);
				subCat.setDownloadable(true);
				subCat.addSubCategories(findSubCategoriesInElement(shelfElement));

				final CategoryDTO existingSubCat = findSubCategorieById(mainCat, subCat.getId());
				if (existingSubCat == null) {
					if (!subCat.getSubCategories().isEmpty()) {
						mainCat.addSubCategory(subCat);
					}
				} else {
					mergeSubCategories(existingSubCat, subCat);
				}
			}
		}

		final CategoryDTO partnerCat = new CategoryDTO(WatConf.NAME, "Partenaires", "Partenaires", WatConf.EXTENSION);
		partnerCat.setDownloadable(false);
		for (final Element aElement : doc.select("ul.partner-list li a")) {
			String title = aElement.children().first().attr("alt");
			if (!StringUtils.isEmpty(title)) {
				String url = WatConf.HOME_URL + aElement.attr("href");
				final CategoryDTO subCat = new CategoryDTO(WatConf.NAME, title, url, WatConf.EXTENSION);
				subCat.setDownloadable(true);
				partnerCat.addSubCategory(subCat);
			}
		}
		if (!partnerCat.getSubCategories().isEmpty()) {
			mainCat.addSubCategory(partnerCat);
		}
	}

	private void addSubCategoriesFromMenu(final Element liElement, final CategoryDTO mainCat) {
		for (Element aElement : liElement.select("ul.subNav a")) {
			String subCatTitle = aElement.text();
			String subCatUrl = getFullUrl(aElement.attr("href"));
			final CategoryDTO subCat = new CategoryDTO(WatConf.NAME, subCatTitle, getAllVidUrl(subCatUrl), WatConf.EXTENSION);
			subCat.setDownloadable(true);
			subCat.addSubCategories(findSubCategories(subCatUrl));
			if (!subCat.getSubCategories().isEmpty()) {
				mainCat.addSubCategory(subCat);
			}
		}
	}

	private String getAllVidUrl(String subCatUrl) {
		return subCatUrl + "/toutes-videos";
	}

	private String getFullUrl(String url) {
		return url.startsWith("/") ? (WatConf.HOME_URL + url) : url;
	}

	private Collection<CategoryDTO> findSubCategories(final String subCatUrl) {
		return findSubCategoriesInElement(Jsoup.parse(getUrlContent(subCatUrl)));
	}

	private Collection<CategoryDTO> findSubCategoriesInElement(Element mainElement) {
		final List<CategoryDTO> categories = new LinkedList<>();
		for (Element aElement : mainElement.select("li.hoverbgcc div.title a")) {
			String catUrl = getFullUrl(aElement.attr("href"));
			String name = aElement.text();
			final CategoryDTO categoryDTO = new CategoryDTO(WatConf.NAME, name, catUrl, WatConf.EXTENSION);
			categoryDTO.setDownloadable(true);
			categories.add(categoryDTO);
		}

		Collections.sort(categories);
		return new LinkedHashSet<>(categories);
	}

	// FIXME use CategoryDTO.
	private CategoryDTO findSubCategorieById(CategoryDTO cat, String id) {
		for (CategoryDTO subCategory : cat.getSubCategories()) {
			if (id.equals(subCategory.getId())) {
				return subCategory;
			}

		}
		return null;
	}

	private void mergeSubCategories(CategoryDTO cat, CategoryDTO otherCat) {
		for (CategoryDTO otherSubCat : otherCat.getSubCategories()) {
			CategoryDTO subCat = cat.findSubCategorieById(otherSubCat.getId());
			if (subCat == null) {
				cat.addSubCategory(otherSubCat);
			} else {
				subCat.mergeSubCategories(otherSubCat);
			}
		}
	}
}
