package com.dabi.habitv.provider.pluzz.jpluzz;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class PluzzDLF4M {

	public final static int BUFFER_LEN = 1024 * 1024;

	private static final Logger logger = Logger.getLogger(PluzzDLF4M.class);

	private Browser browser = Browser.BrowserSingleton.getBrowser();

	private float duration;

	private String pv2;

	private String urlFrag;

	private byte[] flvHeader;

	private final CmdProgressionListener progressionListener;

	private final String downloadOuput;

	public PluzzDLF4M(CmdProgressionListener progressionListener,
			final String downloadOuput) {
		this.progressionListener = progressionListener;
		this.downloadOuput = downloadOuput;
	}

	public void dl(String manifestURL) throws DecoderException, Exception {
		// Verifie si le lien du manifest contient la chaine "media-secure"
		if (manifestURL.contains("media-secure")) {
			logger.error("pluzzdl ne sait pas encore gérer ce type de vidéo...");
			throw new TechnicalException("");
		}
		String subUrl = manifestURL.substring(manifestURL.indexOf("/z/"));
		// Lien du manifest (apres le token)
		String manifestURLToken = browser
				.getFileAsString("http://hdfauth.francetv.fr/esi/urltokengen2.html?url="
						+ subUrl);
		// Recupere le manifest
		String manifest = browser.getFileAsString(manifestURLToken);
		// Parse le manifest
		parseManifest(manifestURL, manifest);
		String[] pv2T = pv2.split(";");
		String hdntl = pv2T[1];

		// Creation de la video

		int premierFragment = 1;
		OutputStream videoFileOutputStream = openNewVideo();

		// Calcul l'estimation du nombre de fragments
		int nbFragMax = Math.round(duration / 6F);
		logger.debug("Estimation du nombre de fragments : " + nbFragMax);

		// Ajout des fragments
		logger.info("Début du téléchargement des fragments");
		int i = premierFragment;
		int old = -1;
		browser.appendCookie("hdntl", hdntl);
		try {
			while (i <= nbFragMax) {
				browser.addReferer("http://fpdownload.adobe.com/strobe/FlashMediaPlayback_101.swf");
				final byte[] frag = browser.getFile(urlFrag + i);
				int start = startOfVideo(i, new String(frag, "US-ASCII"));
				videoFileOutputStream.write(frag, start, frag.length - start);
				// Affichage de la progression
				int newP = handleProgression(nbFragMax, i, old);
				if (newP != old) {
					progressionListener.listen(String.valueOf(newP));
					old = newP;
					logger.info("Avancement : " + newP + " %");
				}
				i++;
			}
		} catch (final IOException e) {
			logger.error("erreur " + browser.getStatusCode() + " : "
					+ browser.getReason());
			switch (browser.getStatusCode()) {

			case 403:
				if (browser.getReason().contains("Forbidden")) {
					logger.error(e.getMessage());
					logger.error("Impossible de charger la vidéo");
				}
				break;
			case 404:
				logger.info("Fin du téléchargement");
				break;
			default:
				logger.error("Erreur inconnue");
			}

		} finally {
			videoFileOutputStream.close();

		}

	}

	public int handleProgression(final int nbMax, final int indice,
			final int old) {
		final float f = (float) indice / (float) nbMax;
		return Math.min((int) (f * 100), 100);
	}

	public static int toInt(final byte[] bytes, final int offset) {
		int ret = 0;
		for (int i = 0; i < 4 && i + offset < bytes.length; i++) {
			ret <<= 8;
			ret |= bytes[i] & 0xFF;
		}
		return ret;
	}

	private int startOfVideo(final int fragID, final String fragData)
			throws UnsupportedEncodingException {
		int _start = fragData.indexOf("mdat") + 4;
		if (fragID > 1) {
			for (int _dummy = 0; _dummy < 2; _dummy++) {
				int _tagLen = 0;
				final byte[] b = (fragData.substring(_start, _start + 4))
						.getBytes("US-ASCII");
				_tagLen = toInt(b, 0);
				_tagLen &= 0x00ffffff;
				_start += (_tagLen + 11 + 4);

			}
		}
		return _start;
	}

	private OutputStream openNewVideo() {
		FileOutputStream videoFileOutputStream = null;
		try {

			videoFileOutputStream = new FileOutputStream(downloadOuput);
			videoFileOutputStream
					.write(a2bHex("464c56010500000009000000001200010c00000000000000"));
			videoFileOutputStream.write(flvHeader);
			videoFileOutputStream.write(a2bHex("00000000"));
			videoFileOutputStream.flush();

		} catch (final Exception e) {
			if (videoFileOutputStream != null) {
				try {
					videoFileOutputStream.close();
				} catch (final IOException e1) {
				}
			}
			logger.error("Impossible d'écrire dans le répertoire "
					+ System.getProperty("user.dir"));
			e.printStackTrace();
			throw new TechnicalException(
					"Impossible d'écrire dans le répertoire ");
		}
		return videoFileOutputStream;
	}

	public static String hmacEncode(final byte[] key, final String message)
			throws Exception {
		String result = "";

		final Charset asciiCs = Charset.forName("US-ASCII");
		final Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		final SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(
				key, "HmacSHA256");
		sha256_HMAC.init(secret_key);
		final byte[] mac_data = sha256_HMAC.doFinal(asciiCs.encode(message)
				.array());

		for (final byte element : mac_data) {
			result += Integer.toString((element & 0xff) + 0x100, 16).substring(
					1);
		}

		return result;

	}

	// private void parseManifest2() {
	// arbre = xml.etree.ElementTree.fromstring( self.manifest )
	// # Duree
	// self.duree = float( arbre.find( "{http://ns.adobe.com/f4m/1.0}duration"
	// ).text )
	// self.pv2 = arbre.find( "{http://ns.adobe.com/f4m/1.0}pv-2.0" ).text
	// media = arbre.findall( "{http://ns.adobe.com/f4m/1.0}media" )[ -1 ]
	// # Bitrate
	// self.bitrate = int( media.attrib[ "bitrate" ] )
	// # URL des fragments
	// urlbootstrap = media.attrib[ "url" ]
	// self.urlFrag = "%s%sSeg1-Frag" %( self.manifestURLToken[ :
	// self.manifestURLToken.find( "manifest.f4m" ) ], urlbootstrap )
	// # Header du fichier final
	// self.flvHeader = base64.b64decode( media.find(
	// "{http://ns.adobe.com/f4m/1.0}metadata" ).text )
	// except :
	// logger.critical( "Impossible de parser le manifest" )
	// sys.exit( -1 )
	//
	// }

	private void parseManifest(String manifestUrl, String manifest) {
		try {
			final DocumentBuilderFactory _factory = DocumentBuilderFactory
					.newInstance();
			_factory.setValidating(false);

			final DocumentBuilder constructeur = _factory.newDocumentBuilder();
			final InputStream _inputStream = new ByteArrayInputStream(
					manifest.getBytes());
			final Document document = constructeur.parse(_inputStream);

			final Element _root = document.getDocumentElement();

			duration = Float.parseFloat(((Element) _root.getElementsByTagName(
					"duration").item(0)).getTextContent());
			pv2 = ((Element) _root.getElementsByTagName("pv-2.0").item(0))
					.getTextContent();

			final NodeList _childs = _root.getElementsByTagName("media");
			final Element _media = (Element) _childs
					.item(_childs.getLength() - 1);
			final String _urlBootstrap = _media.getAttribute("url");
			urlFrag = manifestUrl.substring(0,
					manifestUrl.lastIndexOf("manifest.f4m"))
					+ _urlBootstrap + "Seg1-Frag";
			flvHeader = Base64
					.decodeBase64(((Element) _media.getElementsByTagName(
							"metadata").item(0)).getTextContent());

		} catch (final Exception e) {
			logger.error("Impossible de parser le manifest");
			e.printStackTrace();
			throw new TechnicalException("Impossible de parser le manifest");
		}

	}

	public static String hexDigest(final byte[] b) {
		String _result = "";
		for (final byte element : b) {
			_result += Integer.toString((element & 0xff) + 0x100, 16)
					.substring(1);
		}
		return _result;
	}

	private static byte[] a2bHex(final String hexString)
			throws DecoderException {
		byte[] _result = null;

		_result = Hex.decodeHex(hexString.toCharArray());

		return _result;
	}
}
