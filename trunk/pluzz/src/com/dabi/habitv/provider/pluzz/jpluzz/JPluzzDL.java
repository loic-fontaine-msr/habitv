package com.dabi.habitv.provider.pluzz.jpluzz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

/**
 * 
 * @author bigk
 * @version 0.8.5
 * 
 * 
 *          Copyright (C) 2012 bigktheone@gmail.com
 * 
 *          This program is free software; you can redistribute it and/or modify
 *          it under the terms of the GNU General Public License as published by
 *          the Free Software Foundation; either version 2 of the License, or
 *          (at your option) any later version.
 * 
 *          This program is distributed in the hope that it will be useful, but
 *          WITHOUT ANY WARRANTY; without even the implied warranty of
 *          MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *          General Public License for more details.
 * 
 *          You should have received a copy of the GNU General Public License
 *          along with this program; if not, write to the Free Software
 *          Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 *          02110-1301, USA
 */

public class JPluzzDL {

	private volatile Logger m_logger;

	public final static int BUFFER_LEN = 1024 * 1024;

	private String m_id;
	private final Browser m_browser;
	private final String m_url;
	private String m_mmsLink = "";
	private String m_rtmpLink = "";
	private String m3u8URL = "";
	private String m_manifestURL = "";
	private String m_drm = "";

	private final CmdProgressionListener progressionListener;

	private final String downloadOuput;

	public JPluzzDL(final String url, final String downloadOuput,
			final CmdProgressionListener progressionListener) throws Exception {

		m_url = url;
		this.progressionListener = progressionListener;
		this.downloadOuput = downloadOuput;
		m_logger = Logger.getLogger(JPluzzDL.class.getName());
		m_browser = Browser.BrowserSingleton.getBrowser();
		start();
	}

	private boolean isPluzzUrl() {
		final Pattern _p = Pattern
				.compile("http://www.pluzz.fr/[^\\.]+?\\.html");
		final Matcher _m = _p.matcher(m_url);
		return _m.find();
	}

	private void start() throws IOException {
		if (isPluzzUrl()) {

			getID();

			getInfo();

			if ("oui".equals(m_drm)) {
				m_logger.log(Level.WARNING,
						"La vidéo possède un DRM ; elle sera sans doute illisible");
			}
			if (!"".equals(m_mmsLink)) {
				m_logger.log(
						Level.INFO,
						"MMS Link : "
								+ m_mmsLink
								+ "\n"
								+ "Lien MMS : %s\nUtiliser par exemple mimms ou msdl pour la recuperer directement ou l'option -f de pluzzdl pour essayer de la charger via ses fragments");
			}
			if (!"".equals(m3u8URL)) {
				m_logger.log(Level.INFO, "m3u8URL : " + m3u8URL);
			}
			if (!"".equals(m_rtmpLink)) {
				m_logger.log(
						Level.INFO,
						"RTMP Link : "
								+ m_rtmpLink
								+ "\n"
								+ "Lien RTMP : %s\nUtiliser par exemple rtmpdump pour la recuperer directement ou l'option -f de pluzzdl pour essayer de la charger via ses fragments");
			}
			if ("".equals(m_manifestURL)) {
				m_logger.log(Level.SEVERE, "Pas de lien vers le manifest");
				throw new TechnicalException("Pas de lien vers le manifest");
			}
		} else {
			String _page = "";
			try {
				_page = m_browser.getFileAsString(m_url);

			} catch (final IOException e) {
				m_logger.log(Level.SEVERE, "bad url");
				throw new TechnicalException("bad url");
			}

			final Pattern _p = Pattern.compile("(http://.+?manifest.f4m)");
			final Matcher _m = _p.matcher(_page);
			_m.find();
			try {
				m_manifestURL = _m.group(1);
			} catch (final Exception _e) {
				m_logger.log(Level.SEVERE, "Pas de lien vers le manifest");
				throw new TechnicalException("Pas de lien vers le manifest");
			}
		}
		try {
			(new PluzzDLF4M(progressionListener, downloadOuput))
					.dl(m_manifestURL);
		} catch (Exception e) {
			throw new TechnicalException(e);
		}

	}

	private void getID() {
		try {
			final String _webPage = m_browser.getFileAsString(m_url);
			final Pattern _p = Pattern
					.compile("http://info.francetelevisions.fr/\\?id-video=([^\"]+)");
			final Matcher _m = _p.matcher(_webPage);
			_m.find();
			m_id = _m.group(1);
			m_logger.log(Level.FINEST, "ID de l'émission : " + m_id);
		} catch (final IOException e) {
			m_logger.log(Level.SEVERE,
					"Impossible de récupérer l'ID de l'émission");
			throw new TechnicalException(
					"Impossible de récupérer l'ID de l'émission");
		}
	}

	private void getInfo() {
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		ByteArrayInputStream _b = null;
		try {
			final SAXParser saxParser = factory.newSAXParser();
			final String _infos = m_browser
					.getFileAsString("http://www.pluzz.fr/appftv/webservices/video/getInfosOeuvre.php?mode=zeri&id-diffusion="
							+ m_id);
			_b = new ByteArrayInputStream(_infos.getBytes());

			final Reader reader = new InputStreamReader(_b, "UTF-8");

			final InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");

			saxParser.parse(is, new JPluzzDLInfosHandler());

		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new TechnicalException(
					"Impossible de parser le fichier XML de l'émission");
		} finally {
			if (_b != null) {
				try {
					_b.close();
				} catch (IOException e) {
					m_logger.log(Level.SEVERE, "", e);
				}
			}
		}

		m_logger.log(Level.FINEST, "MMS Link : " + m_mmsLink);
		m_logger.log(Level.FINEST, "RTMP Link : " + m_rtmpLink);
		m_logger.log(Level.FINEST, "URL of the manifest : " + m_manifestURL);
		m_logger.log(Level.FINEST, "MMS Link : " + m_mmsLink);
		m_logger.log(Level.FINEST, "Use of DRM : " + m_drm);
	}

	public static int toInt(final byte[] bytes, final int offset) {
		int ret = 0;
		for (int i = 0; i < 4 && i + offset < bytes.length; i++) {
			ret <<= 8;
			ret |= bytes[i] & 0xFF;
		}
		return ret;
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
			final String _data = new String(ch, start, length);
			if (isURL) {
				if (_data.startsWith("mms")) {
					m_mmsLink = _data;
				}
				if (_data.startsWith("rtmp")) {
					m_rtmpLink = _data;
				}
				if (_data.endsWith("f4m")) {
					m_manifestURL = _data;
				}
				if (_data.endsWith("m3u8")) {
					m3u8URL = _data;
				}
			} else if (isDRM) {
				m_drm = _data;
			}
		}
	}

	public int handleProgression(final int nbMax, final int indice,
			final int old) {
		final float f = (float) indice / (float) nbMax;
		return Math.min((int) (f * 100), 100);
	}
}
