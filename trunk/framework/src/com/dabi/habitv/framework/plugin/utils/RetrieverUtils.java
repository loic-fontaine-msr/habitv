package com.dabi.habitv.framework.plugin.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
			return new URL(url).openStream();
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

			input = new Base64InputStream(new URL(url).openStream());

			int nRead;
			final byte[] data = new byte[16384];
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

		final InputStream in = getInputStreamFromUrl(url);
		final StringBuffer sb = new StringBuffer();

		final byte[] buffer = new byte[256];

		while (true) {
			int byteRead;
			try {
				byteRead = in.read(buffer);
			} catch (final IOException e) {
				throw new TechnicalException(e);
			}
			if (byteRead == -1) {
				break;
			}
			for (int i = 0; i < byteRead; i++) {
				sb.append((char) buffer[i]);
			}
		}
		return sb.toString();

	}

}