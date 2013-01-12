package com.dabi.habitv.provider.d8;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

class D8Retreiver {

	private D8Retreiver() {

	}

	static Set<EpisodeDTO> findEpisodeByCategory(final ClassLoader classLoader, final CategoryDTO category) {
		final Set<EpisodeDTO> episodes = new HashSet<>();

		try {
			final Connection con = Jsoup.connect(D8Conf.HOME_URL+category.getId());

			final Elements select = con.get().select(".list-programmes-emissions").get(0).children();
			for (final Element liElement : select) {
				try {
					final Element aLink = liElement.child(0);
					final String title = aLink.child(1).child(0).text() + " - " + aLink.child(1).child(1).text();
					final String url = aLink.attr("href").split("vid=")[1].split("&")[0];
					episodes.add(new EpisodeDTO(category, title, url));
				} catch (final IndexOutOfBoundsException e) {
					throw new TechnicalException(e);
				}
			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
		return episodes;
	}

	public static Set<CategoryDTO> findCategories(final ClassLoader classLoader) {
		final Set<CategoryDTO> categories = new HashSet<>();

		try {
			final Connection con = Jsoup.connect(D8Conf.HOME_URL);

			final Elements select = con.get().select("#nav").get(0).child(0).children();
			for (final Element liElement : select) {
				final Element aElement = liElement.child(0);
				final String url = aElement.attr("href");
				final String name = aElement.text();
				final CategoryDTO categoryDTO = new CategoryDTO(D8Conf.NAME, name, url, D8Conf.EXTENSION);
				categoryDTO.addSubCategories(findSubCategories(url));
				categories.add(categoryDTO);

			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}

		return categories;
	}

	private static Collection<CategoryDTO> findSubCategories(final String catUrl) {
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

	public static String findVideoUrl(final String id) {
		try {
			final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true); // never forget this!
			final DocumentBuilder builder = domFactory.newDocumentBuilder();
			final Document doc = builder.parse(RetrieverUtils.getInputStreamFromUrl(D8Conf.VIDEO_INFO_URL + id));

			final XPathFactory factory = XPathFactory.newInstance();
			final XPath xpath = factory.newXPath();
			final XPathExpression expr = xpath.compile("//VIDEO[ID='" + id + "']/MEDIA/VIDEOS");

			final Object result = expr.evaluate(doc, XPathConstants.NODESET);
			final NodeList nodes = ((NodeList) result).item(0).getChildNodes();
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
		} catch (IOException | XPathExpressionException | SAXException | ParserConfigurationException e) {
			throw new TechnicalException(e);
		}
	}
}
