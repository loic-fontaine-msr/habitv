package com.dabi.habitv.provider.canalplus;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.utils.DownloadUtils;

public class CanalPlusPluginDownloader extends BasePluginWithProxy implements PluginDownloaderInterface { // NO_UCD

	@Override
	public void download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders, final CmdProgressionListener listener)
			throws DownloadFailedException {
		DownloadUtils.download(downloadParam, downloaders, listener);
		// FIXME se baser sur des urls canalPlus
	}

	@Override
	public String getName() {
		return CanalPlusConf.NAME;
	}

}
