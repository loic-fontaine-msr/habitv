package com.dabi.habitv.plugintester;

import org.apache.log4j.Logger;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;

public class MockDownloader implements PluginDownloaderInterface {

	private static final Logger LOG = Logger.getLogger(MockDownloader.class);

	@Override
	public String getName() {
		return MockDownloader.class.getSimpleName();
	}

	@Override
	public DownloadableState canDownload(final String downloadInput) {
		return DownloadableState.SPECIFIC;
	}

	@Override
	public void download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders, final CmdProgressionListener listener) throws DownloadFailedException {
		LOG.info("download of " + downloadParam + " ");
	}

}
