package com.dabi.habitv.provider.nrj12;

import java.util.HashSet;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

public class NRJ12PluginManager extends BasePluginWithProxy implements
		PluginProviderDownloaderInterface {

	@Override
	public String getName() {
		return NRJ12Conf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodes = new LinkedHashSet<>();
		final String main_url = NRJ12Conf.HOME_URL
				+ "/replay-4203/collectionvideo/";
		// System.out.println("category_url=" + category_url);
		final org.jsoup.nodes.Document doc = Jsoup.parse(getUrlContent(
				main_url, NRJ12Conf.ENCODING));
		final String name = category.getName();
		// System.out.println("name='" + name + "'");
		int i = 0;
		while (true) {
			final String id = String.format("liste_%d", i);
			Element element = doc.getElementById(id);
			if (element == null)
				break;
			final Element anchor = element.child(0).child(0);
			String identifier = anchor.attr("title");
			if (identifier == "") {
				identifier = "_other_";
			}
			// System.out.println("identifier='" + identifier + "'");
			if (name.equals(identifier)) {
				Elements select2 = element.getElementsByClass("content");
				if (select2.size() == 1) {
					element = select2.get(0);
				}
				// System.out.println("element" + element.toString());
				select2 = element.select(".titre");
				for (final Element element2 : select2) {
					final Elements anchors = element2.getElementsByTag("a");
					final Element anchor2 = anchors.get(0);
					// System.out.println("anchor2=" + anchor2.toString());
					final String url = anchor2.attr("href");
					// System.out.println("url=" + url);
					final String title = anchor2.ownText().trim();
					// System.out.println("title='" + title + "'");
					episodes.add(new EpisodeDTO(category, title,
							NRJ12Conf.HOME_URL + url));
				}
			}
			i += 1;
		}
		return episodes;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		// http://www.nrj12.fr/replay-4203/collectionvideo/

		final Set<CategoryDTO> categories = new HashSet<>();

		final String url = NRJ12Conf.HOME_URL + "/replay-4203/collectionvideo/";
		// System.out.println("url=" + url);
		final Document doc = Jsoup
				.parse(getUrlContent(url), NRJ12Conf.ENCODING);
		// To get the main categories ("Divertissments", "Infos / Magazines",
		// etc.):
		// final Elements select = doc.select(".replay");
		int i = 0;
		while (true) {
			final String id = String.format("liste_%d", i);
			// System.out.println("id=" + id);
			final Element element = doc.getElementById(id);
			if (element == null)
				break;
			// System.out.println("element" + element.toString());
			final Element anchor = element.child(0).child(0);
			// System.out.println("anchor" + anchor.toString());
			final String identifier = anchor.attr("title");
			if (identifier != "") {
				// System.out.println("identifier=" + identifier.toString());
				CategoryDTO categoryDTO = new CategoryDTO(NRJ12Conf.NAME,
						identifier, identifier, NRJ12Conf.EXTENSION);
				categoryDTO.setDownloadable(true);
				categories.add(categoryDTO);
			}
			i += 1;
		}
		// Used for other main categories (e.g. "Film/Téléfilm", etc.):
		CategoryDTO otherCategory = new CategoryDTO(NRJ12Conf.NAME, "_other_",
				"_other_", NRJ12Conf.EXTENSION);
		otherCategory.setDownloadable(true);
		categories.add(otherCategory);
		return categories;
	}

	@Override
	public ProcessHolder download(final DownloadParamDTO downloadParam,
			final DownloaderPluginHolder downloaders)
			throws DownloadFailedException {
		final String mediaId = findMediaId(getUrlContent(downloadParam
				.getDownloadInput()));
		return DownloadUtils.download(DownloadParamDTO.buildDownloadParam(
				downloadParam, buildUrlVideoInfo(mediaId)), downloaders);

	}

	private static final Pattern MEDIAID_PATTERN = Pattern
			.compile("/(\\d*)-minipicto");
	private static final Pattern MEDIAID2_PATTERN = Pattern
			.compile("\\?mediaId=(\\d*)&");

	private static String buildUrlVideoInfo(final String mediaId) {
		// Live HTTP headers:
		// -
		// http://95.81.147.19/1UU71eAEJMYNKZIWGHWj23fg_7EEGGNwF_uc=/mogador/web/00126166_h264_12.mp4
		// - Referer:
		// http://www.nrj12.fr/swf/player_video/playerNR12.swf?_=0.8726717974570418
		// captvty subprocess:
		// - curl "http://r.nrj.fr/mogador/web/00148519_h264_12.mp4" -C - -L -g
		// -A "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)"
		// -o "..."
		return NRJ12Conf.REPLAY_URL + "/mogador/web/" + mediaId
				+ "_h264_12.mp4";
	}

	private static String findMediaId(final String content) {
		Matcher matcher = MEDIAID_PATTERN.matcher(content);
		boolean hasMatched = matcher.find();
		String ret = null;
		if (hasMatched) {
			ret = matcher.group(matcher.groupCount());
		} else {
			matcher = MEDIAID2_PATTERN.matcher(content);
			hasMatched = matcher.find();
			ret = null;
			if (hasMatched) {
				ret = matcher.group(matcher.groupCount());
			} else {
				throw new TechnicalException("can't find mediaId");
			}
			return ret;
		}
		return ret;
	}

	@Override
	public DownloadableState canDownload(final String downloadInput) {
		return DownloadableState.IMPOSSIBLE;
	}
}
