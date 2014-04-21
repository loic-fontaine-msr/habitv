package com.dabi.habitv.provider.m6w9;

public interface M6W9Conf {

	String NAME = "m6w9";
	String RTMDUMP = "rtmpdump";
	String EXTENSION = "mp4";
	String AGENT = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)";

	int DELAY = 86400;
	String LIMELIGHT_APPLICATION_NAME = "/a2883/e1/";
	String LIMELIGHT_SECRET_KEY = "vw8kuo85j2xMc";

	String M6_NAME = "m6";
	String M6_URL_NAME = "m6replay";
	String W9_NAME = "w9";
	String W9_URL_NAME = "w9replay";
	String SIXTER_NAME = "6ter";
	String SIXTER_URL_NAME = "6terreplay";
	String CATALOG_URL = "http://static.m6replay.fr/catalog/m6group_web/%s/catalogue.json";
	String CLIP_URL = "http://static.m6replay.fr/catalog/m6group_web/%s/clip/%s/%s/clip_infos-%s.json";
	String RTMPDUMP_CMD = "-r \"rtmpe://groupemsix.fcod.llnwd.net/a2883/e1/#VIDEO_URL#?#TOKEN#\" -c 1935 -m 10 -o \"#FILE_DEST#\"";

	long MAX_CACHE_ARCHIVE_TIME_MS = 60000L;
}
