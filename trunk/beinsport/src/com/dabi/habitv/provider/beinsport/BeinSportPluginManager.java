package com.dabi.habitv.provider.beinsport;

import java.io.IOException;
import java.util.ArrayList;
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
import com.dabi.habitv.framework.plugin.api.dto.ProxyDTO;
import com.dabi.habitv.framework.plugin.api.provider.BasePluginProvider;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.utils.SoccerUtils;
import com.dabi.habitv.provider.beinsport.BeinSportConfCst.BeinSportConf;

public class BeinSportPluginManager extends BasePluginProvider { // NO_UCD

	@Override
	public String getName() {
		return BeinSportConf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		switch (category.getId()) {
		case BeinSportConf.VIDEOS_CATEGORY:
			return findEpisodeByCategory(category, BeinSportConf.VIDEOS_URL);
		case BeinSportConf.REPLAY_CATEGORY:
			final Set<EpisodeDTO> episodeDTOs = new HashSet<>();
			for (final CategoryDTO subCategory : findReplaycategories()) {
				episodeDTOs.addAll(findEpisodeByCategory(subCategory, BeinSportConf.HOME_URL + subCategory.getId()));
			}
			return episodeDTOs;
		default:
			return findEpisodeByCategory(category, BeinSportConf.HOME_URL + category.getId());
		}
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categoryDTOs = new HashSet<>();
		categoryDTOs.add(new CategoryDTO(BeinSportConf.VIDEOS_CATEGORY, BeinSportConf.VIDEOS_CATEGORY, BeinSportConf.VIDEOS_CATEGORY, BeinSportConf.EXTENSION));
		final CategoryDTO replayCategory = new CategoryDTO(BeinSportConf.REPLAY_CATEGORY, BeinSportConf.REPLAY_CATEGORY, BeinSportConf.REPLAY_CATEGORY,
				BeinSportConf.EXTENSION);
		replayCategory.addSubCategories(findReplaycategories());
		categoryDTOs.add(replayCategory);
		return categoryDTOs;
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener,
			final EpisodeDTO episode) throws DownloadFailedException, NoSuchDownloaderException {
		if (episode.getUrl().endsWith(".mp4")) {
			curlDownload(downloadOuput, downloaders, cmdProgressionListener, episode, getProtocol2proxy());
		} else {
			rtmpDumpDownload(downloadOuput, downloaders, cmdProgressionListener, episode, getProtocol2proxy());
		}
	}

	private void rtmpDumpDownload(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener listener, final EpisodeDTO episode,
			final Map<ProxyDTO.ProtocolEnum, ProxyDTO> proxies) throws DownloadFailedException, NoSuchDownloaderException {
		final String downloaderName = BeinSportConf.RTMDUMP;
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);
		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));

		final String finalVideoUrl = findFinalRtmpUrl(episode.getUrl());
		final String[] tab = finalVideoUrl.split("/");
		final String contextRoot = tab[3];
		final String rtmpdumpCmd = BeinSportConf.RTMPDUMP_CMD2.replace("#PROTOCOL#", tab[0]).replace("#HOST#", tab[2])
				.replaceAll("#CONTEXT_ROOT#", contextRoot);
		final String relativeUrl = finalVideoUrl.substring(finalVideoUrl.indexOf("/" + contextRoot + "/") + 1);

		parameters.put(FrameworkConf.PARAMETER_ARGS, rtmpdumpCmd);
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());
		pluginDownloader.download(relativeUrl, downloadOuput, parameters, listener, proxies);
	}

	private void curlDownload(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener,
			final EpisodeDTO episode, final Map<ProxyDTO.ProtocolEnum, ProxyDTO> map) throws NoSuchDownloaderException, DownloadFailedException {
		final String downloaderName = BeinSportConf.CURL;
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());

		pluginDownloader.download(episode.getUrl(), downloadOuput, parameters, cmdProgressionListener, map);
	}

	private static final Pattern VIDEOID_PATTERN = Pattern.compile(".*videoId\\s+=\\s+\\\"(.*)\\\";.*");

	private Set<EpisodeDTO> findEpisodeByCategory(final CategoryDTO category, final String url) {
		final Set<EpisodeDTO> episodeList = new HashSet<>();

		final org.jsoup.nodes.Document doc = Jsoup.parse(getUrlContent(url));

		Elements divTabContainer = doc.select("#tabContainer");
		if (divTabContainer.isEmpty()){
			divTabContainer = doc.select("#newsIndex");
		}

		for (final Element article : divTabContainer.select("article")) {
			Element aHref = article.child(0);
			if (!"a".equals(aHref.tagName())){
				aHref = article.child(0).child(0);
			}
			final Element h4 = article.child(1).child(0);
			final String href = aHref.attr("href");
			String name = h4.text();
			name = SoccerUtils.maskScore(name);
			episodeList.add(new EpisodeDTO(category, name, href));
		}

		return episodeList;
	}

	private Collection<CategoryDTO> findReplaycategories() {
		final Set<CategoryDTO> categories = new HashSet<>();

		final org.jsoup.nodes.Document doc = Jsoup.parse(getUrlContent(BeinSportConf.REPLAY_URL));

		final Elements divTabContainer = doc.select("#tabContainer");
		for (final Element h2 : divTabContainer.select("h2")) {
			final Element aHref = h2.child(0);
			final String href = aHref.attr("href");
			final String title = aHref.text();
			categories.add(new CategoryDTO(BeinSportConf.NAME, title, href, BeinSportConf.EXTENSION));
		}
		return categories;
	}

	private String findFinalRtmpUrl(final String url) {
		final String content = getUrlContent(BeinSportConf.HOME_URL + url);
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

	private ArrayList<String> findUrlList(final String clipId) throws ParserConfigurationException, XPathExpressionException, SAXException, IOException {
		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xpath = factory.newXPath();
		final XPathExpression expr = xpath.compile("//file");

		final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		final DocumentBuilder builder = domFactory.newDocumentBuilder();
		final Document doc = builder.parse(getInputStreamFromUrl(BeinSportConf.XML_INFO + clipId));

		final NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
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
