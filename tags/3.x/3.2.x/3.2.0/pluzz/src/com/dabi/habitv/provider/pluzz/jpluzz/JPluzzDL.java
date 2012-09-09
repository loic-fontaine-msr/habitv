package com.dabi.habitv.provider.pluzz.jpluzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

/**
 * 
 */

public class JPluzzDL {

	private static final Logger LOGGER = Logger.getLogger(JPluzzDL.class);

	private final Browser browser;
	private final String url;
	private String manifestURL = "";
	private String drm = "";

	private final CmdProgressionListener progressionListener;

	private final String downloadOuput;

	public JPluzzDL(final String url, final String downloadOuput,
			final CmdProgressionListener progressionListener) throws Exception {

		this.url = url;
		this.progressionListener = progressionListener;
		this.downloadOuput = downloadOuput;
		browser = Browser.BrowserSingleton.getBrowser();
		start();
	}

	private void start() throws DownloadFailedException {
		getInfo(getID());

		if ("oui".equals(drm)) {
			throw new DownloadFailedException("La vidéo possede un DRM");
		}
		if ("".equals(manifestURL)) {
			throw new TechnicalException("Pas de lien vers le manifest");
		}
		(new PluzzDLF4M(progressionListener, downloadOuput)).dl(manifestURL);

	}

	private String getID() {
		String id;
		try {
			final String webPage = browser.getFileAsString(url);
			final Pattern pattern = Pattern
					.compile("http://info.francetelevisions.fr/\\?id-video=([^\"]+)");
			final Matcher matcher = pattern.matcher(webPage);
			matcher.find();
			id = matcher.group(1);
			LOGGER.debug("ID de l'Emission : " + id);
			return id;
		} catch (final IOException e) {
			throw new TechnicalException(
					"Impossible de recuperer l'ID de l'Emission");
		}
	}

	private void getInfo(final String id) throws DownloadFailedException {
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		InputStream inputStream = null;
		try {
			final SAXParser saxParser = factory.newSAXParser();
			final String _infos = browser
					.getFileAsString("http://www.pluzz.fr/appftv/webservices/video/getInfosOeuvre.php?mode=zeri&id-diffusion="
							+ id);
			inputStream = new ByteArrayInputStream(_infos.getBytes());

			final Reader reader = new InputStreamReader(inputStream, "UTF-8");

			final InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");

			saxParser.parse(is, new JPluzzDLInfosHandler());

		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new DownloadFailedException(
					"Impossible de parser le fichier XML de l'emission");
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (final IOException e) {
					LOGGER.error("", e);
				}
			}
		}
	}

	private class JPluzzDLInfosHandler extends DefaultHandler {
		boolean isDRM = false;
		boolean isURL = false;

		@Override
		public void startElement(final String uri, final String localName,
				final String qName, final Attributes attributes)
				throws SAXException {
			if ("url".equals(qName)) {
				isURL = true;
			}
			if ("drm".equals(qName)) {
				isDRM = true;
			}
		}

		@Override
		public void endElement(final String uri, final String localName,
				final String qName) throws SAXException {
			if ("url".equals(qName)) {
				isURL = false;
			}
			if ("drm".equals(qName)) {
				isDRM = false;
			}
		}

		@Override
		public void characters(final char ch[], final int start,
				final int length) throws SAXException {
			final String data = new String(ch, start, length);
			if (isURL) {
				if (data.endsWith("f4m")) {
					manifestURL = data;
				}
			} else if (isDRM) {
				drm = data;
			}
		}
	}
}
