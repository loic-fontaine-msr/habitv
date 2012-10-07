package com.dabi.habitv.provider.pluzz.jpluzz;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class PluzzDLF4M {

	public final static int BUFFER_LEN = 1024 * 1024;

	private static final Logger LOGGER = Logger.getLogger(PluzzDLF4M.class);

	private final Browser browser = Browser.BrowserSingleton.getBrowser();

	private float duration;

	private String pv2;

	private String urlFrag;

	private byte[] flvHeader;

	private final CmdProgressionListener progressionListener;

	private final String downloadOuput;

	public PluzzDLF4M(final CmdProgressionListener progressionListener, final String downloadOuput) {
		this.progressionListener = progressionListener;
		this.downloadOuput = downloadOuput;
	}

	public void dl(final String manifestURL) throws DownloadFailedException {
		// Verifie si le lien du manifest contient la chaine "media-secure"
		if (manifestURL.contains("media-secure")) {
			throw new DownloadFailedException("video securisee");
		}
		final String subUrl = manifestURL.substring(manifestURL.indexOf("/z/"));
		// Lien du manifest (apres le token)
		String manifestURLToken;
		try {
			manifestURLToken = browser.getFileAsString("http://hdfauth.francetv.fr/esi/urltokengen2.html?url=" + subUrl);
		} catch (final IOException e) {
			throw new DownloadFailedException("Erreur lors de la recuperation de l'URL du manifest");
		}
		String manifest;
		try {
			// Recupere le manifest
			manifest = browser.getFileAsString(manifestURLToken);
		} catch (final IOException e) {
			throw new DownloadFailedException("Erreur lors de la recuperation de l'URL du manifest");
		}
		// Parse le manifest
		parseManifest(manifestURL, manifest);
		final String[] pv2T = pv2.split(";");
		final String hdntl = pv2T[1];

		// Creation de la video

		final int premierFragment = 1;
		final OutputStream videoFileOutputStream = openNewVideo();

		// Calcul l'estimation du nombre de fragments
		final int nbFragMax = Math.round(duration / 6F);
		LOGGER.debug("Estimation du nombre de fragments : " + nbFragMax);

		// Ajout des fragments
		int i = premierFragment;
		int old = -1;
		browser.appendCookie("hdntl", hdntl);
		try {
			while (i <= nbFragMax) {
				browser.addReferer("http://fpdownload.adobe.com/strobe/FlashMediaPlayback_101.swf");
				final byte[] frag = browser.getFile(urlFrag + i);
				final int start = startOfVideo(i, new String(frag, "US-ASCII"));
				videoFileOutputStream.write(frag, start, frag.length - start);
				// Affichage de la progression
				final int newP = handleProgression(nbFragMax, i, old);
				if (newP != old) {
					progressionListener.listen(String.valueOf(newP));
					old = newP;
					LOGGER.debug("Avancement : " + newP + " %");
				}
				i++;
			}
		} catch (final IOException e) {
			switch (browser.getStatusCode()) {
			case 403:
				if (browser.getReason().contains("Forbidden")) {
					LOGGER.error("", e);
					throw new DownloadFailedException(e);
				}
				break;
			case 404:
				LOGGER.debug("Fin du telechargement");
				break;
			default:
				throw new DownloadFailedException(e);
			}

		} finally {
			try {
				videoFileOutputStream.close();
			} catch (final IOException e) {
				LOGGER.error("", e);
			}

		}

	}

	private int handleProgression(final int nbMax, final int indice, final int old) {
		final float f = (float) indice / (float) nbMax;
		return Math.min((int) (f * 100), 100);
	}

	private static int toInt(final byte[] bytes, final int offset) {
		int ret = 0;
		for (int i = 0; i < 4 && i + offset < bytes.length; i++) {
			ret <<= 8;
			ret |= bytes[i] & 0xFF;
		}
		return ret;
	}

	private int startOfVideo(final int fragID, final String fragData) {
		int start = fragData.indexOf("mdat") + 4;
		if (fragID > 1) {
			for (int dummy = 0; dummy < 2; dummy++) {
				int tagLen = 0;
				byte[] b;
				try {
					b = (fragData.substring(start, start + 4)).getBytes("US-ASCII");
				} catch (final UnsupportedEncodingException e) {
					throw new TechnicalException(e);
				}
				tagLen = toInt(b, 0);
				tagLen &= 0x00ffffff;
				start += (tagLen + 11 + 4);

			}
		}
		return start;
	}

	private OutputStream openNewVideo() {
		FileOutputStream videoFileOutputStream = null;
		try {
			videoFileOutputStream = new FileOutputStream(downloadOuput);
			videoFileOutputStream.write(a2bHex("464c56010500000009000000001200010c00000000000000"));
			videoFileOutputStream.write(flvHeader);
			videoFileOutputStream.write(a2bHex("00000000"));
			videoFileOutputStream.flush();

		} catch (final IOException e) {
			if (videoFileOutputStream != null) {
				try {
					videoFileOutputStream.close();
				} catch (final IOException e1) {
				}
			}
			throw new TechnicalException("Erreur d'ecriture ");
		}
		return videoFileOutputStream;
	}

	private void parseManifest(final String manifestUrl, final String manifest) {
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);

			final DocumentBuilder constructeur = factory.newDocumentBuilder();
			final InputStream _inputStream = new ByteArrayInputStream(manifest.getBytes());
			final Document document = constructeur.parse(_inputStream);

			final Element _root = document.getDocumentElement();

			duration = Float.parseFloat(((Element) _root.getElementsByTagName("duration").item(0)).getTextContent());
			pv2 = ((Element) _root.getElementsByTagName("pv-2.0").item(0)).getTextContent();

			final NodeList _childs = _root.getElementsByTagName("media");
			final Element _media = (Element) _childs.item(_childs.getLength() - 1);
			final String _urlBootstrap = _media.getAttribute("url");
			urlFrag = manifestUrl.substring(0, manifestUrl.lastIndexOf("manifest.f4m")) + _urlBootstrap + "Seg1-Frag";
			flvHeader = Base64.decodeBase64(((Element) _media.getElementsByTagName("metadata").item(0)).getTextContent());

		} catch (final IOException | SAXException | ParserConfigurationException e) {
			throw new TechnicalException(e);
		}

	}

	private static byte[] a2bHex(final String hexString) {
		byte[] result = null;

		try {
			result = Hex.decodeHex(hexString.toCharArray());
		} catch (final DecoderException e) {
			throw new TechnicalException(e);
		}

		return result;
	}
}
