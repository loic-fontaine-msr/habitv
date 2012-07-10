package com.dabi.habitv.framework.plugin.api;

import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;

public interface PluginDownloaderInterface extends PluginBase {

	void download(final String cmd, final CmdProgressionListener listener) throws DownloadFailedException;

}
