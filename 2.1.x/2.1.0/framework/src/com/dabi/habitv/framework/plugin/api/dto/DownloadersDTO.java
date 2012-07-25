package com.dabi.habitv.framework.plugin.api.dto;

import java.util.Map;

import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;

public final class DownloadersDTO {

	private final Map<String, PluginDownloaderInterface> downloaderName2downloader;

	private final Map<String, String> downloaderName2BinPath;

	public DownloadersDTO(final Map<String, PluginDownloaderInterface> downloaderName2downloader, final Map<String, String> downloaderName2BinPath) {
		super();
		this.downloaderName2downloader = downloaderName2downloader;
		this.downloaderName2BinPath = downloaderName2BinPath;
	}

	public PluginDownloaderInterface getDownloader(final String downloaderName) throws NoSuchDownloaderException {
		final PluginDownloaderInterface downloader = downloaderName2downloader.get(downloaderName);
		if (downloader == null) {
			throw new NoSuchDownloaderException(downloaderName);
		}
		return downloader;
	}

	public String getBinPath(final String downloaderName) {
		return downloaderName2BinPath.get(downloaderName);
	}

}
