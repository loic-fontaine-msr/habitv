package com.dabi.habitv.provider.arte;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.api.PluginProviderDownloaderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.utils.DownloadUtils;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class ArtePluginManager extends BasePluginWithProxy implements
		PluginProviderDownloaderInterface { // NO_UCD

	private static final Logger LOG = Logger.getLogger(ArtePluginManager.class);

	@Override
	public String getName() {
		return ArteConf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		if ("search".equals(category.getId())) {
			return searchEpisodeByKeyworkds(category);
		} else {
			try {
				return findEpisodeByCategory(category,
						getInputStreamFromUrl(ArteConf.RSS_CATEGORY_URL
								.replace(ArteConf.ID_EMISSION_TOKEN,
										category.getId())));
			} catch (Exception e) {
				LOG.error(
						"Erreur findEpisodeByCategory utilisation de la recherche",
						e);
				return searchEpisodeByKeyworkds(category);
			}
		}
	}

	private Set<EpisodeDTO> searchEpisodeByKeyworkds(final CategoryDTO category) {
		final String url = "http://videos.arte.tv/fr/do_search/videos/recherche?q="
				+ category.getName().replaceAll(" ", "+");
		final Set<EpisodeDTO> episodes = new HashSet<>();

		final Document doc = Jsoup.parse(getUrlContent(url));

		final Elements select = doc.select(".video");
		for (final Element element : select) {
			try {
				final Element h2 = element.child(1);
				final Element aHref = h2.child(0);
				final String attr = aHref.attr("href");
				final String attrR = attr;
				// final String videoUrl = "http://videos.arte.tv" + attr;
				// final String videoContent = getUrlContent(videoUrl);
				// final Matcher matcher =
				// VIDEO_ID_PATTERN.matcher(videoContent);
				// final boolean hasMatched = matcher.find();
				// String attrR = null;
				// if (hasMatched) {
				// attrR = matcher.group(matcher.groupCount());
				// } else {
				// throw new TechnicalException("can't find mediaId");
				// }
				episodes.add(new EpisodeDTO(category, aHref.text() + "-"
						+ getNbr(attr), attrR));
			} catch (final IndexOutOfBoundsException e) {
				getLog().error(element, e);
				throw new TechnicalException(e);
			}
		}
		return episodes;
	}

	private int getNbr(final String attr) {
		int i = 0;
		if (attr.contains("--") && attr.contains(".")) {
			i = Integer.parseInt(attr.substring(attr.indexOf("--") + 2,
					attr.indexOf(".")));
		}
		return i;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return findCategories(RetrieverUtils.getUrlContent(ArteConf.RSS_PAGE,
				FrameworkConf.UTF8, getHttpProxy()));
	}

	@Override
	public void download(final DownloadParamDTO downloadParam,
			final DownloaderPluginHolder downloaders,
			final CmdProgressionListener listener)
			throws DownloadFailedException {
		final String downloadLink = buildDownloadLink(downloadParam
				.getDownloadInput());
		DownloadUtils.download(DownloadParamDTO.buildDownloadParam(
				downloadParam, downloadLink), downloaders, listener);
	}

	private static final String SEP = "/";

	private static final Pattern VIDEO_REF_FILE_PATTERN = Pattern
			.compile(".*videorefFileUrl = \"(.*)\"");

	private static final Pattern LINK_TITLE_PATTERN = Pattern
			.compile("<a href=\"([^\\,]*),view,rss.xml\" class=\"rss\">([^\\<]*)</a>");

	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMdd");

	private Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO category,
			final InputStream inputStream) {
		final Set<EpisodeDTO> episodeList;
		try {
			final SyndFeedInput input = new SyndFeedInput();
			final SyndFeed feed = input.build(new XmlReader(inputStream, true,
					FrameworkConf.UTF8));
			episodeList = convertFeedToEpisodeList(feed, category);
		} catch (IllegalArgumentException | FeedException | IOException e) {
			throw new TechnicalException(e);
		}
		return episodeList;
	}

	private Set<EpisodeDTO> convertFeedToEpisodeList(final SyndFeed feed,
			final CategoryDTO category) {
		final Set<EpisodeDTO> episodeList = new HashSet<EpisodeDTO>();
		final List<?> entries = feed.getEntries();
		final boolean uniqueTitle = isTitleUnique(entries);
		for (final Object object : entries) {
			final SyndEntry entry = (SyndEntry) object;
			episodeList.add(new EpisodeDTO(category, buildTitle(entry,
					uniqueTitle), entry.getLink()));
		}
		return episodeList;
	}

	private String buildTitle(final SyndEntry entry, final boolean uniqueTitle) {
		return entry.getTitle()
				+ ((uniqueTitle) ? "" : (" " + SIMPLE_DATE_FORMAT.format(entry
						.getPublishedDate())));
	}

	private boolean isTitleUnique(final List<?> entries) {
		final Set<String> titles = new HashSet<>(entries.size());
		for (final Object object : entries) {
			final SyndEntry entry = (SyndEntry) object;
			if (titles.contains(entry.getTitle())) {
				return false;
			}
			titles.add(entry.getTitle());
		}
		return true;
	}

	private Set<CategoryDTO> findCategories(final String urlContent) {
		final Set<CategoryDTO> categoryDTOs = new HashSet<>();
		final Matcher matcher = LINK_TITLE_PATTERN.matcher(urlContent);
		String categoryName;
		String identifier;
		while (matcher.find()) {
			identifier = findShowIdentifier(matcher.group(1));
			categoryName = matcher.group(2);
			categoryDTOs.add(new CategoryDTO(ArteConf.NAME, categoryName,
					identifier, FrameworkConf.UTF8));
		}
		return categoryDTOs;
	}

	private static String findShowIdentifier(final String url) {
		final String[] subUrl = url.split(SEP);
		if (subUrl.length > 2) {
			return subUrl[subUrl.length - 2] + SEP + subUrl[subUrl.length - 1];
		}
		throw new TechnicalException("can't find show identifier");
	}

	private String buildDownloadLink(final String url)
			throws DownloadFailedException {
		final String htmlInfo = getUrlContent(url, FrameworkConf.UTF8);

		final Matcher matcher = VIDEO_REF_FILE_PATTERN.matcher(htmlInfo);
		final String videoRefFileUrl;
		if (matcher.find()) {
			videoRefFileUrl = matcher.group(matcher.groupCount());
		} else {
			throw new DownloadFailedException("can't find json url");
		}
		final String strVideXmlUrl = findStrVideoXmlUrl(videoRefFileUrl);
		return findVideoUrlFromStrVideoXml(strVideXmlUrl);
	}

	private String findStrVideoXmlUrl(final String videoRefFileUrl)
			throws DownloadFailedException {
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		XPathExpression expr;
		try {
			expr = xpath.compile("//videoref/videos/video[@lang='fr']");

			final DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(true);
			final DocumentBuilder builder = domFactory.newDocumentBuilder();
			final org.w3c.dom.Document doc = builder
					.parse(getInputStreamFromUrl(videoRefFileUrl));

			final NodeList nodes = (NodeList) expr.evaluate(doc,
					XPathConstants.NODESET);
			if (nodes.getLength() > 0) {
				return nodes.item(0).getAttributes().getNamedItem("ref")
						.getTextContent();
			}
		} catch (XPathExpressionException | ParserConfigurationException
				| SAXException | IOException e) {
			throw new DownloadFailedException(e);
		}

		return null;
	}

	private String findVideoUrlFromStrVideoXml(final String strVideXmlUrl)
			throws DownloadFailedException {
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		XPathExpression expr;
		try {
			expr = xpath.compile("/video/urls/url[@quality='hd']");

			final DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(true);
			final DocumentBuilder builder = domFactory.newDocumentBuilder();
			final org.w3c.dom.Document doc = builder
					.parse(getInputStreamFromUrl(strVideXmlUrl));

			final NodeList nodes = (NodeList) expr.evaluate(doc,
					XPathConstants.NODESET);
			if (nodes.getLength() > 0) {
				return nodes.item(0).getFirstChild().getNodeValue();
			}
		} catch (XPathExpressionException | ParserConfigurationException
				| SAXException | IOException e) {
			throw new DownloadFailedException(e);
		}

		return null;
	}

	@Override
	public DownloadableState canDownload(final String downloadInput) {
		return downloadInput.startsWith(ArteConf.ARTE_PREFIX_URL) ? DownloadableState.SPECIFIC
				: DownloadableState.IMPOSSIBLE;
	}

}
