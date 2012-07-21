package com.dabi.habitv.framework.plugin.api.dto;

import java.util.Map;

import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;

public final class DownloaderDTO {

	private final Map<String, PluginDownloaderInterface> downloaderName2downloader;

	private final Map<String, String> downloaderName2BinPath;

	private final String downloadOutputDir;

	private final String indexDir;

	public DownloaderDTO(final Map<String, PluginDownloaderInterface> downloaderName2downloader, final Map<String, String> downloaderName2BinPath,
			final String downloadOutputDir, final String indexDir) {
		super();
		this.downloaderName2downloader = downloaderName2downloader;
		this.downloaderName2BinPath = downloaderName2BinPath;
		this.downloadOutputDir = downloadOutputDir;
		this.indexDir = indexDir;
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

	public String getDownloadOutputDir() {
		return downloadOutputDir;
	}

	public String getIndexDir() {
		return indexDir;
	}

}
