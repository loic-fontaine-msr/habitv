package com.dabi.habitv.framework.plugin.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.codec.binary.Base64InputStream;

import com.dabi.habitv.framework.plugin.exception.TechnicalException;

/**
 * utils function to retrieve remote data
 * 
 */
public final class RetrieverUtils {

	private static final int BUFFER_SIZE = 16384;

	// private static final int URL_BUFFER_SIZE = 256;

	private static final String USER_AGENT = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)";

	private RetrieverUtils() {

	}

	/**
	 * Create an inputStream from an URL throws runtime technical exception if
	 * fail
	 * 
	 * @param url
	 *            the URL
	 * @return the input stream
	 */
	public static InputStream getInputStreamFromUrl(final String url) {
		try {
			final URLConnection hc = (new URL(url)).openConnection();
			hc.setConnectTimeout(15000);
			hc.setReadTimeout(15000);
			hc.setRequestProperty("User-Agent", USER_AGENT);
			return hc.getInputStream();
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
	}

	/**
	 * Create an inputStream from an URL with encryption throws runtime
	 * technical exception if fail
	 * 
	 * @param url
	 *            the url
	 * @param encryption
	 *            the encryption type @see Cipher#getInstance(String)
	 * @param secretKey
	 *            the secret key @see Cipher#init(int, java.security.Key)
	 * @return the input stream
	 */
	public static InputStream getEncryptedInputStreamFromUrl(final String url, final String encryption, final String secretKey) {
		InputStream input = null;
		try {

			final URLConnection hc = (new URL(url)).openConnection();
			hc.setRequestProperty("User-Agent", USER_AGENT);
			input = new Base64InputStream(hc.getInputStream());

			int nRead;
			final byte[] data = new byte[BUFFER_SIZE];
			final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			while ((nRead = input.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			buffer.flush();

			final SecretKeySpec key = new SecretKeySpec(secretKey.getBytes("UTF-8"), encryption);

			final Cipher cipher = Cipher.getInstance(encryption);
			cipher.init(Cipher.DECRYPT_MODE, key);
			final byte[] newPlainText = cipher.doFinal(buffer.toByteArray());
			return new ByteArrayInputStream(newPlainText);
		} catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (final IOException e) {
				throw new TechnicalException(e);
			}
		}
	}

	/**
	 * Unmarshal the inputstream to an Object to be casted
	 * 
	 * @param input
	 *            the input stream
	 * @param unmarshallerPackage
	 *            the jaxb entities package
	 * @param classLoader
	 *            the classLoader since plugin can be load with a different
	 *            classloader
	 * @return the unmarshalled object to be casted
	 */
	public static Object unmarshalInputStream(final InputStream input, final String unmarshallerPackage, final ClassLoader classLoader) {
		try {
			final JAXBContext context;
			if (classLoader == null) {
				context = JAXBContext.newInstance(unmarshallerPackage);
			} else {
				context = JAXBContext.newInstance(unmarshallerPackage, classLoader);
			}
			return context.createUnmarshaller().unmarshal(input);
		} catch (final JAXBException e) {
			throw new TechnicalException(e);
		}
	}

	/**
	 * @see #unmarshalInputStream(InputStream, String) without the classloader
	 */
	public static Object unmarshalInputStream(final InputStream input, final String unmarshallerPackage) {
		return unmarshalInputStream(input, unmarshallerPackage, null);
	}

	public static String getUrlContent(final String url) {
		return getUrlContent(url, null);
	}

	public static String getUrlContent(final String url, final String encoding) {

		final InputStream in = getInputStreamFromUrl(url);
		final BufferedReader reader;
		try {
			if (encoding != null) {
				reader = new BufferedReader(new InputStreamReader(in, encoding));
			} else {
				reader = new BufferedReader(new InputStreamReader(in));
			}
		} catch (final UnsupportedEncodingException e) {
			throw new TechnicalException(e);
		}
		final StringBuffer sb = new StringBuffer();

		String readLine;
		try {
			while ((readLine = reader.readLine()) != null) {
				sb.append(readLine + "\r\n");
			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}

		return sb.toString();

	}

	public static String getUrlContentRef(final String url, final String referer) {

		try {
			final URLConnection hc = (new URL(url)).openConnection();
			hc.setRequestProperty("User-Agent", USER_AGENT);
			hc.setRequestProperty("referer", referer);
			final InputStream in = hc.getInputStream();

			final BufferedReader reader;
			reader = new BufferedReader(new InputStreamReader(in));
			final StringBuffer sb = new StringBuffer();

			String readLine;
			while ((readLine = reader.readLine()) != null) {
				sb.append(readLine);
			}
			return sb.toString();
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}

	}

	public static byte[] getUrlContentBytes(final String url) {
		final InputStream in = getInputStreamFromUrl(url);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i;
		try {
			while ((i = in.read()) != -1) {
				baos.write(i);
				baos.flush();
			}
			baos.close();
			in.close();
		} catch (final IOException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				in.close();
			} catch (final IOException e) {
				// should log ?
			}
		}
		return baos.toByteArray();
	}
}