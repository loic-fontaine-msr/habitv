package com.dabi.habitv.provider.beinsport;

class BeinSportConfCst {

	private interface IBeinSportConf {

		String NAME = "beinsport";

		String EXTENSION = "mp4";

		String CURL = "curl";

		String RTMDUMP = "rtmpdump";

		String VIDEOS_CATEGORY = "video";

		String VIDEOS_URL = "http://www.beinsport.fr/videos/page/1/size/50";

		String REPLAY_CATEGORY = "replay";

		String REPLAY_URL = "http://www.beinsport.fr/replay";

		String HOME_URL = "http://www.beinsport.fr/";

		String RTMPDUMP_CMD2 = "-r \"#PROTOCOL#//#HOST#/#CONTEXT_ROOT#\" -a \"#CONTEXT_ROOT#\" -f \"WIN 11,3,300,265\" -W \"http://www.beinsport.fr/ptvFlash/unifiedplayer/adaptive_vod/UnifiedPlayer.swf\" -y \"mp4:#VIDEO_URL#\" -o \"#FILE_DEST#\"";

		String XML_INFO = "http://www.beinsport.fr/vodConfig.xml/videoId/";
	}

	public final class BeinSportConf implements IBeinSportConf {
		private BeinSportConf() {
		}
	}
}
