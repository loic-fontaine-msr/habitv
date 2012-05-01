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

	public static final String DUMP_CMD = "-r \"#VIDEO_URL#\" -o \"#FILE_DEST#\"";

	public static final String RTMDUMP = "rtmpdump";

	public static final String RTMPDUMP_PREFIX = "rtmp:";

	public static final String CURL = "curl";

	public static final String CURL_CMD = " \"#VIDEO_URL#\"  -C - -L -g -A \"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)\" -o \"#FILE_DEST#\" ";

}
