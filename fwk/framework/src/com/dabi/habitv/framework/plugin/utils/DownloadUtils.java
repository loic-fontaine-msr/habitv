package com.dabi.habitv.framework.plugin.utils;

import java.util.LinkedList;
import java.util.List;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.api.plugin.api.PluginDownloaderInterface.DownloadableState;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.framework.FrameworkConf;

public class DownloadUtils {

	public static void download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders, final CmdProgressionListener listener)
			throws DownloadFailedException {
		final String downloaderName = downloadParam.getParam(FrameworkConf.DOWNLOADER_PARAM);
		if (downloaderName == null) {
			final PluginDownloaderInterface downloader = findDownloaderByUrl(downloaders, downloadParam.getDownloadInput());
			downloader.download(downloadParam, downloaders, listener);
		} else {
			download(downloadParam, downloaders, listener, downloaderName);
		}
	}

	public static void download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders, final CmdProgressionListener listener,
			final String downloaderName) throws DownloadFailedException {
		final PluginDownloaderInterface pluginDownloader = downloaders.getPlugin(downloaderName);
		pluginDownloader.download(downloadParam, downloaders, listener);
	}

	private static PluginDownloaderInterface findDownloaderByUrl(final DownloaderPluginHolder downloaders, final String url) {
		final List<PluginDownloaderInterface> possibleDownloaders = new LinkedList<>();
		for (final PluginDownloaderInterface downloader : downloaders.getPlugins()) {
			final DownloadableState downloadableState = downloader.canDownload(url);
			switch (downloadableState) {
			case SPECIFIC:
				return downloader;
			case POSSIBLE:
				possibleDownloaders.add(downloader);
				break;
			default:
				break;
			}
		}

		if (possibleDownloaders.isEmpty()) {
			return downloaders.getPlugin(FrameworkConf.DEFAULT_DOWNLOADER); // FIXME
			// en
			// conf
		}

		return possibleDownloaders.iterator().next();
	}

}
