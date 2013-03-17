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

	private final Map<ProxyDTO.ProtocolEnum, ProxyDTO> protocol2proxy;

	public DownloaderDTO(final String cmdProcessor, final Map<String, PluginDownloaderInterface> downloaderName2downloader,
			final Map<String, String> downloaderName2BinPath, final String downloadOutputDir, final String indexDir, final Map<ProxyDTO.ProtocolEnum, ProxyDTO> protocol2proxy) {
		super();
		this.cmdProcessor = cmdProcessor;
		this.downloaderName2downloader = downloaderName2downloader;
		this.downloaderName2BinPath = downloaderName2BinPath;
		this.downloadOutput = downloadOutputDir;
		this.indexDir = indexDir;
		this.protocol2proxy = protocol2proxy;
	}

	public DownloaderDTO(final String cmdProcessor, final Map<String, PluginDownloaderInterface> downloaderName2downloader,
			final Map<String, String> downloaderName2BinPath, final String downloadOutputDir, final String indexDir) {
		this(cmdProcessor, downloaderName2downloader, downloaderName2BinPath, downloadOutputDir, indexDir, null);
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

	public Map<ProxyDTO.ProtocolEnum, ProxyDTO> getProtocol2proxy() {
		return protocol2proxy;
	}

}
