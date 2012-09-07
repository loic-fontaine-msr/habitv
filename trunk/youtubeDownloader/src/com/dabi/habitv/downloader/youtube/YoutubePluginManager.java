package com.dabi.habitv.downloader.youtube;

import java.util.Map;

import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class YoutubePluginManager implements PluginDownloaderInterface {

	@Override
	public String getName() {
		return YoutubeConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		// no need
	}

	@Override
	public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters,
			final CmdProgressionListener listener) throws DownloadFailedException {
		final String videoId = YoutubeDownloader.getYoutubeId(downloadInput);
		final int format = YoutubeDownloader.findBestFormat(downloadInput);
		YoutubeDownloader.download(videoId, format, downloadDestination,listener);
	}

}
