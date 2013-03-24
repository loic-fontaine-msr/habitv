package com.dabi.habitv.provider.d8;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.provider.BasePluginProvider;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class D8PluginManager extends BasePluginProvider { // NO_UCD

	@Override
	public String getName() {
		return D8Conf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodes = new HashSet<>();

		final org.jsoup.nodes.Document doc = Jsoup.parse(getUrlContent(D8Conf.HOME_URL + category.getId()));

		Elements select = doc.select(".list-programmes-emissions");

		if (!select.isEmpty()) {

			final Elements emission = select.get(0).children();
			for (final Element liElement : emission) {
				final Element aLink = liElement.child(0);
				final String title = aLink.child(1).child(0).text() + " - " + aLink.child(1).child(1).text();
				final String attr = getAttrName(aLink);
				final String url = getVidId(aLink, attr);
				episodes.add(new EpisodeDTO(category, title, url));
			}
		} else {
			final String domain = findMatch(doc.html(), DOMAIN_PATTERN);
			select = doc.select("a");
			for (final Element aLink : select) {
				final String attr = getAttrName(aLink);
				final String title = getTitle(aLink).trim();
				String url = aLink.attr(attr);
				if (url.contains("vid=")) {
					url = getVidId(aLink, attr);
					episodes.add(new EpisodeDTO(category, title, url));
				} else if (url.contains("epi_id=")) {
					final String epContent = getUrlContent(domain + url);
					final String ret = findMatch(epContent, VID_PATTERN);
					episodes.add(new EpisodeDTO(category, title, ret));
				}
			}
		}
		return episodes;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categories = new HashSet<>();

		final org.jsoup.nodes.Document doc = Jsoup.parse(getUrlContent(D8Conf.HOME_URL));

		final Elements select = doc.select("#nav").get(0).child(0).children();
		for (final Element liElement : select) {
			final Element aElement = liElement.child(0);
			final String url = aElement.attr("href");
			final String name = aElement.text();
			final CategoryDTO categoryDTO = new CategoryDTO(D8Conf.NAME, name, url, D8Conf.EXTENSION);
			categoryDTO.addSubCategories(findSubCategories(url));
			categories.add(categoryDTO);

		}

		return categories;
	}

	private String getDownloader(final String url) {
		String downloaderName;
		if (url.startsWith(D8Conf.RTMPDUMP_PREFIX)) {
			downloaderName = D8Conf.RTMDUMP;
		} else {
			downloaderName = D8Conf.CURL;
		}
		return downloaderName;
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener listener, final EpisodeDTO episode)
			throws DownloadFailedException, NoSuchDownloaderException {
		final String videoUrl = findVideoUrl(episode.getUrl());
		final String downloaderName = getDownloader(videoUrl);
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());

		pluginDownloader.download(videoUrl, downloadOuput, parameters, listener, getProtocol2proxy());
	}

	private static final Pattern VID_PATTERN = Pattern.compile(".*<canal:player.*videoId=\"(\\d+)\".*");
	private static final Pattern DOMAIN_PATTERN = Pattern.compile("var sDocumentHttp = \'(.*)\';");

	private static String findMatch(final String epContent, final Pattern pattern) {
		final Matcher matcher = pattern.matcher(epContent);
		final boolean hasMatched = matcher.find();
		String ret = null;
		if (hasMatched) {
			ret = matcher.group(matcher.groupCount());
		}
		return ret;
	}

	private static String getVidId(final Element aLink, final String attr) {
		return aLink.attr(attr).split("vid=")[1].split("&")[0];
	}

	private static String getTitle(final Element aLink) {
		final Element lastE = aLink;
		if (!lastE.children().isEmpty()) {
			final StringBuilder title = new StringBuilder();
			for (final Element child : lastE.children()) {
				title.append(" " + getTitle(child));
			}
			return title.toString();
		} else {
			return aLink.text();
		}
	}

	private static String getAttrName(final Element aLink) {
		String attr;
		if (aLink.hasAttr("href")) {
			attr = "href";
		} else {
			attr = "data-href";
		}
		return attr;
	}

	private Collection<CategoryDTO> findSubCategories(final String catUrl) {
		final Set<CategoryDTO> categories = new HashSet<>();

		try {
			final Connection con = Jsoup.connect(D8Conf.HOME_URL + catUrl);
			final Elements select = con.get().select(".tp-grid").get(0).children();
			for (final Element divElement : select) {
				for (final Element subDivElement : divElement.children()) {
					final Element aElement = subDivElement.child(0);
					final String url = aElement.attr("href");
					final String name = aElement.child(1).text();
					final CategoryDTO categoryDTO = new CategoryDTO(D8Conf.NAME, name, url, D8Conf.EXTENSION);
					categories.add(categoryDTO);
				}

			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}

		return categories;
	}

	private String findVideoUrl(final String id) throws DownloadFailedException {
		try {
			final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true); // never forget this!
			final DocumentBuilder builder = domFactory.newDocumentBuilder();
			final Document doc = builder.parse(getInputStreamFromUrl(D8Conf.VIDEO_INFO_URL + id));

			final XPathFactory factory = XPathFactory.newInstance();
			final XPath xpath = factory.newXPath();
			final XPathExpression expr = xpath.compile("//VIDEO[ID='" + id + "']/MEDIA/VIDEOS");

			final Object result = expr.evaluate(doc, XPathConstants.NODESET);
			final NodeList nodeList = (NodeList) result;
			if (nodeList.getLength() > 0) {
				final NodeList nodes = nodeList.item(0).getChildNodes();
				final Map<String, String> q2url = new HashMap<>();
				for (int i = 0; i < nodes.getLength(); i++) {
					q2url.put(nodes.item(i).getLocalName(), nodes.item(i).getTextContent());
				}

				String videoUrl = q2url.get("HD");
				if (videoUrl == null) {
					videoUrl = q2url.get("HAUT_DEBIT");
				}
				if (videoUrl == null) {
					videoUrl = q2url.get("BAS_DEBIT");
				}
				return videoUrl;
			}
		} catch (IOException | XPathExpressionException | SAXException | ParserConfigurationException e) {
			throw new TechnicalException(e);
		}
		return null;
	}
}
