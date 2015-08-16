package com.dabi.habitv.provider.arte;

interface ArteConf {

	String NAME = "arte";

	String CAT_PAGE = "http://www.arte.tv/guide/fr/plus7";

	String ID_EMISSION_TOKEN = "#ID_EMISSION#";

	String RSS_CATEGORY_URL = "http://videos.arte.tv/fr/do_delegate/videos/programmes/"
			+ ID_EMISSION_TOKEN + ",view,rss.xml";

	String ID_EPISODE_TOKEN = "#ID_EPISODE#";

	String RTMPDUMP_CMD = "-r \"#VIDEO_URL#\" -c 1935 -m 10 -o \"#FILE_DEST#\"";

	String HOME_URL = "http://www.arte.tv";

}
