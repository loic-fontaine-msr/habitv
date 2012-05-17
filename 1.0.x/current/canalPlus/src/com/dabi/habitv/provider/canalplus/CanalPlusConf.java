package com.dabi.habitv.provider.canalplus;

public final class CanalPlusConf {

	private CanalPlusConf() {

	}

	public static final String NAME = "canalPlus";

	public static final String INITPLAYER_URL = "http://service.canal-plus.com/video/rest/initPlayer/cplus";

	public static final String MEA_URL = "http://service.canal-plus.com/video/rest/getMEAs/cplus/";

	public static final String VIDEO_URL = "http://service.canal-plus.com/video/rest/getVideosLiees/cplus/";

	public static final String MEA_PACKAGE_NAME = "com.dabi.habitv.provider.canalplus.mea.entities";

	public static final String VIDEO_PACKAGE_NAME = "com.dabi.habitv.provider.canalplus.video.entities";

	public static final String INITPLAYER_PACKAGE_NAME = "com.dabi.habitv.provider.canalplus.initplayer.entities";

	public static final String RTMDUMP = "rtmpdump";

	public static final String RTMPDUMP_PREFIX = "rtmp:";

	public static final String CURL = "curl";

}
