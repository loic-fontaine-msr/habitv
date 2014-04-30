package com.dabi.habitv.provider.rss;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.provider.BasePluginProvider;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;

import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RSSPluginManager extends BasePluginProvider {

	private static final int MIN_TITLE_SIZE = 5;

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList;
		try {

			final SyndFeedInput input = new SyndFeedInput();
			final SyndFeed feed = input.build(new XmlReader(getInputStreamFromUrl(category.getId())));
			episodeList = convertFeedToEpisodeList(feed, category);
		} catch (IllegalArgumentException | FeedException | IOException e) {
			throw new TechnicalException(e);
		}
		return episodeList;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categoryList = new HashSet<>();
		final List<String> include = new ArrayList<String>(1);
		include.add("Add include pattern");
		final List<String> exclude = new ArrayList<String>(1);
		exclude.add("Add exclude pattern");
		final CategoryDTO categoryDTO = new CategoryDTO(RSSConf.NAME, "Set RSS Label Here", "Set RSS Url Here", include, exclude, "Set files extension Here");
		categoryDTO.addParameter(RSSConf.DOWNLOADER_PARAM, "Set the downloader here  :aria2c, youtube, rtmpdump (default aria2c)");
		categoryList.add(categoryDTO);
		return categoryList;
	}

	@Override
	public void download(final String downloadOuput, final DownloaderPluginHolder downloaders, final CmdProgressionListener listener, final EpisodeDTO episode)
			throws DownloadFailedException {
		String downloaderName = episode.getCategory().getParameter(RSSConf.DOWNLOADER_PARAM);
		if (downloaderName == null) {
			downloaderName = RSSConf.DOWNLOADER;
		}
		final PluginDownloaderInterface pluginDownloader = downloaders.getPlugin(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());

		pluginDownloader.download(episode.getUrl(), downloadOuput, parameters, listener, getProtocol2proxy());
	}

	@Override
	public String getName() {
		return RSSConf.NAME;
	}

	private static Set<EpisodeDTO> convertFeedToEpisodeList(final SyndFeed feed, final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new HashSet<EpisodeDTO>();
		final List<?> entries = feed.getEntries();
		if (!entries.isEmpty()) {
			for (final Object object : entries) {
				final SyndEntry entry = (SyndEntry) object;
				final List<?> enclosures = entry.getEnclosures();
				String url;
				if (!enclosures.isEmpty()) {
					url = ((SyndEnclosure) enclosures.get(0)).getUrl();
				} else {
					url = entry.getLink();
				}

				String safeTtitle = entry.getTitle().replaceAll("[^\\x00-\\x7F]", "").trim();
				if (safeTtitle.length() <= 5 && entry.getTitle().length() >= MIN_TITLE_SIZE) {
					safeTtitle = entry.getAuthor() + "-" + (new SimpleDateFormat("yyyyMMddHHmmss").format(entry.getPublishedDate()));
				}
				episodeList.add(new EpisodeDTO(category, safeTtitle, url));
			}
		}
		return episodeList;
	}
}
