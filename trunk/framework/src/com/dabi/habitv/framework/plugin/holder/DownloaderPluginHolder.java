package com.dabi.habitv.framework.plugin.holder;

import java.util.Map;

import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;

public final class DownloaderPluginHolder extends AbstractPluginHolder<PluginDownloaderInterface> {

	private final Map<String, String> downloaderName2BinPath;

	private final String downloadOutput;

	private final String indexDir;

	private final String cmdProcessor;

	public DownloaderPluginHolder(final String cmdProcessor, final Map<String, PluginDownloaderInterface> downloaderName2downloader,
			final Map<String, String> downloaderName2BinPath, final String downloadOutputDir, final String indexDir) {
		super(downloaderName2downloader);
		this.cmdProcessor = cmdProcessor;
		this.downloaderName2BinPath = downloaderName2BinPath;
		this.downloadOutput = downloadOutputDir;
		this.indexDir = indexDir;
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
