package com.dabi.habitv.provider.tvSubtitles;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;

public final class TvSubtitlesRetriever {

	private TvSubtitlesRetriever() {

	}

	// private class RatedEpisode implements Comparable<RatedEpisode>{
	// EpisodeDTO episode;
	// int rate;
	// public RatedEpisode(EpisodeDTO episode, int rate) {
	// super();
	// this.episode = episode;
	// this.rate = rate;
	// }
	//
	// }

	public static Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new HashSet<>();
		try {
			Source source = new Source(new URL(TvSubtitlesConf.HOME_URL + "/" + category.getId()));
			for (Segment segment : source.getAllStartTags("tr align=\"middle\" bgcolor=\"#ffffff\"")) {
				if (segment.getChildElements().size() > 0 && segment.getChildElements().get(0).getChildElements().size() > 0) {
					// String id =
					// segment.getChildElements().get(0).getChildElements().get(0).getContent().toString();
					// String name =
					// segment.getChildElements().get(0).getChildElements().get(1).getChildElements().get(0).getChildElements().get(0).getContent()
					// .toString();
					String url = segment.getChildElements().get(0).getChildElements().get(1).getChildElements().get(0).getAttributeValue("href");
					Source source2 = new Source(new URL(TvSubtitlesConf.HOME_URL + "/" + url));
					Integer previousRate = null;
					Map<String, Integer> nameToRate = new HashMap<>();
					Map<String, EpisodeDTO> nameToEpisode = new HashMap<>();
					if (source2.getAllStartTags("div title").size() > 0) {
						for (Segment segment2 : source2.getAllStartTags("a href=")) {
							if (segment2.getChildElements().size() > 0 && segment2.getChildElements().get(0).getChildElements().size() > 0
									&& segment2.getChildElements().get(0).getAllStartTags("div title").size() > 0) {
								String language = segment2.getChildElements().get(0).getChildElements().get(0).getAttributeValue("title");
								String epName = segment2.getChildElements().get(0).getChildElements().get(0).getChildElements().get(1).getContent().toString();
								epName = epName.substring(epName.lastIndexOf(">") + 1, epName.length());
								if (language != null && (language.contains(TvSubtitlesConf.LANGUAGE) || language.contains(TvSubtitlesConf.LANGUAGE2))) {
									String dlUrl = segment2.getChildElements().get(0).getAttributeValue("href");
									Source source3 = new Source(new URL(TvSubtitlesConf.HOME_URL + dlUrl));

									int rate = 0;
									for (Segment segment33 : source3.getAllStartTags("b id=\"love\"")) {
										String valRate = segment33.getChildElements().get(0).getContent().toString();
										try {
											rate = Integer.valueOf(valRate);
										} catch (NumberFormatException e) {
											// nothing
										}

									}

									for (Segment segment3 : source3.getAllStartTags("a href=")) {
										if (segment3.getChildElements().size() > 0
												&& segment3.getChildElements().get(0).getAllStartTags("img src").size() > 0
												&& segment3.getChildElements().get(0).getAllStartTags("img src").get(0).getAttributeValue("title") != null
												&& segment3.getChildElements().get(0).getAllStartTags("img src").get(0).getAttributeValue("title")
														.equals("Download")) {
											String downloadUrl = segment3.getChildElements().get(0).getAttributeValue("href");

											// String epName = id + " - " +
											// name;
											previousRate = nameToRate.get(epName);

											if (previousRate == null || rate > previousRate) {
												nameToEpisode.put(epName, new EpisodeDTO(category, epName, TvSubtitlesConf.HOME_URL + "/" + downloadUrl));
												nameToRate.put(epName, rate);
											}
											previousRate = rate;
										}
									}
								}
							}
						}
					}
					episodeList.addAll(nameToEpisode.values());
				}
			}
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
		return episodeList;
	}
}
