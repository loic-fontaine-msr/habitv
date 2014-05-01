package com.dabi.habitv.framework.plugin.api;

import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public interface PluginDownloaderInterface extends PluginBaseInterface {

	void download(final String downloadInput, final String downloadOuput, final DownloaderPluginHolder downloaders, final CmdProgressionListener listener)
			throws DownloadFailedException;

}
