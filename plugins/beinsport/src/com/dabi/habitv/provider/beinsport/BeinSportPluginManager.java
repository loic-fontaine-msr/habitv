package com.dabi.habitv.provider.beinsport;

import java.io.IOException;
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

		final org.jsoup.nodes.Document doc = Jsoup.parse(getUrlContent(category.getId()));

		for (final Element aHref : doc.select("article.cluster_video__article h3 a")) {
			final String href = BeinSportConf.HOME_URL + aHref.attr("data-url");
			String name = aHref.text();
			name = SoccerUtils.maskScore(name);
			episodeList.add(new EpisodeDTO(category, name, href));
		}
		return episodeList;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categoryDTOs = new LinkedHashSet<>();
		CategoryDTO videoCategory = new CategoryDTO(BeinSportConf.VIDEOS_CATEGORY, BeinSportConf.VIDEOS_CATEGORY, BeinSportConf.VIDEOS_URL,
				BeinSportConf.EXTENSION);
		videoCategory.setDownloadable(true);
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

		Elements selects = doc.select("select.bein-selectBox");
		Element showTypeSelect = selects.get(1);
		Element catSelect = selects.get(0);

		addSubCategories(category, catSelect, showTypeSelect);
	}

	private void addSubCategories(CategoryDTO category, Element catSelect, Element showTypeSelect) {
		for (final Element option : catSelect.select("option")) {
			final String catRel = option.attr("value");
			if (!"all".equals(catRel)) {
				String catName = option.text();
				CategoryDTO catCategory = buildSubCategory(category, catName, catRel, "all");
				category.addSubCategory(catCategory);
				for (final Element optionShow : showTypeSelect.select("option")) {
					final String showRel = optionShow.attr("value");
					if (!"all".equals(showRel)) {
						String showName = optionShow.text();
						CategoryDTO showCategory = buildSubCategory(category, catName + " : " + showName, catRel, showRel);
						catCategory.addSubCategory(showCategory);
					}
				}
			}
		}
	}

	private CategoryDTO buildSubCategory(CategoryDTO category, final String name, String cat, String type) {
		CategoryDTO subCategory = new CategoryDTO(BeinSportConf.NAME, name, category.getId() + "/" + cat + "/" + type,
				BeinSportConf.EXTENSION);
		subCategory.setDownloadable(true);
		return subCategory;
	}

	@Override
	public ProcessHolder download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders)
			throws DownloadFailedException {
		return DownloadUtils.download(
				DownloadParamDTO.buildDownloadParam(downloadParam, findUrlDownload(downloadParam.getDownloadInput())), downloaders);
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
		return downloadInput.startsWith(BeinSportConf.HOME_URL) ? DownloadableState.SPECIFIC : DownloadableState.IMPOSSIBLE;
	}

}
