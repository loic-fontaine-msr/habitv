package com.dabi.habitv.provider.arte;

interface ArteConf {

	String NAME = "arte";

	String RSS_PAGE = "http://videos.arte.tv/fr/videos/meta/index--3188674--3223978.html";

	String ID_EMISSION_TOKEN = "#ID_EMISSION#";

	String RSS_CATEGORY_URL = "http://videos.arte.tv/fr/do_delegate/videos/programmes/"
			+ ID_EMISSION_TOKEN + ",view,rss.xml";

	String ID_EPISODE_TOKEN = "#ID_EPISODE#";

	String RTMPDUMP_CMD = "-r \"#VIDEO_URL#\" -c 1935 -m 10 -o \"#FILE_DEST#\"";

}
