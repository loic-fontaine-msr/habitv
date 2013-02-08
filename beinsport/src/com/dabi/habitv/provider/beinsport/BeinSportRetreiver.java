package com.dabi.habitv.provider.beinsport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
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

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.framework.plugin.utils.SoccerUtils;

class BeinSportRetreiver {

	private static final Pattern VIDEOID_PATTERN = Pattern.compile(".*videoId\\s+=\\s+\\\"(.*)\\\";.*");

	private BeinSportRetreiver() {

	}

	static Set<EpisodeDTO> findEpisodeByCategory(final ClassLoader classLoader, final CategoryDTO category, final InputStream inputStream) {
		final Data data = (Data) RetrieverUtils.unmarshalInputStream(inputStream, BeinSportConf.PACKAGE_NAME, classLoader);

		final List<Video> videos = data.getResults().getResultList().getVideo();
		final Set<EpisodeDTO> episodeList = new HashSet<>();
		for (final Video video : videos) {
			final List<File> fileList = video.getVideoFiles().getFile();
			final String name = SoccerUtils.maskScore(video.getDescription());
			episodeList.add(new EpisodeDTO(category, name, fileList.get(0).getValue()));
		}

		return episodeList;
	}

	public static Collection<CategoryDTO> findReplaycategories() {
		final Set<CategoryDTO> categories = new HashSet<>();

		try {
			final Connection con = Jsoup.connect(BeinSportConf.REPLAY_URL);

			final Elements divTabContainer = con.get().select("#tabContainer");
			for (final Element h2 : divTabContainer.select("h2")) {
				final Element aHref = h2.child(0);
				final String href = aHref.attr("href");
				final String title = aHref.text();
				categories.add(new CategoryDTO(BeinSportConf.NAME, title, href, BeinSportConf.EXTENSION));
			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
		return categories;
	}

	public static Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO category, final InputStream inputStreamFromUrl) {
		final Set<EpisodeDTO> episodeList = new HashSet<>();
		try {
			final Connection con = Jsoup.connect(BeinSportConf.REPLAY_URL);

			final Elements divTabContainer = con.get().select("#tabContainer");
			for (final Element article : divTabContainer.select("article")) {
				final Element aHref = article.child(0);
				final Element div = article.child(1);
				final String href = aHref.attr("href");
				final String title = div.child(0).child(0).text();
				episodeList.add(new EpisodeDTO(category, title, href));
			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
		return episodeList;
	}

	public static String findFinalRtmpUrl(final String url) {
		final String content = RetrieverUtils.getUrlContent(BeinSportConf.HOME_URL + url);
		final String clipId = findMediaId(content);
		ArrayList<String> urlList;
		try {
			urlList = findUrlList(clipId);
		} catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
			throw new TechnicalException(e);
		}
		if (!urlList.isEmpty()) {
			return urlList.get(urlList.size() - 1);
		} else {
			throw new TechnicalException("No link found");
		}
	}

	private static ArrayList<String> findUrlList(final String clipId) throws ParserConfigurationException, XPathExpressionException, SAXException, IOException {
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		final XPathExpression expr = xpath.compile("//file");

		final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		final DocumentBuilder builder = domFactory.newDocumentBuilder();
		final String content = RetrieverUtils.getUrlContent(BeinSportConf.XML_INFO + clipId);
		final Document doc = builder.parse(new ByteArrayInputStream(content.substring(content.indexOf("<?xml")).getBytes()));

		final NodeList nodes = (NodeList)  expr.evaluate(doc, XPathConstants.NODESET);
		final ArrayList<String> urlList = new ArrayList<>();
		for (int i = 0; i < nodes.getLength(); i++) {
			urlList.add(nodes.item(i).getAttributes().getNamedItem("externalPath").getTextContent());
		}

		return urlList;
	}

	private static String findMediaId(final String content) {

		final Matcher matcher = VIDEOID_PATTERN.matcher(content);
		final boolean hasMatched = matcher.find();
		String ret = null;
		if (hasMatched) {
			ret = matcher.group(matcher.groupCount());
		} else {
			throw new TechnicalException("can't find mediaId");
		}
		return ret;
	}
}

