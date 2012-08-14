package com.dabi.habitv.provider.lequipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public class LEquipeRetreiver {

	private LEquipeRetreiver() {

	}

	public static Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList;

		switch (category.getId()) {
		case "Rencontre":
			episodeList = findRencontre(category);
			break;
		case "Résumé":
			episodeList = findResume(category);
			break;

		case "Avant-Match":
			episodeList = findAvanMatch(category);
			break;
		default:
			episodeList = null;
			break;
		}
		return episodeList;
	}

	private static Set<EpisodeDTO> findRencontre(final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new HashSet<>();
		try {
			final Connection con = Jsoup.connect(LEquipeConf.HOME_URL + LEquipeConf.VIDEO_URL);

			final Elements select = con.get().select(".rencontres");
			for (final Element liMatch : select.first().children()) {
				final Element aHref = liMatch.child(0);
				final String name = aHref.child(0).text();
				final String videoUrl = aHref.attr("href");
				episodeList.add(new EpisodeDTO(category, name, videoUrl));
			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
		return episodeList;
	}

	private static Set<EpisodeDTO> findResume(final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new HashSet<>();
		try {
			final Connection con = Jsoup.connect(LEquipeConf.HOME_URL + LEquipeConf.VIDEO_URL);

			final Elements select = con.get().select("#col-gauche");
			final Element divMain = select.first();
			final Element h1 = divMain.child(0);
			final String name = h1.text();
			episodeList.add(new EpisodeDTO(category, name, LEquipeConf.VIDEO_URL));
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
		return episodeList;
	}

	private static Set<EpisodeDTO> findAvanMatch(final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new HashSet<>();
		try {
			final Connection con = Jsoup.connect(LEquipeConf.HOME_URL + LEquipeConf.VIDEO_URL);

			final Elements select = con.get().select(".item");
			for (final Element ulRencontre : select) {
				final Element aHref = ulRencontre.child(0);
				final String name = aHref.child(2).text();
				final String videoUrl = aHref.attr("href");
				episodeList.add(new EpisodeDTO(category, name, videoUrl));
			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
		return episodeList;
	}

	public static String findDownloadlink(final String url) {
		final String htmlContent = RetrieverUtils.getUrlContent(LEquipeConf.HOME_URL + url);
		final Pattern pattern = Pattern.compile("<param name=\"flashVars\" value=\"language_code=fr&amp;playerKey=(.*)&amp;&amp;suffix=&amp;sig=(.*)\">");
		final Matcher matcher = pattern.matcher(htmlContent);
		// lancement de la recherche de toutes les occurrences
		final boolean hasMatched = matcher.find();
		// si recherche fructueuse
		String sig = null;
		String playerKey = null;
		if (hasMatched) {
			playerKey = matcher.group(1);
			sig = matcher.group(2);
		} else {
			throw new TechnicalException("sig and player key not found");
		}
		try {
			return findDownloadlinkBySigAndPlayerKey(sig, playerKey);
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
	}

	private static String findDownloadlinkBySigAndPlayerKey(final String sig, final String playerKey) throws IOException {
		final String url = "http://api.kewego.com/config/getStreamInit/";
		final URLConnection hc = (new URL(url)).openConnection();
		hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1");
		hc.setRequestProperty("X-KDORIGIN", "http%3A//video.lequipe.fr/video/football/foot-l1-video-bande-annonce-saison-2012-2013/%3Fsig%3Db640e3b69ces");
		String data = URLEncoder.encode("player_type", "UTF-8") + "=" + URLEncoder.encode("kp", "UTF-8");
		data += "&" + URLEncoder.encode("sig", "UTF-8") + "=" + URLEncoder.encode(sig, "UTF-8");
		data += "&" + URLEncoder.encode("playerKey", "UTF-8") + "=" + URLEncoder.encode(playerKey, "UTF-8");
		data += "&" + URLEncoder.encode("request_verbose", "UTF-8") + "=" + URLEncoder.encode("false", "UTF-8");
		data += "&" + URLEncoder.encode("language_code", "UTF-8") + "=" + URLEncoder.encode("fr", "UTF-8");
		hc.setDoOutput(true);
		final OutputStreamWriter wr = new OutputStreamWriter(hc.getOutputStream());
		wr.write(data);
		wr.flush();
		// Get the response
		final BufferedReader rd = new BufferedReader(new InputStreamReader(hc.getInputStream()));
		String line;
		final StringBuilder builder = new StringBuilder();
		while ((line = rd.readLine()) != null) {
			builder.append(line);
		}
		wr.close();
		rd.close();

		final Pattern pattern = Pattern.compile("<playerAppToken>(.*)</playerAppToken>");
		final Matcher matcher = pattern.matcher(builder.toString());
		// lancement de la recherche de toutes les occurrences
		final boolean hasMatched = matcher.find();
		// si recherche fructueuse
		String ret = null;
		if (hasMatched) {
			ret = matcher.group(matcher.groupCount());
		}
		return "\"http://api.kewego.com/video/getStream/?appToken=" + ret + "&sig=" + sig + "&format=w640&v=2749\"";
	}
}
