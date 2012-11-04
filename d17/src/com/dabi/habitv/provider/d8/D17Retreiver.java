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
import com.dabi.habitv.provider.d17.cat.Program;
import com.dabi.habitv.provider.d17.cat.Programs;

class D17Retreiver {

	private static final Pattern JS_URL_PATTERN = Pattern.compile("<script type=[\'|\"]text/javascript[\'|\"] src=[\'|\"]([^\'\"]*player-vod-ads.js.[^\'\"]*)[\'|\"]></script>");
	private static final Pattern BASE_URL_PATTERN = Pattern.compile(".*baseUrl:\\s*\\\'(.*/)\\\',.*");
	private static final Pattern FLV_URL_PATTERN = Pattern.compile(".*url : \\\'([^\\\']*)\\\'\\s*},");

	private D17Retreiver() {

	}

	static Set<EpisodeDTO> findEpisodeByCategory(final ClassLoader classLoader, final CategoryDTO category) {
		Set<EpisodeDTO> episodes = new HashSet<>();

		try {
			final Connection con = Jsoup.connect(String.format(D17Conf.PROGRAM_URL, category.getId()));

			final Elements select = con.get().select(".vignette_container");
			for (final Element element : select) {
				try {
					final Element aLink = element.child(0);
					final String title = aLink.attr("title");
					final String url = aLink.attr("href");
					episodes.add(new EpisodeDTO(category, title, url));
				} catch (final IndexOutOfBoundsException e) {
					throw new TechnicalException(e);
				}
			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}

		// si un seul épisode, il faut ajouter la date dans le titre pour
		// pouvoir le redl quand il change
		if (episodes.size() == 1) {
			String date = findDate(classLoader, category);
			final Set<EpisodeDTO> episodesDated = new HashSet<>();
			for (EpisodeDTO episodeDTO : episodes) {
				episodesDated.add(new EpisodeDTO(category, episodeDTO.getName() + " " + date, episodeDTO.getUrl()));
			}
			episodes = episodesDated;
		}

		return episodes;
	}

	private static String findDate(ClassLoader classLoader, CategoryDTO category) {

		final com.dabi.habitv.provider.d17.ep.Program program = (com.dabi.habitv.provider.d17.ep.Program) RetrieverUtils.unmarshalInputStream(RetrieverUtils
				.getInputStreamFromUrl(String.format(D17Conf.PROGRAM_API_URL, category.getId())), com.dabi.habitv.provider.d17.ep.Program.class.getPackage()
				.getName(), classLoader);
		String ret ="";
		for (Object object : program.getDetail().getContent()) {
			if (object instanceof JAXBElement){
				JAXBElement<?> element =(JAXBElement<?>) object;
				if ("date".equals(element.getName().getLocalPart())){
					ret =  (String) element.getValue();
					break;
				}
			}
			
		}
		return ret;
	}

	public static Set<CategoryDTO> findCategories(final ClassLoader classLoader) {

		final Set<CategoryDTO> categoryDTOs = new HashSet<>();
		for (int i = 0; i <= D17Conf.ROOT_CATEGORY_SIZE; i++) {
			final Programs programs = (Programs) RetrieverUtils.unmarshalInputStream(
					RetrieverUtils.getInputStreamFromUrl(String.format(D17Conf.CATALOG_URL, i)), Programs.class.getPackage().getName(), classLoader);
			for (final Program program : programs.getProgram()) {
				categoryDTOs.add(new CategoryDTO(D17Conf.NAME, program.getIntitule(), String.valueOf(program.getId()), D17Conf.EXTENSION));
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
