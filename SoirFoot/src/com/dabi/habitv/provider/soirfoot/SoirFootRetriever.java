package com.dabi.habitv.provider.soirfoot;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;

public final class SoirFootRetriever {

	private SoirFootRetriever() {

	}

	public static Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new HashSet<>();
		try {
			Source source = new Source(new URL(SoirFootConf.HOME_URL + "/" + category.getId()));
			for (Segment segment : source.getAllStartTags("div class=\"video_i\"")) {
				try {
					Element segmentA = segment.getChildElements().get(0).getChildElements().get(0);
					String url = segmentA.getAttributeValue("href");
					Source source2 = new Source(new URL(url));

					String name = source2.getAllStartTags("h2 class=\"h2_artist\"").get(0).getElement().getContent().toString();
					name = name.replaceAll("(\\d\\s*-\\s*\\d)", "").replaceAll("(\\d_*-_*\\d)", "");

					// justin
					for (Segment segment2 : source2.getAllStartTags("param name=\"flashvars\"")) {
						String param = segment2.getChildElements().get(0).getAttributeValue("value");
						if (param.contains("archive_id")) {
							String archiveId = param.split("&")[0].split("=")[1];
							String xmlUrl = SoirFootConf.JUSTIN_API_URL + archiveId + ".xml?onsite=true";
							Source source3 = new Source(new URL(xmlUrl));
							for (Segment segment3 : source3.getAllStartTags("video_file_url")) {
								String flvUrl = segment3.getChildElements().get(0).getContent().toString();
								episodeList.add(new EpisodeDTO(category.getName(), name, flvUrl));
							}
						}
					}

					// rutube
//					for (Segment segment2 : source2.getAllStartTags("embed src=\"")) {
//						String param = segment2.getChildElements().get(0).getAttributeValue("src");
//						if (param.contains("/")) {
//							String archiveId = param.substring(param.lastIndexOf("/") + 1, param.length());
//							String xmlUrl = SoirFootConf.RUTUBE_API_URL.replaceFirst("#ID#", archiveId);
//							Source source3 = new Source(new URL(xmlUrl));
//							String baseUrl = source3.getAllStartTags("baseurl").get(0).getElement().getContent().toString();
//							String rtmpDumpUrl = source3.getAllStartTags("media").get(0).getAttributeValue("url");
//							episodeList.add(new EpisodeDTO(category.getName(), name, baseUrl + rtmpDumpUrl));
//						}
//					}
				} catch (Exception e) {
					e.printStackTrace();
					// FIXME
				}
			}
		} catch (IOException e) {
			throw new TechnicalException(e);// FIXME throw functionnal
											// checkFailedException
		}
		return episodeList;
	}

}
