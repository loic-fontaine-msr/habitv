package com.dabi.habitv.downloader.ffmpeg;

import com.dabi.habitv.framework.FrameworkConf;

public final class FFMPEGConf {

	private FFMPEGConf() {

	}

	public static final String NAME = "ffmpeg";

	public static final long MAX_HUNG_TIME = 100000L;

	public static final String FFMPEG_CMD = " -i \""+FrameworkConf.DOWNLOAD_INPUT+"\" -c copy -absf aac_adtstoasc -f -y "+FrameworkConf.EXTENSION+" \"" + FrameworkConf.DOWNLOAD_DESTINATION + "\" ";

}
