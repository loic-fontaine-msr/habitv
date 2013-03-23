package com.dabi.habitv.provider.tmc;

public interface TMCConf {

	String NAME = "tmc";

	String HOME_URL = "http://www.tmc.tv";

	String VIDEO_URL = "http://videos.tmc.tv";

	String CURL = "curl";

	String RTMDUMP = "rtmpdump";

	String RTMPDUMP_PREFIX = "rtmp";

	String DUMP_CMD="-r \"#VIDEO_URL#\" -c 1935 -m 10 -w a502adc490f7e313b42045ac24449ca3c6420e485c1f4fddc924678fa5f41ba9 -x 351945 -o \"#FILE_DEST#\"";

	String EXTENSION = "flv";

	String WAT_HOME = "http://www.wat.tv";

	String VIDEO_INFO = "http://www.wat.tv/video/";

	String CORRECT_VIDEO_CMD = "%s -isync -i %s -c copy %s";

	String ASSEMBLER = "ffmpeg";
}
