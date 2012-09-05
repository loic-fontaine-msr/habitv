package com.dabi.habitv.provider.pluzz.jpluzz;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

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

public class Browser {
	/**
	 * This example demonstrates the use of the {@link ResponseHandler} to
	 * simplify the process of processing the HTTP response and releasing
	 * associated resources.
	 */
	private final HttpClient m_httpClient;
	private final CookieStore m_cookieStore;
	private int m_statusCode = 200;

	private final Logger m_logger;
	private String m_reason;
	private String referer;

	private static String m_proxy = null;

	public String getReason() {
		return m_reason;
	}

	public int getStatusCode() {
		return m_statusCode;
	}

	final static String[] m_userAgents = {
			"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_5; fr-fr) AppleWebKit/525.18 (KHTML, like Gecko) Version/3.1.2 Safari/525.20.1",
			"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.186 Safari/535.1",
			"Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13",
			"Mozilla/5.0 (X11; U; Linux x86_64; en-us) AppleWebKit/528.5+ (KHTML, like Gecko, Safari/528.5+) midori",
			"Mozilla/5.0 (Windows NT 6.0) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.107 Safari/535.1",
			"Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en-us) AppleWebKit/312.1 (KHTML, like Gecko) Safari/312",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.12 Safari/535.11",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.8 (KHTML, like Gecko) Chrome/17.0.940.0 Safari/535.8" };

	private Browser() {
		m_logger = Logger.getLogger(Browser.class.getName());
		m_httpClient = new DefaultHttpClient();
		if (m_proxy != null) {
			final Pattern _pattern = Pattern.compile("http://([^:]+?):(\\d+)");
			final Matcher _matcher = _pattern.matcher(m_proxy);
			_matcher.find();
			final HttpHost _proxy = new HttpHost(_matcher.group(1),
					Integer.parseInt(_matcher.group(2)));
			m_httpClient.getParams().setParameter(
					ConnRoutePNames.DEFAULT_PROXY, _proxy);
		}

		// m_httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
		// userAgent);
		m_cookieStore = new BasicCookieStore();

	}

	public byte[] getFile(final String url) throws IOException {

		final HttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, m_cookieStore);
		final HttpGet httpget = new HttpGet(url);
		httpget.addHeader("Referer", referer);
		m_logger.log(Level.FINEST, "executing request " + httpget.getURI());
		final HttpResponse response = m_httpClient.execute(httpget,
				localContext);
		final HttpEntity entity = response.getEntity();

		if (response.getStatusLine().getStatusCode() < 200
				|| response.getStatusLine().getStatusCode() >= 400) {
			m_logger.log(Level.FINEST, "Error Retrieving webpage " + url
					+ ": error " + response.getStatusLine().getStatusCode());
			m_statusCode = response.getStatusLine().getStatusCode();
			m_reason = response.getStatusLine().getReasonPhrase();
			EntityUtils.consume(entity);
			throw new IOException("Got bad response, error code = "
					+ response.getStatusLine().getStatusCode());
		}

		if (entity != null) {
			// m_encoding = entity.getContentEncoding().toString();
			m_logger.log(Level.FINEST, entity.getContentType().getValue());
			// String _body = EntityUtils.toString(entity,"ISO-8859-1");
			final InputStream _is = entity.getContent();
			final ByteArrayOutputStream _baos = new ByteArrayOutputStream();
			int _i;
			while ((_i = _is.read()) != -1) {
				_baos.write(_i);
				_baos.flush();
			}
			_baos.close();
			_is.close();
			EntityUtils.consume(entity);
			return _baos.toByteArray();
		}
		return null;
	}

	public String getFileAsString(final String url) throws IOException {
		return new String(getFile(url));
	}

	public void close() {
		m_httpClient.getConnectionManager().shutdown();
	}

	public static void setProxy(final String proxy) {
		Browser.m_proxy = proxy;
	}

	public static class BrowserSingleton {

		private static Browser m_browser = null;

		public static Browser getBrowser() {
			if (m_browser == null) {
				m_browser = new Browser();
			}
			return m_browser;
		}

	}

	public void appendCookie(String name, String value) {
		Cookie cookie = new BasicClientCookie(name, value);
		m_cookieStore.addCookie(cookie);
	}

	public void addReferer(String referer) {
		this.referer = referer; 
	}
}
