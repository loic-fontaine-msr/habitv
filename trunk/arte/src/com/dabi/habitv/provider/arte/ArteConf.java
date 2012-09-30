package com.dabi.habitv.provider.arte;

interface ArteConf {

	String NAME = "arte";

	String EXTENSION = "mp4";

	String RTMPDUMP = "rtmpdump";

	String RSS_PAGE = "http://videos.arte.tv/fr/videos/meta/index--3188674--3223978.html";

	String ID_EMISSION_TOKEN = "#ID_EMISSION#";

	String RSS_CATEGORY_URL = "http://videos.arte.tv/fr/do_delegate/videos/programmes/" + ID_EMISSION_TOKEN + ",view,rss.xml";

	String ID_EPISODE_TOKEN = "#ID_EPISODE#";

	String XML_INFO_URL = "http://videos.arte.tv/fr/do_delegate/videos/" + ID_EPISODE_TOKEN + ",view,asPlayerXml.xml";
}
