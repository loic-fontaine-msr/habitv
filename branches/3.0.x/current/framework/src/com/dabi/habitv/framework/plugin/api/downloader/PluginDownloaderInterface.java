package com.dabi.habitv.framework.plugin.api.downloader;

import java.util.Map;

import com.dabi.habitv.framework.plugin.api.PluginBase;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public interface PluginDownloaderInterface extends PluginBase {

	void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters, final CmdProgressionListener listener)
			throws DownloadFailedException;

}
