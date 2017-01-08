package com.dabi.habitv.provider.beinsport;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabi.habitv.api.plugin.api.PluginProviderDownloaderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.utils.DownloadUtils;
import com.dabi.habitv.framework.plugin.utils.SoccerUtils;

public class BeinSportPluginManager extends BasePluginWithProxy implements PluginProviderDownloaderInterface { // NO_UCD

	@Override
	public String getName() {
		return BeinSportConf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new LinkedHashSet<>();

		for (int i = 1; i <= 3; i++) {
			final org.jsoup.nodes.Document doc = Jsoup
					.parse(getUrlContent(BeinSportConf.HOME_URL + category.getId() + "/" + i));

			for (final Element aHref : doc.select("figcaption a")) {
				String href = BeinSportConf.HOME_URL + aHref.attr("href");
				String name = aHref.text();
				name = SoccerUtils.maskScore(name);
				episodeList.add(new EpisodeDTO(category, name, href));
			}
		}
		return episodeList;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categoryDTOs = new LinkedHashSet<>();
		CategoryDTO videoCategory = new CategoryDTO(BeinSportConf.VIDEOS_CATEGORY, BeinSportConf.VIDEOS_CATEGORY,
				BeinSportConf.VIDEOS_URL, BeinSportConf.EXTENSION);
		videoCategory.setDownloadable(false);
		addSubCategories(videoCategory);
		categoryDTOs.add(videoCategory);
		final CategoryDTO replayCategory = new CategoryDTO(BeinSportConf.REPLAY_CATEGORY, BeinSportConf.REPLAY_CATEGORY,
				BeinSportConf.REPLAY_URL, BeinSportConf.EXTENSION);
		replayCategory.setDownloadable(true);
		categoryDTOs.add(replayCategory);
		addSubCategories(replayCategory);
		return categoryDTOs;
	}

	private void addSubCategories(CategoryDTO category) {
		org.jsoup.nodes.Document doc;
		try {
			doc = Jsoup.parse(getInputStreamFromUrl(category.getId()), "UTF-8", category.getId());
		} catch (IOException e) {
			throw new TechnicalException(e);
		}

		addSubCategories(category, doc);
	}

	private void addSubCategories(CategoryDTO category, Element doc) {
		for (final Element liMain : doc.select(".top_menu__list>li")) {
			Elements aMain = liMain.select(">a");
			if (aMain.hasClass("taxonomy-mobile")) {
				final String href = aMain.attr("href") + "videos";
				final String text = aMain.text();
				CategoryDTO mainCat = buildSubCategory(category, text, href);
				addSubSubCat(mainCat, doc);
				category.addSubCategory(mainCat);
			}
		}
	}

	private void addSubSubCat(CategoryDTO mainCat, Element doc) {
		Iterator<Element> it = doc.select(".sports_links_column>*").iterator();
		while (it.hasNext()) {
			Element element = it.next();
			if ("h3".equals(element.tagName())) {
				Element link = element.select("a").first();
				if (mainCat.getId().equals(link.attr("href")+"videos")) {
					element = it.next();
					for (Element aLink : element.select("li a")) {
						String text = aLink.text();
						String href = aLink.attr("href") + "videos";
						CategoryDTO secondCat = buildSubCategory(mainCat, text, href);
						mainCat.addSubCategory(secondCat);
					}
				}
			}
		}
	}

	private CategoryDTO buildSubCategory(CategoryDTO category, final String name, String cat) {
		CategoryDTO subCategory = new CategoryDTO(BeinSportConf.NAME, name, cat, BeinSportConf.EXTENSION);
		subCategory.setDownloadable(true);
		return subCategory;
	}

	@Override
	public ProcessHolder download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders)
			throws DownloadFailedException {
		return DownloadUtils.download(
				DownloadParamDTO.buildDownloadParam(downloadParam, findUrlDownload(downloadParam.getDownloadInput())),
				downloaders);
	}

	private String findUrlDownload(String downloadInput) {
		org.jsoup.nodes.Document doc;
		try {
			doc = Jsoup.parse(getInputStreamFromUrl(downloadInput), "UTF-8", downloadInput);
			return doc.select("iframe").first().attr("src");
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
	}

	@Override
	public DownloadableState canDownload(final String downloadInput) {
		return downloadInput.startsWith(BeinSportConf.HOME_URL) ? DownloadableState.SPECIFIC
				: DownloadableState.IMPOSSIBLE;
	}

}
