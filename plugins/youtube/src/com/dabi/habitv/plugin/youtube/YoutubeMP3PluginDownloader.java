package com.dabi.habitv.plugin.youtube;

import com.dabi.habitv.api.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.utils.DownloadUtils;

public class YoutubeMP3PluginDownloader implements PluginDownloaderInterface {

	@Override
	public String getName() {
		return YoutubeConf.NAME_MP3;
	}

	@Override
	public ProcessHolder download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders) throws DownloadFailedException {
		DownloadParamDTO downloadParamMp3 = new DownloadParamDTO(downloadParam.getDownloadInput().replace("mp3:", ""),
		        downloadParam.getDownloadOutput(), "mp3");
		downloadParamMp3.getParams().putAll(downloadParam.getParams());
		downloadParamMp3.addParam(FrameworkConf.PARAMETER_ARGS, YoutubeConf.DUMP_CMD_MP3);
		return DownloadUtils.download(downloadParamMp3, downloaders, YoutubeConf.NAME);
	}

	@Override
	public DownloadableState canDownload(final String downloadInput) {
		if (downloadInput.startsWith("mp3:")) {
			return DownloadableState.SPECIFIC;
		} else {
			return DownloadableState.IMPOSSIBLE;
		}
	}

}
