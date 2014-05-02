package com.dabi.habitv.framework.plugin.utils;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.framework.FrameworkConf;

public class DownloadUtils {

	public static void download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders, final CmdProgressionListener listener)
			throws DownloadFailedException {
		String downloaderName = downloadParam.getParam(FrameworkConf.DOWNLOADER_PARAM);
		if (downloaderName == null) {
			downloaderName = findDownloaderByUrl(downloadParam.getDownloadInput());
		}
		download(downloadParam, downloaders, listener, downloaderName);
	}

	public static void download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders, final CmdProgressionListener listener,
			final String downloaderName) throws DownloadFailedException {
		final PluginDownloaderInterface pluginDownloader = downloaders.getPlugin(downloaderName);
		pluginDownloader.download(downloadParam, downloaders, listener);
	}

	private static String findDownloaderByUrl(final String url) {
		String downloaderName;
		if (isRTMPDownloadable(url)) {
			downloaderName = FrameworkConf.RTMDUMP;
		} else if (isFFMPEGDownloadable(url)) {
			downloaderName = FrameworkConf.FFMPEG;
		} else {
			downloaderName = FrameworkConf.CURL;
		}
		return downloaderName;
	}

	public static boolean isFFMPEGDownloadable(final String url) {
		return url.contains(FrameworkConf.M3U8);
	}

	public static boolean isRTMPDownloadable(final String url) {
		return url.startsWith(FrameworkConf.RTMPDUMP_PREFIX);
	}
}
