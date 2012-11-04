package com.dabi.habitv.provider.d8;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBElement;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

class D8Retreiver {

	private static final Pattern JS_URL_PATTERN = Pattern.compile("<script type=[\'|\"]text/javascript[\'|\"] src=[\'|\"]([^\'\"]*player-vod-ads.js.[^\'\"]*)[\'|\"]></script>");
	private static final Pattern BASE_URL_PATTERN = Pattern.compile(".*baseUrl:\\s*\\\'(.*/)\\\',.*");
	private static final Pattern FLV_URL_PATTERN = Pattern.compile(".*url : \\\'([^\\\']*)\\\'\\s*},");

	private D8Retreiver() {

	}

	static Set<EpisodeDTO> findEpisodeByCategory(final ClassLoader classLoader, final CategoryDTO category) {
		final Set<EpisodeDTO> episodes = new HashSet<>();

		try {
			final Connection con = Jsoup.connect(category.getId());

			final Elements select = con.get().select(".home-replay-thumb");
			for (final Element element : select) {
				try {
					final Element aLink = element.child(2).child(0);
					final String title = aLink.text();
					final String url = aLink.attr("href");
					episodes.add(new EpisodeDTO(category, title, url));
				} catch (final IndexOutOfBoundsException e) {
					throw new TechnicalException(e);
				}
			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
		return episodes;
	}

	public static Set<CategoryDTO> findCategories(final ClassLoader classLoader) {

		final Set<CategoryDTO> categoryDTOs = new HashSet<>();
		for (int i = 0; i <= D8Conf.ROOT_CATEGORY_SIZE; i++) {
			final Rubriques rubriques = (Rubriques) RetrieverUtils.unmarshalInputStream(
					RetrieverUtils.getInputStreamFromUrl(String.format(D8Conf.CATALOG_URL, i)), Program.class.getPackage().getName(), classLoader);
			final Rubrique rubrique = rubriques.getRubrique();
			@SuppressWarnings("unchecked")
			final CategoryDTO categoryDTO = new CategoryDTO(D8Conf.NAME, ((JAXBElement<String>) rubrique.getContent().get(1)).getValue(),
					String.valueOf(rubrique.getId()), D8Conf.EXTENSION);
			categoryDTOs.add(categoryDTO);
			final Programs programs = (Programs) rubrique.getContent().get(3);
			for (final Program program : programs.getProgram()) {
				categoryDTO.addSubCategory(new CategoryDTO(D8Conf.NAME, program.getLabel(), program.getProgramUrlPartage(), D8Conf.EXTENSION));
			}
		}

		return categoryDTOs;
	}

	public static String findEpisodeUrl(EpisodeDTO episode) throws DownloadFailedException {
		String videoPage = RetrieverUtils.getUrlContent(episode.getUrl());
		final Matcher matcher = JS_URL_PATTERN.matcher(videoPage);
		final boolean hasMatched = matcher.find();
		String jsUrl = null;
		if (hasMatched) {
			jsUrl = matcher.group(matcher.groupCount());
			return findEpisodeUrlInJs(jsUrl, episode.getUrl());
		} else {
			throw new DownloadFailedException("can't find js url");
		}

	}

	private static String findEpisodeUrlInJs(String jsUrl, String episodeUrl) throws DownloadFailedException {
		String videoPage = RetrieverUtils.getUrlContentRef(jsUrl, episodeUrl);
		Matcher matcher = BASE_URL_PATTERN.matcher(videoPage);
		boolean hasMatched = matcher.find();
		String baseUrl = null;
		if (hasMatched) {
			baseUrl = matcher.group(matcher.groupCount());
		} else {
			throw new DownloadFailedException("can't find baseUrl");
		}
		matcher = FLV_URL_PATTERN.matcher(videoPage);
		hasMatched = matcher.find();
		String flvUrl = null;
		if (hasMatched) {
			flvUrl = matcher.group(matcher.groupCount());
		} else {
			throw new DownloadFailedException("can't find flvUrl");
		}

		return baseUrl + flvUrl;
	}
}
