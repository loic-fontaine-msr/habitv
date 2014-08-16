package com.dabi.habitv.provider.canalplus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.utils.DownloadUtils;
import com.dabi.habitv.framework.plugin.utils.M3U8Utils;

public class CanalUtils {

	public static ProcessHolder doDownload(
			final DownloadParamDTO downloadParam,
			final DownloaderPluginHolder downloaders,
			BasePluginWithProxy basePluginWithProxy, String videoInfoUrl) {
		final String videoUrl;
		if (downloadParam.getDownloadInput().contains("vid=")) {
			final String vid = CanalUtils.getVid(downloadParam);
			videoUrl = CanalUtils.findVideoUrl(vid, basePluginWithProxy,
					videoInfoUrl);
		} else {
			videoUrl = downloadParam.getDownloadInput();
		}
		return DownloadUtils.download(
				DownloadParamDTO.buildDownloadParam(downloadParam, videoUrl),
				downloaders);
	}

	public static String getVid(final DownloadParamDTO downloadParam) {
		final String vid = downloadParam.getDownloadInput().split("vid=")[1]
				.split("&")[0];
		return vid;
	}

	public static String findVideoUrl(final String id,
			BasePluginWithProxy basePluginWithProxy, String videoInfoUrl) {
		try {
			final DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(true); // never forget this!
			final DocumentBuilder builder = domFactory.newDocumentBuilder();
			final Document doc = builder.parse(basePluginWithProxy
					.getInputStreamFromUrl(videoInfoUrl + id));

			final XPathFactory factory = XPathFactory.newInstance();
			final XPath xpath = factory.newXPath();
			final XPathExpression expr = xpath.compile("//VIDEO[ID='" + id
					+ "']/MEDIA/VIDEOS");

			final Object result = expr.evaluate(doc, XPathConstants.NODESET);
			final NodeList nodes = ((NodeList) result).item(0).getChildNodes();
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
		} catch (IOException | XPathExpressionException | SAXException
				| ParserConfigurationException e) {
			throw new TechnicalException(e);
		}
	}

}
