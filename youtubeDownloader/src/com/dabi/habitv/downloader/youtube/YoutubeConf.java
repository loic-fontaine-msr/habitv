package com.dabi.habitv.downloader.youtube;

public final class YoutubeConf {

	private YoutubeConf() {

	}

	public static final String NAME = "youtube";
	public static final String ENCODING = "UTF-8";
	public static final String DUMP_CMD = " \"#VIDEO_URL#\" -o \"#FILE_DEST#\"";
	public static final long MAX_HUNG_TIME = 300000L;

}
