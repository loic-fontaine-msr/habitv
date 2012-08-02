package com.dabi.habitv.provider.pluzz.jpluzz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.InflaterInputStream;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
	private String m_manifestURL = "";
	private String m_drm = "";
	private float m_duration;
	private String m_pv2 = "";
	private String m_urlFrag = "";
	private byte[] m_flvHeader;
	private int m_bitrate;
	private final boolean m_useFragments;
	private String m_manifestURLToken = "";
	private String m_manifest = "";
	private String m_hdnea = "";
	private String m_pv20 = "";
	private String m_hdntl = "";
	private String m_pvTokenData = "";
	private String m_pvToken = "";
	private int m_firstFragment;
	private int m_maxNumOfFrag = 0;
	private FileOutputStream m_videoFileOutputStream;
	private int m_currentFragment = 1;

	private final CmdProgressionListener progressionListener;

	private final String downloadOuput;

	public JPluzzDL(final String url, final String downloadOuput, final boolean useFragments, final String proxy,
			final CmdProgressionListener progressionListener) throws Exception {

		m_url = url;
		this.progressionListener = progressionListener;
		this.downloadOuput = downloadOuput;
		m_useFragments = useFragments;
		m_logger = Logger.getLogger(JPluzzDL.class.getName());
		Browser.setProxy(proxy);
		m_browser = Browser.BrowserSingleton.getBrowser();
		start();
	}

	private boolean isPluzzUrl() {
		final Pattern _p = Pattern.compile("http://www.pluzz.fr/[^\\.]+?\\.html");
		final Matcher _m = _p.matcher(m_url);
		return _m.find();
	}

	private void start() {
		try {
			if (isPluzzUrl()) {

				getID();

				getInfo();

				if ("oui".equals(m_drm)) {
					m_logger.log(Level.WARNING, "La vidéo possède un DRM ; elle sera sans doute illisible");
				}
				if (!"".equals(m_mmsLink)) {
					m_logger.log(
							Level.INFO,
							"MMS Link : "
									+ m_mmsLink
									+ "\n"
									+ "Lien MMS : %s\nUtiliser par exemple mimms ou msdl pour la recuperer directement ou l'option -f de pluzzdl pour essayer de la charger via ses fragments");
				}
				if (!"".equals(m_rtmpLink)) {
					m_logger.log(
							Level.INFO,
							"RTMP Link : "
									+ m_rtmpLink
									+ "\n"
									+ "Lien RTMP : %s\nUtiliser par exemple rtmpdump pour la recuperer directement ou l'option -f de pluzzdl pour essayer de la charger via ses fragments");
				}
				if (!("".equals(m_mmsLink) && "".equals(m_rtmpLink)) && !m_useFragments) {
					System.exit(0);
				}
				if ("".equals(m_manifestURL)) {
					m_logger.log(Level.SEVERE, "Pas de lien vers le manifest");
					System.exit(-1);
				}
			} else {
				String _page = "";
				try {
					_page = m_browser.getFileAsString(m_url);

				} catch (final IOException e) {
					m_logger.log(Level.SEVERE, "bad url");
					System.exit(-1);
				}

				final Pattern _p = Pattern.compile("(http://.+?manifest.f4m)");
				final Matcher _m = _p.matcher(_page);
				_m.find();
				try {
					m_manifestURL = _m.group(1);
				} catch (final Exception _e) {
					m_logger.log(Level.SEVERE, "Pas de lien vers le manifest");
					System.exit(-1);
				}

			}
			if (m_manifestURL.contains("media-secure")) {
				m_logger.log(Level.SEVERE, "jpluzzdl ne sait pas encore gérer ce type de vidéo...");
				System.exit(0);
			}

			try {
				final String _urlManifestURLToken = "http://hdfauth.francetv.fr/esi/urltokengen2.html?url="
						+ m_manifestURL.substring(m_manifestURL.lastIndexOf("/z/"));
				m_logger.log(Level.FINEST, "_urlManifestURLToken : " + _urlManifestURLToken);
				m_manifestURLToken = m_browser.getFileAsString(_urlManifestURLToken);
				m_logger.log(Level.FINEST, "m_manifestURLToken : " + m_manifestURLToken);
				m_manifest = m_browser.getFileAsString(m_manifestURLToken);
				m_logger.log(Level.FINEST, m_manifest);
			} catch (final IOException e) {
				m_logger.log(Level.SEVERE, "Impossible de charger le manifest");
				e.printStackTrace();
				System.exit(-1);
			}
			parseManifest();
			m_hdnea = m_manifestURLToken.substring(m_manifestURLToken.lastIndexOf("hdnea"));
			m_pv20 = m_pv2.substring(0, m_pv2.lastIndexOf(";"));
			m_hdntl = m_pv2.substring(m_pv2.lastIndexOf(";") + 1, m_pv2.length());
			m_pvTokenData = "st=0000000000~exp=9999999999~acl=%2f%2a~data=" + m_pv20 + "!" + getPlayerHash();

			m_pvToken = "pvtoken=" + URLEncoder.encode(m_pvTokenData, "US-ASCII") + "~hmac="
					+ hmacEncode(Hex.decodeHex(JPluzzConf.HMAC_KEY.toCharArray()), m_pvTokenData);

			m_firstFragment = 1;

			openNewVideo();

			m_maxNumOfFrag = Math.round((m_duration * m_bitrate) / 6040);
			m_logger.log(Level.INFO, "Estimation du nombre de fragments : " + m_maxNumOfFrag);

			m_logger.log(Level.INFO, "Début du téléchargement des fragments");
			m_currentFragment = m_firstFragment;
			int newP;
			int old = -1;
			int indice = 0;
			try {
				for (int i = m_firstFragment; i < 99999; i++) {
					m_currentFragment = i;
					final byte[] _frag = m_browser.getFile(m_urlFrag + i + "?" + m_pvToken + "&" + m_hdntl + "&" + m_hdnea);
					final int _start = startOfVideo(i, new String(_frag, "US-ASCII"));
					m_logger.log(Level.FINEST, "start = " + _start);
					m_videoFileOutputStream.write(_frag, _start, _frag.length - _start);
					m_videoFileOutputStream.flush();
					newP = handleProgression(m_maxNumOfFrag, indice, old);
					if (newP != old) {
						progressionListener.listen(String.valueOf(newP));
						old = newP;
						m_logger.log(Level.INFO, "Avancement : " + newP + " %");
					}
					indice++;
				}

			} catch (final IOException e) {
				m_logger.log(Level.FINEST, "erreur " + m_browser.getStatusCode() + " : " + m_browser.getReason());
				switch (m_browser.getStatusCode()) {

				case 403:
					if (m_browser.getReason().contains("Forbidden")) {
						m_logger.log(Level.SEVERE, e.getMessage());
						m_logger.log(Level.SEVERE, "Impossible de charger la vidéo");
					}
					break;
				case 404:
					m_logger.log(Level.INFO, "Fin du téléchargement");
					break;
				default:
					m_logger.log(Level.SEVERE, "Erreur inconnue");
				}

			} finally {

				m_logger.log(Level.FINEST, "Saving fragment " + m_currentFragment);
				m_videoFileOutputStream.close();

			}
		} catch (final Exception _e) {
			_e.printStackTrace();
			throw new RuntimeException(_e);
		}
	}

	private void getID() {
		try {
			final String _webPage = m_browser.getFileAsString(m_url);
			final Pattern _p = Pattern.compile("http://info.francetelevisions.fr/\\?id-video=([^\"]+)");
			final Matcher _m = _p.matcher(_webPage);
			_m.find();
			m_id = _m.group(1);
			m_logger.log(Level.FINEST, "ID de l'émission : " + m_id);
		} catch (final IOException e) {
			m_logger.log(Level.SEVERE, "Impossible de récupérer l'ID de l'émission");
			System.exit(-1);
		}
	}

	private void getInfo() {
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		ByteArrayInputStream _b = null;
		try {
			try {
				final SAXParser saxParser = factory.newSAXParser();
				final String _infos = m_browser.getFileAsString("http://www.pluzz.fr/appftv/webservices/video/getInfosOeuvre.php?mode=zeri&id-diffusion="
						+ m_id);
				_b = new ByteArrayInputStream(_infos.getBytes());

				final Reader reader = new InputStreamReader(_b, "UTF-8");

				final InputSource is = new InputSource(reader);
				is.setEncoding("UTF-8");

				saxParser.parse(is, new JPluzzDLInfosHandler());

			} finally {
				if (_b != null) {
					_b.close();
				}
			}

		} catch (final Exception e) {
			m_logger.log(Level.SEVERE, "Impossible de parser le fichier XML de l'émission");
			e.printStackTrace();
			System.exit(-1);
		}
		m_logger.log(Level.FINEST, "MMS Link : " + m_mmsLink);
		m_logger.log(Level.FINEST, "RTMP Link : " + m_rtmpLink);
		m_logger.log(Level.FINEST, "URL of the manifest : " + m_manifestURL);
		m_logger.log(Level.FINEST, "MMS Link : " + m_mmsLink);
		m_logger.log(Level.FINEST, "Use of DRM : " + m_drm);
	}

	private void parseManifest() {
		try {
			final DocumentBuilderFactory _factory = DocumentBuilderFactory.newInstance();
			_factory.setValidating(false);

			final DocumentBuilder constructeur = _factory.newDocumentBuilder();
			final InputStream _inputStream = new ByteArrayInputStream(m_manifest.getBytes());
			final Document document = constructeur.parse(_inputStream);

			final Element _root = document.getDocumentElement();

			m_duration = Float.parseFloat(((Element) _root.getElementsByTagName("duration").item(0)).getTextContent());
			m_pv2 = ((Element) _root.getElementsByTagName("pv-2.0").item(0)).getTextContent();

			final NodeList _childs = _root.getElementsByTagName("media");
			final Element _media = (Element) _childs.item(_childs.getLength() - 1);
			m_bitrate = Integer.parseInt(_media.getAttribute("bitrate"));
			final String _urlBootstrap = _media.getAttribute("url");
			m_urlFrag = m_manifestURLToken.substring(0, m_manifestURLToken.lastIndexOf("manifest.f4m")) + _urlBootstrap + "Seg1-Frag";
			m_flvHeader = Base64.decodeBase64(((Element) _media.getElementsByTagName("metadata").item(0)).getTextContent());

		} catch (final Exception e) {
			m_logger.log(Level.SEVERE, "Impossible de parser le manifest");
			e.printStackTrace();
			System.exit(-1);
		}

	}

	public static String hmacEncode(final byte[] key, final String message) throws Exception {
		String result = "";

		final Charset asciiCs = Charset.forName("US-ASCII");
		final Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		final SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(key, "HmacSHA256");
		sha256_HMAC.init(secret_key);
		final byte[] mac_data = sha256_HMAC.doFinal(asciiCs.encode(message).array());

		for (final byte element : mac_data) {
			result += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
		}

		return result;

	}

	public static String hexDigest(final byte[] b) {
		String _result = "";
		for (final byte element : b) {
			_result += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
		}
		return _result;
	}

	private static byte[] a2bHex(final String hexString) throws DecoderException {
		byte[] _result = null;

		_result = Hex.decodeHex(hexString.toCharArray());

		return _result;
	}

	private void openNewVideo() {
		try {

			m_videoFileOutputStream = new FileOutputStream(downloadOuput);
			m_videoFileOutputStream.write(a2bHex("464c56010500000009000000001200010c00000000000000"));
			m_videoFileOutputStream.write(m_flvHeader);
			m_videoFileOutputStream.write(a2bHex("00000000"));
			m_videoFileOutputStream.flush();

		} catch (final Exception e) {
			if (m_videoFileOutputStream != null) {
				try {
					m_videoFileOutputStream.close();
				} catch (final IOException e1) {
				}
			}
			m_logger.log(Level.SEVERE, "Impossible d'écrire dans le répertoire " + System.getProperty("user.dir"));
			e.printStackTrace();
			System.exit(-1);
		}

	}

	public static int toInt(final byte[] bytes, final int offset) {
		int ret = 0;
		for (int i = 0; i < 4 && i + offset < bytes.length; i++) {
			ret <<= 8;
			ret |= bytes[i] & 0xFF;
		}
		return ret;
	}

	private int startOfVideo(final int fragID, final String fragData) throws UnsupportedEncodingException {
		int _start = fragData.indexOf("mdat") + 4;
		if (fragID > 1) {
			for (int _dummy = 0; _dummy < 2; _dummy++) {
				int _tagLen = 0;
				final byte[] b = (fragData.substring(_start, _start + 4)).getBytes("US-ASCII");
				_tagLen = toInt(b, 0);
				_tagLen &= 0x00ffffff;
				_start += (_tagLen + 11 + 4);

			}
		}
		return _start;
	}

	private String getPlayerHash() throws IOException, NoSuchAlgorithmException, DecoderException {

		final byte[] _b = m_browser.getFile("http://www.pluzz.fr/layoutftv/players/h264/player.swf");

		final byte[] _b2 = decompressSWF(_b);

		final MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.update(_b2);
		String result = hexDigest(digest.digest());
		m_logger.log(Level.FINEST, result);

		final byte[] hexString = a2bHex(result);
		final byte[] _b64 = Base64.encodeBase64(hexString);
		result = new String(_b64);
		m_logger.log(Level.FINEST, result);

		return result;

	}

	private static byte[] decompressSWF(final byte[] swfData) throws IOException {
		final ByteArrayOutputStream _result = new ByteArrayOutputStream();
		final byte[] _buf = new byte[BUFFER_LEN];

		final InputStream _is = new ByteArrayInputStream(swfData);

		_is.read(_buf, 0, 3);
		final String _magic = new String(_buf, 0, 3, "ISO-8859-1");
		if (_magic.equals("CWS")) {
			_result.write("FWS".getBytes("ISO-8859-1"));
		}

		final byte[] _buf2 = new byte[5];
		_is.read(_buf2, 0, 5);
		_result.write(_buf2);

		final ByteArrayOutputStream _ba = new ByteArrayOutputStream();
		final InflaterInputStream _ins = new InflaterInputStream(_is);
		int i;
		while ((i = _ins.read()) != -1) {
			_ba.write(i);
		}
		_result.write(_ba.toByteArray());

		if (_ins != null) {
			_ins.close();
		}
		if (_ba != null) {
			_ba.close();
		}
		if (_is != null) {
			_is.close();
		}

		return _result.toByteArray();
	}

	private class JPluzzDLInfosHandler extends DefaultHandler {
		boolean isDRM = false;
		boolean isURL = false;

		@Override
		public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
			if ("url".equals(qName)) {
				isURL = true;
			}
			if ("drm".equals(qName)) {
				isDRM = true;
			}
		}

		@Override
		public void endElement(final String uri, final String localName, final String qName) throws SAXException {
			if ("url".equals(qName)) {
				isURL = false;
			}
			if ("drm".equals(qName)) {
				isDRM = false;
			}
		}

		@Override
		public void characters(final char ch[], final int start, final int length) throws SAXException {
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
			} else if (isDRM) {
				m_drm = _data;
			}
		}
	}

	public int handleProgression(final int nbMax, final int indice, final int old) {
		final float f = (float) indice / (float) nbMax;
		return Math.min((int) (f * 100), 100);
	}
}
