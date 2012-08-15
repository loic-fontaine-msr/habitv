package com.dabi.habitv.provider.tf1;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public class TF1Retreiver {

	private static final Logger LOGGER = Logger.getLogger(TF1Retreiver.class);

	public static Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categories = new HashSet<>();

		try {
			final Connection con = Jsoup.connect(TF1Conf.HOME_URL);

			final Elements select = con.get().select(".teaser");
			for (final Element element : select) {
				final String attr = element.child(0).child(0).child(0).attr("onmousedown");
				if (attr.contains("|")) {
					final String key = attr.split("\\|")[3];
					categories.add(new CategoryDTO(TF1Conf.NAME, key, key, TF1Conf.EXTENSION));
				}
			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
		return categories;
	}

	public static Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodes = new HashSet<>();

		try {
			final Connection con = Jsoup.connect(TF1Conf.HOME_URL + "/" + category.getId());

			final Elements select = con.get().select(".teaser");
			for (final Element element : select) {
				try {
					final Element divInfoIntegrale = element.child(1);
					if ("infosTeaser".equals(divInfoIntegrale.attr("class")) || "infosIntegrale".equals(divInfoIntegrale.attr("class"))) {
						final String title = divInfoIntegrale.child(1).child(0).text();
						final String url = divInfoIntegrale.child(1).child(0).attr("href");
						episodes.add(new EpisodeDTO(category, title, url));
					}
				} catch (final IndexOutOfBoundsException e) {
					LOGGER.error(element, e);
					throw new TechnicalException(e);
				}
			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
		return episodes;
	}

	static String buildToken(final String id, final Long timestamp, final String contextRoot) {
		// my $hexdate = sprintf("%x",time());
		// fill up triling zeroes
		final String dateS = String.format("%x", timestamp);
		final StringBuilder dateSC = new StringBuilder(dateS);
		for (int i = 0; i < (dateS.length() - 8); i++) {
			dateSC.append("0");
		}
		// $hexdate .= "0" x (length($hexdate) - 8);
		final String key = "9b673b13fa4682ed14c3cfa5af5310274b514c4133e9b3a81e6e3aba00912564";
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest((key + "/" + contextRoot + "/" + id + dateSC.toString()).getBytes());
		} catch (final NoSuchAlgorithmException e) {
			throw new TechnicalException(e);
		}
		final StringBuilder hashString = new StringBuilder();
		for (int i = 0; i < hash.length; i++) {
			final String hex = Integer.toHexString(hash[i]);
			if (hex.length() == 1) {
				hashString.append('0');
				hashString.append(hex.charAt(hex.length() - 1));
			} else {
				hashString.append(hex.substring(hex.length() - 2));
			}
		}
		return hashString.toString() + "/" + dateS;
	}

	public static String findFinalUrl(final EpisodeDTO episode) {
		final String content = RetrieverUtils.getUrlContent(TF1Conf.HOME_URL + episode.getUrl());
		final boolean hd = content.contains("content=\"720\"");
		// TODO remplacer par appel à
		// http://www.wat.tv/interface/contentv3/8635245
		String contextRoot;
		if (hd) {
			contextRoot = "webhd";
		} else {
			contextRoot = "web";
		}
		final String mediaId = findMediaId(content);
		final String urlForVideo = TF1Conf.WAT_HOME + "/get/" + contextRoot + "/" + mediaId
				+ "?domain=videos.tf1.fr&version=WIN%2010,2,152,32&country=FR&getURL=1&token=" + buildToken(mediaId, findTimeStamp(), contextRoot);
		return RetrieverUtils.getUrlContent(urlForVideo);
	}

	private static Long findTimeStamp() {
		final String content = RetrieverUtils.getUrlContent(TF1Conf.WAT_HOME + "/servertime");
		return Long.valueOf(content.split("\\|")[0]);
	}

	private static String findMediaId(final String content) {
		// compilation de la regex
		final Pattern pattern = Pattern.compile("mediaId : (\\d*),");
		final Matcher matcher = pattern.matcher(content);
		// lancement de la recherche de toutes les occurrences
		final boolean hasMatched = matcher.find();
		String ret = null;
		// si recherche fructueuse
		if (hasMatched) {
			ret = matcher.group(matcher.groupCount());
		}
		return ret;
	}
}
