package com.dabi.habitv.downloader.youtube;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public class YoutubeDownloader {

	public static String newline = System.getProperty("line.separator");
	private static final Logger log = Logger.getLogger(YoutubeDownloader.class.getCanonicalName());
	private static final String scheme = "http";
	private static final String host = "www.youtube.com";
	private static final Pattern commaPattern = Pattern.compile(",");
	private static final Pattern equalPattern = Pattern.compile("=");

	public static Integer findBestFormat(final String downloadInput) {
		final Pattern pattern = Pattern.compile("itag=(\\d+)(,|\\\\)");
		final Matcher matcher = pattern.matcher(RetrieverUtils.getUrlContent(downloadInput));
		final List<Integer> formatList = new LinkedList<>();
		while (matcher.find()) {
			formatList.add(Integer.valueOf(matcher.group(1)));
		}
		final Integer ret;
		if (formatList.isEmpty()) {
			ret = 43;
		} else {
			ret = Collections.max(formatList);
		}
		return ret;
	}

	public static String getYoutubeId(final String downloadInput) {
		final Pattern pattern = Pattern.compile("(?<=videos\\/|v=)([\\w-]+)");
		final Matcher matcher = pattern.matcher(downloadInput);
		final boolean hasMatched = matcher.find();
		String ret = null;
		if (hasMatched) {
			ret = matcher.group(matcher.groupCount());
		}
		return ret;
	}

	public static void download(final String url, final String videoId, final int format, final String outputFile, final CmdProgressionListener listener) {
		log.fine("Retrieving " + videoId);
		final List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("video_id", videoId));
		qparams.add(new BasicNameValuePair("fmt", "" + format));
		URI uri;
		try {
			uri = getUri("get_video_info", qparams);
		} catch (final URISyntaxException e) {
			throw new TechnicalException(e);
		}

		final CookieStore cookieStore = new BasicCookieStore();
		final HttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		final HttpClient httpclient = new DefaultHttpClient();
		final HttpGet httpget = new HttpGet(uri);
		httpget.setHeader("User-Agent", YoutubeConf.USER_AGENT);

		log.finer("Executing " + uri);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget, localContext);
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
		final HttpEntity entity = response.getEntity();
		if (entity != null && response.getStatusLine().getStatusCode() == 200) {
			InputStream instream;
			try {
				instream = entity.getContent();
			} catch (IllegalStateException | IOException e) {
				throw new TechnicalException(e);
			}
			String videoInfo;
			try {
				videoInfo = getStringFromInputStream(YoutubeConf.ENCODING, instream);
			} catch (final IOException e) {
				throw new TechnicalException(e);
			}
			if (videoInfo != null && videoInfo.length() > 0) {
				final List<NameValuePair> infoMap = new ArrayList<NameValuePair>();
				URLEncodedUtils.parse(infoMap, new Scanner(videoInfo), YoutubeConf.ENCODING);
				String downloadUrl = null;

				for (final NameValuePair pair : infoMap) {
					final String key = pair.getName();
					final String val = pair.getValue();
					log.finest(key + "=" + val);
					if (key.equals("url_encoded_fmt_stream_map")) {
						final String[] formats = commaPattern.split(val);
						for (final String fmt : formats) {
							final String[] fmtPieces = equalPattern.split(fmt);
							if (fmtPieces.length > 0) {
								// in the end, download somethin!
								downloadUrl = fmtPieces[1];
							}
						}
					}
				}

				if (downloadUrl == null) {
					downloadUrl = error150Url(url);
				}
				downloadWithHttpClient(YoutubeConf.USER_AGENT, downloadUrl, new File(outputFile), listener);
			}
		}
	}

	public static String unescapeYoutubeUrl(final String s) {
		char ch;
		final StringBuilder sb = new StringBuilder();
		int j;
		int i;
		for (i = 0; i < s.length(); i++) {
			ch = s.charAt(i);
			switch (ch) {
			case '\\':
				ch = s.charAt(++i);
				StringBuilder sb2 = null;
				switch (ch) {
				/* unicode */
				case 'u':
					sb2 = new StringBuilder();
					i++;
					j = i + 4;
					for (; i < j; i++) {
						ch = s.charAt(i);
						if (sb2.length() > 0 || ch != '0') {
							sb2.append(ch);
						}
					}
					i--;
					sb.append((char) Long.parseLong(sb2.toString(), 16));
					continue;
					/* Hex */
				case 'x':
					sb2 = new StringBuilder();
					i++;
					j = i + 2;
					for (; i < j; i++) {
						ch = s.charAt(i);
						sb2.append(ch);
					}
					i--;
					sb.append((char) Long.parseLong(sb2.toString(), 16));
					continue;
				default:
					sb.append(ch);
					continue;
				}

			}
			sb.append(ch);
		}

		return sb.toString();
	}

	public static String error150Url(String url) {
		String pageCode = RetrieverUtils.getUrlContent(url);
		Matcher fmtMatcher = Pattern.compile("\"url_encoded_fmt_stream_map\": \"(.+)\"", Pattern.CASE_INSENSITIVE).matcher(pageCode);
		if (fmtMatcher.find()) {
			String strFmtMap = fmtMatcher.group(1);
			String decodedUrls;
			try {
				decodedUrls = URLDecoder.decode(strFmtMap, YoutubeConf.ENCODING);
			} catch (UnsupportedEncodingException e) {
				throw new TechnicalException(e);
			}
			if (decodedUrls != null) {
				String[] vUrls = Pattern.compile(",").split(decodedUrls);
				for (String elem : vUrls) {
					elem = unescapeYoutubeUrl(elem);

					Matcher urlMatcher = Pattern.compile("url=(http://.+&itag=(\\d+).*)&type=.*", Pattern.CASE_INSENSITIVE).matcher(elem);
					String downloadUrl = null;
					if (urlMatcher.find()) {
						downloadUrl = urlMatcher.group(1);
						return downloadUrl;
						// String fmt = urlMatcher.group(2);
						// format = getVideoFormat(Integer.parseInt(fmt));
					}
				}
			}
			return decodedUrls;
		}
		throw new TechnicalException("no url");
	}

	private static void downloadWithHttpClient(final String userAgent, final String downloadUrl, final File outputfile, final CmdProgressionListener listener) {
		HttpGet httpget2;
		try {
			httpget2 = new HttpGet(URLDecoder.decode(downloadUrl, YoutubeConf.ENCODING));
		} catch (final UnsupportedEncodingException e) {
			throw new TechnicalException(e);
		}
		httpget2.setHeader("User-Agent", userAgent);

		log.finer("Executing " + httpget2.getURI());
		final HttpClient httpclient2 = new DefaultHttpClient();
		HttpResponse response2;
		try {
			response2 = httpclient2.execute(httpget2);
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
		final HttpEntity entity2 = response2.getEntity();
		if (entity2 != null && response2.getStatusLine().getStatusCode() == 200) {//FIXME 403
			final long length = entity2.getContentLength();
			InputStream instream2;
			try {
				instream2 = entity2.getContent();
			} catch (IllegalStateException | IOException e) {
				throw new TechnicalException(e);
			}
			log.finer("Writing " + length + " bytes to " + outputfile);
			if (outputfile.exists()) {
				outputfile.delete();
			}
			FileOutputStream outstream = null;
			int byteProgress = 0;
			try {
				outstream = new FileOutputStream(outputfile);
				final byte[] buffer = new byte[2048];
				int count = -1;
				long lastTime = System.currentTimeMillis();
				String progress;
				while ((count = instream2.read(buffer)) != -1) {
					outstream.write(buffer, 0, count);
					byteProgress += count;
					progress = String.valueOf(Math.round(Math.min(100, Double.valueOf(byteProgress) / Double.valueOf(length) * 100)));
					if (listener != null && progress != null && (System.currentTimeMillis() - lastTime) > FrameworkConf.TIME_BETWEEN_LOG) {
						lastTime = System.currentTimeMillis();
						listener.listen(progress);
					}
				}
				listener.listen("100");
				outstream.flush();
			} catch (final IOException e) {
				throw new TechnicalException(e);
			} finally {
				try {
					if (outstream != null) {
						outstream.close();
					}
				} catch (final IOException e) {
					throw new TechnicalException(e);
				}
			}
		}
	}

	private static URI getUri(final String path, final List<NameValuePair> qparams) throws URISyntaxException {
		final URI uri = URIUtils.createURI(scheme, host, -1, "/" + path, URLEncodedUtils.format(qparams, YoutubeConf.ENCODING), null);
		return uri;
	}

	private static String getStringFromInputStream(final String encoding, final InputStream instream) throws UnsupportedEncodingException, IOException {
		final Writer writer = new StringWriter();

		final char[] buffer = new char[1024];
		try {
			final Reader reader = new BufferedReader(new InputStreamReader(instream, encoding));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} finally {
			instream.close();
		}
		final String result = writer.toString();
		return result;
	}
}

