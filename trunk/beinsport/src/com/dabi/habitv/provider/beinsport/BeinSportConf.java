package com.dabi.habitv.provider.beinsport;

interface BeinSportConf {

	String NAME = "beinsport";

	String EXTENSION = "mp4";

	String CURL = "curl";

	String RTMDUMP = "rtmpdump";

	String PACKAGE_NAME = "com.dabi.habitv.provider.beinsport";

	String CATALOG_URL = "http://fr.beinappfeeds.performgroup.com/api/v1_1/videos.xml";

	String VIDEOS_CATEGORY = "video";

	String REPLAY_CATEGORY = "replay";

	String REPLAY_URL = "http://www.beinsport.fr/replay";

	String HOME_URL = "http://www.beinsport.fr/";

	String RTMPDUMP_CMD2="-r \"#PROTOCOL#//#HOST#/#CONTEXT_ROOT#\" -a \"#CONTEXT_ROOT#\" -f \"WIN 11,3,300,265\" -W \"http://www.beinsport.fr/ptvFlash/unifiedplayer/adaptive_vod/UnifiedPlayer.swf\" -y \"mp4:#VIDEO_URL#\" -o \"#FILE_DEST#\"";

	String XML_INFO = "http://www.beinsport.fr/vodConfig.xml/videoId/";

}
