package com.dabi.habitv.provider.tf1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.provider.BasePluginProvider;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdExecutor;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class TF1PluginManager extends BasePluginProvider {

	@Override
	public String getName() {
		return TF1Conf.NAME;
	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		final Set<EpisodeDTO> episodes = new HashSet<>();

		final Document doc = Jsoup.parse(getUrlContent(TF1Conf.HOME_URL + "/" + category.getId()));

		final Elements select = doc.select(".teaser");
		for (final Element element : select) {
			try {
				final Element divInfoIntegrale = element.child(1);
				if (("description".equals(divInfoIntegrale.attr("class")) || "infosTeaser".equals(divInfoIntegrale.attr("class")) || "infosIntegrale"
						.equals(divInfoIntegrale.attr("class"))) && divInfoIntegrale.children().size() > 1) {
					final Element child = divInfoIntegrale.child(1);
					if (!child.children().isEmpty()) {
						final String title = child.child(0).text();
						final String url = child.child(0).attr("href");
						episodes.add(new EpisodeDTO(category, title, url));
					}
				}
			} catch (final IndexOutOfBoundsException e) {
				getLog().error(element, e);
				//throw new TechnicalException(e);
			}
		}
		return episodes;
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categories = new HashSet<>();

		final Document doc = Jsoup.parse(getUrlContent(TF1Conf.HOME_URL));

		final Elements select = doc.select(".teaser");
		for (final Element element : select) {
			final String attr = element.child(0).child(0).child(0).attr("onmousedown");
			if (attr.contains("|")) {
				final String key = attr.split("\\|")[3];
				categories.add(new CategoryDTO(TF1Conf.NAME, key, key, TF1Conf.EXTENSION));
			}
		}
		return categories;
	}

	private String getDownloader(final String url) {
		String downloaderName;
		if (url.startsWith(TF1Conf.RTMPDUMP_PREFIX)) {
			downloaderName = TF1Conf.RTMDUMP;
		} else {
			downloaderName = TF1Conf.CURL;
		}
		return downloaderName;
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener,
			final EpisodeDTO episode) throws DownloadFailedException, NoSuchDownloaderException {
		final VideoStruct videoStruct = findFinalUrl(episode);

		if (videoStruct.getMediaIdList().isEmpty()) {
			throw new DownloadFailedException("no link");
		}

		final String firstMediaID = videoStruct.getMediaIdList().iterator().next();

		String videoUrl;
		try {
			videoUrl = getUrlContent(buildUrlVideoInfo(firstMediaID, "webhd"));
		} catch (final Exception e) {
			videoUrl = getUrlContent(buildUrlVideoInfo(firstMediaID, "web"));
		}
		final String downloaderName = getDownloader(videoUrl);
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());

		if (TF1Conf.RTMDUMP.equals(downloaderName)) {
			videoUrl = videoUrl.replace(",rtmpte", "");
			videoUrl = videoUrl.substring(0, videoUrl.lastIndexOf("?"));
			parameters.put(FrameworkConf.PARAMETER_ARGS, TF1Conf.DUMP_CMD);
			pluginDownloader.download(videoUrl, downloadOuput, parameters, cmdProgressionListener, getProtocol2proxy());
		} else {
			if (videoStruct.getMediaIdList().size() == 1) {
				pluginDownloader.download(videoUrl, downloadOuput, parameters, cmdProgressionListener, getProtocol2proxy());
			} else {
				final String assemblerBinPath = downloaders.getBinPath(TF1Conf.ASSEMBLER);
				if (assemblerBinPath == null) {
					throw new TechnicalException(TF1Conf.ASSEMBLER + " downloader can't be found, add it the config.xml");
				}
				downloadFragments(videoStruct, downloadOuput, cmdProgressionListener, TF1Conf.CORRECT_VIDEO_CMD, assemblerBinPath);
			}
		}
	}

	private int handleProgression(final int nbMax, final int indice, final int old) {
		final float f = (float) indice / (float) nbMax;
		return Math.min((int) (f * 100), 100);
	}

	// TODO mutualiser avec Pluzz
	private void downloadFragments(final VideoStruct videoStruct, final String downloadOutput, final CmdProgressionListener progressionListener,
			final String assemblerCmd, final String assembler) throws DownloadFailedException {
		FileOutputStream fOutputStream = null;
		int i = 0;
		int old = -1;
		final StringBuilder tsList = new StringBuilder("");
		final List<String> tsFilesList = new LinkedList<>();
		for (final String mediaId : videoStruct.getMediaIdList()) {
			final String tmpVideoFile = downloadOutput + "-" + i + ".frg";
			try {
				fOutputStream = new FileOutputStream(tmpVideoFile);

				final byte[] buffer = new byte[1024]; // Adjust if you want
				int bytesRead;
				final InputStream input = getInputStreamFromUrl(getUrlContent(buildUrlVideoInfo(mediaId, "web")));
				while ((bytesRead = input.read(buffer)) != -1) {
					fOutputStream.write(buffer, 0, bytesRead);
				}// TODO dl avec curl et gérer la progression

				// fOutputStream.write(RetrieverUtils.getUrlContentBytes(RetrieverUtils.getUrlContent(TF1Retreiver.buildUrlVideoInfo(mediaId,
				// "web"))));
				// Affichage de la progression
				final int newP = handleProgression(videoStruct.getMediaIdList().size(), i, old);
				if (newP != old) {
					progressionListener.listen(String.valueOf(newP));
					old = newP;
					// LOGGER.debug("Avancement : " + newP + " %");
				}
				i++;
			} catch (final IOException e) {
				throw new TechnicalException(e);
			} finally {
				if (fOutputStream != null) {
					try {
						fOutputStream.flush();
						fOutputStream.close();
					} catch (final IOException e) {
						// LOGGER.error("", e);
					}
				}
			}
			// to TS
			final String tmpVideoFileTs = tmpVideoFile + ".ts";
			(new File(tmpVideoFileTs)).delete();
			try {
				new CmdExecutor(null, String.format("%s -i %s -c copy -y -bsf:v h264_mp4toannexb -f mpegts %s", assembler, tmpVideoFile, tmpVideoFileTs), 2000,
						null).execute();
			} catch (final ExecutorFailedException e) {
				throw new TechnicalException(e);
			}
			(new File(tmpVideoFile)).delete();
			tsList.append(tmpVideoFileTs + "|");
			tsFilesList.add(tmpVideoFileTs);
		}

		// corriger fichier video ffmpeg -isync -i test.avi -c copy test2.avi
		// ffmpeg -isync -i "concat:file-01.mpeg.ts|file-02.mpeg.ts" -f mpeg
		try {
			// fmpeg veut l'extension .avi
			final String downloadOuputAvi = downloadOutput + ".avi";
			new CmdExecutor(null, String.format("%s -isync -i \"concat:%s\" -c copy %s", assembler, tsList.toString(), downloadOuputAvi), 2000, null).execute();
			for (final String tsFile : tsFilesList) {
				(new File(tsFile)).delete();
			}
			(new File(downloadOuputAvi)).renameTo(new File(downloadOutput));
		} catch (final ExecutorFailedException e) {
			throw new TechnicalException(e);
		}
	}

	private static final Pattern MEDIAID_PATTERN = Pattern.compile("mediaId : (\\d*),");

	private static final Pattern VIDEO_ID_PATTERN = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");

	private static final Pattern FILES_PATTERN = Pattern.compile(".*\"files\"\\s*:\\s*\\[(.*)\\].*");

	static String buildToken(final String id, final String timestamp, final String contextRoot) {
		// my $hexdate = sprintf("%x",time());
		// fill up triling zeroes
		// final String dateS = String.format("%x", timestamp);
		final String dateS = Long.toHexString(Long.valueOf(timestamp)).toLowerCase();
		final StringBuilder dateSC = new StringBuilder(dateS);
		for (int i = 0; i < (dateS.length() - 8); i++) {
			dateSC.append("0");
		}
		// $hexdate .= "0" x (length($hexdate) - 8);
		final String key = "9b673b13fa4682ed14c3cfa5af5310274b514c4133e9b3a81e6e3aba009l2564";
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest((key + "/" + contextRoot + "/" + id + dateSC.toString()).getBytes());
		} catch (final NoSuchAlgorithmException e) {
			throw new TechnicalException(e);
		}
		final StringBuilder hashString = new StringBuilder();
		for (int i = 0; i < hash.length; i++) {
			final String hex = Integer.toHexString(hash[i]);
			if (hex.length() == 1) {
				hashString.append('0');
				hashString.append(hex.charAt(hex.length() - 1));
			} else {
				hashString.append(hex.substring(hex.length() - 2));
			}
		}
		return hashString.toString() + "/" + dateS;
	}

	public VideoStruct findFinalUrl(final EpisodeDTO episode) {
		final String content = getUrlContent(TF1Conf.HOME_URL + episode.getUrl());
		final String mainMediaId = findMediaId(content);
		final String videoInfoContent = getUrlContent(TF1Conf.VIDEO_INFO + mainMediaId);
		final boolean hd = videoInfoContent.contains("\"hasHD\":true");
		final Collection<String> mediaIdList = findMediaIdList(videoInfoContent);
		return new VideoStruct(hd, mediaIdList);
	}

	private static Collection<String> findMediaIdList(final String content) {
		// find fragment "files"
		Matcher matcher = FILES_PATTERN.matcher(content);
		final boolean hasMatched = matcher.find();
		String files = null;
		final Set<String> fragIdList = new HashSet<>();
		if (hasMatched) {
			files = matcher.group(matcher.groupCount());
			matcher = VIDEO_ID_PATTERN.matcher(files);
			while (matcher.find()) {
				fragIdList.add(matcher.group().split(":")[1]);
			}
		} else {
			throw new TechnicalException("can't find mediaId");
		}

		// find all files
		final List<String> mediaIdList = new ArrayList<>();
		matcher = VIDEO_ID_PATTERN.matcher(content);
		while (matcher.find()) {
			mediaIdList.add(matcher.group().split(":")[1]);
		}

		if (fragIdList.isEmpty()) {
			return mediaIdList;
		}

		return fragIdList;
	}

	public String buildUrlVideoInfo(final String mediaId, final String contextRoot) {
		return TF1Conf.WAT_HOME + "/get/" + contextRoot + "/" + mediaId + "?token=" + buildToken(mediaId, findTimeStamp(), contextRoot)
				+ "&country=FR&getURL=1&version=WIN 11,5,502,146";
	}

	private String findTimeStamp() {
		final String content = getUrlContent(TF1Conf.WAT_HOME + "/servertime");
		return content.split("\\|")[0];
	}

	private static String findMediaId(final String content) {
		final Matcher matcher = MEDIAID_PATTERN.matcher(content);
		final boolean hasMatched = matcher.find();
		String ret = null;
		if (hasMatched) {
			ret = matcher.group(matcher.groupCount());
		} else {
			throw new TechnicalException("can't find mediaId");
		}
		return ret;
	}
}