/**
 * <pre>
 * Exploded results from get_video_info:
 * 
 * fexp=90...
 * allow_embed=1
 * fmt_stream_map=35|http://v9.lscache8...
 * fmt_url_map=35|http://v9.lscache8...
 * allow_ratings=1
 * keywords=Stefan Molyneux,Luke Bessey,anarchy,stateless society,giant stone cow,the story of our unenslavement,market anarchy,voluntaryism,anarcho capitalism
 * track_embed=0
 * fmt_list=35/854x480/9/0/115,34/640x360/9/0/115,18/640x360/9/0/115,5/320x240/7/0/0
 * author=lukebessey
 * muted=0
 * length_seconds=390
 * plid=AA...
 * ftoken=null
 * status=ok
 * watermark=http://s.ytimg.com/yt/swf/logo-vfl_bP6ud.swf,http://s.ytimg.com/yt/swf/hdlogo-vfloR6wva.swf
 * timestamp=12...
 * has_cc=False
 * fmt_map=35/854x480/9/0/115,34/640x360/9/0/115,18/640x360/9/0/115,5/320x240/7/0/0
 * leanback_module=http://s.ytimg.com/yt/swfbin/leanback_module-vflJYyeZN.swf
 * hl=en_US
 * endscreen_module=http://s.ytimg.com/yt/swfbin/endscreen-vflk19iTq.swf
 * vq=auto
 * avg_rating=5.0
 * video_id=S6IZP3yRJ9I
 * token=vPpcFNh...
 * thumbnail_url=http://i4.ytimg.com/vi/S6IZP3yRJ9I/default.jpg
 * title=The Story of Our Unenslavement - Animated
 * </pre>
 */
