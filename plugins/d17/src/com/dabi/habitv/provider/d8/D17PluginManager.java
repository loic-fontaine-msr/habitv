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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
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
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.utils.DownloadUtils;
import com.dabi.habitv.framework.plugin.utils.M3U8Utils;

public class D17PluginManager extends BasePluginWithProxy implements PluginProviderDownloaderInterface { // NO_UCD

	@Override
	public String getName() {
		return D17Conf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodes = new HashSet<>();
		final Set<String> episodesNames = new HashSet<>();

		final org.jsoup.nodes.Document doc = Jsoup.parse(getUrlContent(D17Conf.HOME_URL + category.getId()));

		final Elements select = doc.select("a.loop-videos");
		for (final Element aVideoElement : select) {
			try {
				final String maintitle = aVideoElement.select("h4").first().text();
				final String subtitle = aVideoElement.select("p").first().text();
				final String title = maintitle + " - " + subtitle;
				final String url = aVideoElement.attr("href").split("vid=")[1].split("&")[0];
				if (!episodesNames.contains(title)) {
					episodes.add(new EpisodeDTO(category, title, url));
					episodesNames.add(title);
				}
			} catch (final IndexOutOfBoundsException e) {
				throw new TechnicalException(e);
			}
		}

		return episodes;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categories = new HashSet<>();

		final org.jsoup.nodes.Document doc = Jsoup.parse(getUrlContent(D17Conf.HOME_URL));

		final Elements select = doc.select(".main-menu").get(0).children();
		for (final Element liElement : select) {
			final Element aElement = liElement.child(0);
			final String url = aElement.attr("href");
			final String name = aElement.text();
			final CategoryDTO categoryDTO = new CategoryDTO(D17Conf.NAME, name, url, D17Conf.EXTENSION);
			categoryDTO.addSubCategories(findSubCategories(url));
			categories.add(categoryDTO);
		}

		return categories;
	}

	@Override
	public void download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders, final CmdProgressionListener listener)
			throws DownloadFailedException {
		final String videoUrl = findVideoUrl(downloadParam.getDownloadInput());
		DownloadUtils.download(DownloadParamDTO.buildDownloadParam(downloadParam, videoUrl), downloaders, listener);
	}

	private Collection<CategoryDTO> findSubCategories(final String catUrl) {
		final Set<CategoryDTO> categories = new HashSet<>();

		final org.jsoup.nodes.Document doc = Jsoup.parse(getUrlContent(getLink(catUrl)));
		final Elements select = doc.select(".block-videos");
		for (final Element divElement : select) {
			final Element link = divElement.child(0).child(0).child(1);
			final String url = link.child(0).attr("href");
			final String name = link.text();
			final CategoryDTO categoryDTO = new CategoryDTO(D17Conf.NAME, name, url, D17Conf.EXTENSION);
			categories.add(categoryDTO);

		}
		return categories;
	}

	private String getLink(final String catUrl) {
		if (catUrl.startsWith("http")) {
			return catUrl;
		} else {
			return D17Conf.HOME_URL + catUrl;
		}
	}

	public String findVideoUrl(final String id) {
		try {
			final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true); // never forget this!
			final DocumentBuilder builder = domFactory.newDocumentBuilder();
			final Document doc = builder.parse(getInputStreamFromUrl(D17Conf.VIDEO_INFO_URL + id));

			final XPathFactory factory = XPathFactory.newInstance();
			final XPath xpath = factory.newXPath();
			final XPathExpression expr = xpath.compile("//VIDEO[ID='" + id + "']/MEDIA/VIDEOS");

			final Object result = expr.evaluate(doc, XPathConstants.NODESET);
			final NodeList nodes = ((NodeList) result).item(0).getChildNodes();
			final Map<String, String> q2url = new HashMap<>();
			for (int i = 0; i < nodes.getLength(); i++) {
				q2url.put(nodes.item(i).getLocalName(), nodes.item(i).getTextContent());
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
		} catch (IOException | XPathExpressionException | SAXException | ParserConfigurationException e) {
			throw new TechnicalException(e);
		}
	}

	@Override
	public DownloadableState canDownload(final String downloadInput) {
		return DownloadableState.IMPOSSIBLE;
	}

}
