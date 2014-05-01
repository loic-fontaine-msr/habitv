package com.dabi.habitv.plugin.ffmpeg;

import com.dabi.habitv.framework.FrameworkConf;

public final class FFMPEGConf {

	private FFMPEGConf() {

	}

	public static final String NAME = "ffmpeg";

	public static final long MAX_HUNG_TIME = 100000L;

	public static final String FFMPEG_CMD = " -i \""+FrameworkConf.DOWNLOAD_INPUT+"\" -c copy -absf aac_adtstoasc -y -f "+FrameworkConf.EXTENSION+" \"" + FrameworkConf.DOWNLOAD_DESTINATION + "\" ";

	public static final String DEFAULT_WINDOWS_BIN_PATH = "bin\\ffmpeg.exe";

	public static final String DEFAULT_LINUX_BIN_PATH = "ffmpeg";

}
