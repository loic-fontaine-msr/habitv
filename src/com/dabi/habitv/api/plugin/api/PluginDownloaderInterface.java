package com.dabi.habitv.api.plugin.api;

import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;

public interface PluginDownloaderInterface extends PluginBaseInterface {

	public enum DownloadableState {
		IMPOSSIBLE, POSSIBLE, SPECIFIC;
	}

	DownloadableState canDownload(String downloadInput);

	ProcessHolder download(final DownloadParamDTO downloadParam,
			final DownloaderPluginHolder downloaders)
			throws DownloadFailedException;

}
