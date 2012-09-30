package com.dabi.habitv.framework.plugin.api.dto;

import java.util.Map;

import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;

public final class DownloaderDTO {

	private final Map<String, PluginDownloaderInterface> downloaderName2downloader;

	private final Map<String, String> downloaderName2BinPath;

	private final String downloadOutput;

	private final String indexDir;

	private final String cmdProcessor;

	public DownloaderDTO(final String cmdProcessor, final Map<String, PluginDownloaderInterface> downloaderName2downloader,
			final Map<String, String> downloaderName2BinPath, final String downloadOutputDir, final String indexDir) {
		super();
		this.cmdProcessor = cmdProcessor;
		this.downloaderName2downloader = downloaderName2downloader;
		this.downloaderName2BinPath = downloaderName2BinPath;
		this.downloadOutput = downloadOutputDir;
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

	public String getDownloadOutput() {
		return downloadOutput;
	}

	public String getIndexDir() {
		return indexDir;
	}

	public String getCmdProcessor() {
		return cmdProcessor;
	}
}
