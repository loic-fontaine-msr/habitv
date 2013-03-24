package com.dabi.habitv.framework.plugin.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;

/**
 * utils function to retrieve remote data
 * 
 */
public final class RetrieverUtils {

	private static final String USER_AGENT = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)";

	private RetrieverUtils() {

	}

	/**
	 * Create an inputStream from an URL throws runtime technical exception if
	 * fail
	 * 
	 * @param url
	 *            the URL
	 * @param proxy
	 * @param timeOut
	 * @return the input stream
	 */
	public static InputStream getInputStreamFromUrl(final String url, final Proxy proxy) {
		return getInputStreamFromUrl(url, FrameworkConf.TIME_OUT_MS, proxy);
	}

	public static InputStream getInputStreamFromUrl(final String url, final Integer timeOut, final Proxy proxy) {
		try {
			final URLConnection hc = (new URL(url)).openConnection(proxy);
			if (timeOut != null) {
				hc.setConnectTimeout(timeOut);
				hc.setReadTimeout(timeOut);
			}
			hc.setRequestProperty("User-Agent", USER_AGENT);
			return hc.getInputStream();
		} catch (final IOException e) {
			throw new TechnicalException(e);
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

	public static String getUrlContent(final String url, final Proxy proxy) {
		return getUrlContent(url, null, proxy);
	}

	public static String getUrlContent(final String url, final String encoding, final Proxy proxy) {

		final InputStream in = getInputStreamFromUrl(url, proxy);
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

	public static String getUrlContentRef(final String url, final String referer, final Proxy proxy) {

		try {
			final URLConnection hc = (new URL(url)).openConnection(proxy);
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

	public static byte[] getUrlContentBytes(final String url, final Proxy proxy) {
		final InputStream in = getInputStreamFromUrl(url, null, proxy);
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