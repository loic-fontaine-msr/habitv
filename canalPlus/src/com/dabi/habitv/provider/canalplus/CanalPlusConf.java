package com.dabi.habitv.provider.canalplus;

interface CanalPlusConf {

	String NAME = "canalPlus";

	String INITPLAYER_URL = "http://service.canal-plus.com/video/rest/initPlayer/cplus";

	String MEA_URL = "http://service.canal-plus.com/video/rest/getMEAs/cplus/";

	String VIDEO_URL = "http://service.canal-plus.com/video/rest/getVideosLiees/cplus/";

	String MEA_PACKAGE_NAME = "com.dabi.habitv.provider.canalplus.mea.entities";

	String VIDEO_PACKAGE_NAME = "com.dabi.habitv.provider.canalplus.video.entities";

	String INITPLAYER_PACKAGE_NAME = "com.dabi.habitv.provider.canalplus.initplayer.entities";

	String RTMDUMP = "rtmpdump";

	String RTMPDUMP_PREFIX = "rtmp:";

	String CURL = "curl";

	String EXTENSION = "mp4";

}
