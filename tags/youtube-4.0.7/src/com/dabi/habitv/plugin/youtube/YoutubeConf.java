package com.dabi.habitv.plugin.youtube;

public final class YoutubeConf {

	private YoutubeConf() {

	}

	public static final String NAME = "youtube";
	public static final String ENCODING = "UTF-8";
	public static final String DUMP_CMD = " \"#VIDEO_URL#\" -o \"#FILE_DEST#\"  --write-sub --write-auto-sub";
	public static final long MAX_HUNG_TIME = 300000L;
	public static final String DEFAULT_WINDOWS_EXE = "youtube-dl.exe";
	public static final String DEFAULT_LINUX_BIN_PATH = "youtube-dl";

}
