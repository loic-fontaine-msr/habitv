package com.dabi.habitv.provider.arte;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabi.habitv.api.plugin.api.PluginProviderDownloaderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.utils.DownloadUtils;

public class ArtePluginManager extends BasePluginWithProxy implements PluginProviderDownloaderInterface { // NO_UCD

	@Override
	public String getName() {
		return ArteConf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodes = new LinkedHashSet<>();

		Document doc = Jsoup.parse(getUrlContent(category.getId()));
		for (Element aEp : doc.select("section#videos article a")) {
			String url = ArteConf.HOME_URL + aEp.attr("href");
			final String name = aEp.select("h3").first().text();
			if (!StringUtils.isEmpty(name)) {
				episodes.add(new EpisodeDTO(category, name, url));
			}
		}
//		for (Element aEp : doc.select("div#playlistContainer li")) {
//			String url = ArteConf.HOME_URL + aEp.attr("href");
//			final String name = aEp.select("h3").first().text();
//			if (!StringUtils.isEmpty(name)) {
//				episodes.add(new EpisodeDTO(category, name, url));
//			}
//		}		
		return episodes;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categories = new LinkedHashSet<>();

		final Document doc = Jsoup.parse(getUrlContent(ArteConf.HOME_URL));

		final Elements aChannels = doc.select("ul.next-language__list li.next-language__list-item a");

		for (final Element aLanguage : aChannels) {
			String href = aLanguage.attr("href");
			String language = aLanguage.text();
			CategoryDTO languageCat = new CategoryDTO(ArteConf.NAME, language, href, ArteConf.EXTENSION);
			languageCat.setDownloadable(false);
			languageCat.addSubCategories(findCategoryByLanguage(href));
			categories.add(languageCat);
		}

		return categories;
	}

	private Collection<CategoryDTO> findCategoryByLanguage(String languageUrl) {
		final Set<CategoryDTO> categories = new LinkedHashSet<>();

		final Document doc = Jsoup.parse(getUrlContent(languageUrl));

		final Elements aMainCats = doc.select("ul.next-menu-nav__main-menu li.next-menu-nav__menu-item a");

		for (final Element aMainCat : aMainCats) {
			String url = ArteConf.HOME_URL + aMainCat.attr("href");
			String text = aMainCat.text();
			CategoryDTO mainCat = new CategoryDTO(ArteConf.NAME, text, url, ArteConf.EXTENSION);
			mainCat.setDownloadable(false);
			mainCat.addSubCategories(findSubCategory(url));
			categories.add(mainCat);
		}

		return categories;
	}

	private Collection<CategoryDTO> findSubCategory(String catUrl) {
		final Set<CategoryDTO> categories = new LinkedHashSet<>();

		final Document doc = Jsoup.parse(getUrlContent(catUrl));

		addSubCategories(categories, doc, "collections");
		//addSubCategories(categories, doc, "playlists_");

		return categories;
	}

	private void addSubCategories(final Set<CategoryDTO> categories, final Document doc, String clazz) {
		final Elements aMainCats = doc.select("div[class~="+clazz+"_] article a");

		for (final Element aMainCat : aMainCats) {
			String href = aMainCat.attr("href");
			String text = aMainCat.select("h3").first().text();
			CategoryDTO cat = new CategoryDTO(ArteConf.NAME, text, href, ArteConf.EXTENSION);
			cat.setDownloadable(true);
			categories.add(cat);
		}
	}

	@Override
	public ProcessHolder download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders) throws DownloadFailedException {
		return DownloadUtils.download(downloadParam, downloaders, "youtube");
	}

	@Override
	public DownloadableState canDownload(String downloadInput) {
		return downloadInput.contains("arte") ? DownloadableState.SPECIFIC : DownloadableState.IMPOSSIBLE;
	}

}
