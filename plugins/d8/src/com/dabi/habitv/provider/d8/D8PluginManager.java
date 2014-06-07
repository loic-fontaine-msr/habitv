package com.dabi.habitv.provider.d8;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
import com.dabi.habitv.framework.plugin.utils.M3U8Utils;

public class D8PluginManager extends BasePluginWithProxy implements
		PluginProviderDownloaderInterface { // NO_UCD

	@Override
	public String getName() {
		return D8Conf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodes = new LinkedHashSet<>();

		final org.jsoup.nodes.Document doc = Jsoup
				.parse(getUrlContent(getUrl(category)));

		Elements select = doc.select(".list-programmes-emissions");

		if (!select.isEmpty()) {

			final Elements emission = select.get(0).children();
			for (final Element liElement : emission) {
				if (!liElement.children().isEmpty()) {
					final Element aLink = liElement.child(0);
					if (aLink.children().size() > 1
							&& aLink.child(1).children().size() > 0) {
						final String title = aLink.child(1).child(0).text()
								+ " - " + aLink.child(1).child(1).text();
						final String attr = getAttrName(aLink);
						final String url = getVidId(aLink, attr);
						episodes.add(new EpisodeDTO(category, title, url));
					}
				}
			}
		}

		select = doc.select(".block-common ");
		if (!select.isEmpty()) {

			for (final Element block : select) {
				if (block.children().size() > 1) {
					final Elements emission = block.children().get(1)
							.children();
					for (final Element aLink : emission) {
						if (aLink.children().size() > 1) {
							final String title = aLink.child(1).text() + " - "
									+ aLink.child(2).text();
							final String attr = getAttrName(aLink);
							final String url = getVidId(aLink, attr);
							if (url != null) {
								episodes.add(new EpisodeDTO(category, title,
										url));
							}
						}
					}
				}
			}
		}
		return episodes;
	}

	private String getUrl(final CategoryDTO category) {
		return DownloadUtils.isHttpUrl(category.getId()) ? category.getId()
				: (D8Conf.HOME_URL + category.getId());
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categories = new LinkedHashSet<>();

		final org.jsoup.nodes.Document doc = Jsoup
				.parse(getUrlContent(D8Conf.HOME_URL));

		final Elements select = doc.select("#nav").get(0).child(0).children();
		for (final Element liElement : select) {
			final Element aElement = liElement.child(0);
			final String url = aElement.attr("href");
			final String name = aElement.text();
			final CategoryDTO categoryDTO = new CategoryDTO(D8Conf.NAME, name,
					url, D8Conf.EXTENSION);
			categoryDTO.addSubCategories(findSubCategories(url));
			categories.add(categoryDTO);

		}

		return categories;
	}

	@Override
	public ProcessHolder download(final DownloadParamDTO downloadParam,
			final DownloaderPluginHolder downloaders)
			throws DownloadFailedException {
		final String videoUrl = findVideoUrl(downloadParam.getDownloadInput());
		return DownloadUtils.download(
				DownloadParamDTO.buildDownloadParam(downloadParam, videoUrl),
				downloaders);
	}

	private static String getVidId(final Element aLink, final String attr) {
		final String attrValue = aLink.attr(attr);
		return attrValue.contains("vid=") ? attrValue.split("vid=")[1]
				.split("&")[0] : null;
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
		final Set<CategoryDTO> categories = new LinkedHashSet<>();
		final org.jsoup.nodes.Document doc = Jsoup.parse(getFullUrl(catUrl));
		final Elements tpGrid = doc.select(".tp-grid");
		if (!tpGrid.isEmpty()) {
			final Elements select = tpGrid.get(0).children();
			for (final Element divElement : select) {
				for (final Element subDivElement : divElement.children()) {
					if (subDivElement.children().size() > 0) {
						final Element aElement = subDivElement.child(0);
						final String url = aElement.attr("href");
						final String name = aElement.child(1).text();
						final CategoryDTO categoryDTO = new CategoryDTO(
								D8Conf.NAME, name, url, D8Conf.EXTENSION);
						categoryDTO.setDownloadable(true);
						categories.add(categoryDTO);
					}
				}
			}
		}
		return categories;
	}

	private String getFullUrl(final String catUrl) {
		return catUrl.startsWith("http") ? catUrl
				: getUrlContent(D8Conf.HOME_URL + catUrl);
	}

	private String findVideoUrl(final String id) throws DownloadFailedException {
		try {
			final DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(true); // never forget this!
			final DocumentBuilder builder = domFactory.newDocumentBuilder();
			final Document doc = builder
					.parse(getInputStreamFromUrl(D8Conf.VIDEO_INFO_URL + id));

			final XPathFactory factory = XPathFactory.newInstance();
			final XPath xpath = factory.newXPath();
			final XPathExpression expr = xpath.compile("//VIDEO[ID='" + id
					+ "']/MEDIA/VIDEOS");

			final Object result = expr.evaluate(doc, XPathConstants.NODESET);
			final NodeList nodeList = (NodeList) result;
			if (nodeList.getLength() > 0) {
				final NodeList nodes = nodeList.item(0).getChildNodes();
				final Map<String, String> q2url = new HashMap<>();
				for (int i = 0; i < nodes.getLength(); i++) {
					q2url.put(nodes.item(i).getLocalName(), nodes.item(i)
							.getTextContent());
				}

				String videoUrl = q2url.get("HLS");
				if (videoUrl == null) {
					videoUrl = q2url.get("HD");
				} else {
					videoUrl = M3U8Utils.keepBestQuality(videoUrl);
				}
				if (videoUrl == null) {
					videoUrl = q2url.get("HAUT_DEBIT");
				}
				if (videoUrl == null) {
					videoUrl = q2url.get("BAS_DEBIT");
				}
				return videoUrl;
			}
		} catch (IOException | XPathExpressionException | SAXException
				| ParserConfigurationException e) {
			throw new TechnicalException(e);
		}
		return null;
	}

	@Override
	public DownloadableState canDownload(final String downloadInput) {
		return DownloadableState.IMPOSSIBLE;
	}

}
