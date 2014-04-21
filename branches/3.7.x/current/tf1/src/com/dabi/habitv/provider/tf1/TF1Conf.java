package com.dabi.habitv.provider.tf1;

public interface TF1Conf {

	String NAME = "tf1";

	String HOME_URL = "http://videos.tf1.fr/";

	String CURL = "curl";

	String RTMDUMP = "rtmpdump";

	String RTMPDUMP_PREFIX = "rtmp";

	String DUMP_CMD="-r \"#VIDEO_URL#\" -c 1935 -m 10 -w 288902587f13ebfd9971092eab9fd78fcc23b400caa9061a73eb87b0f13c6b41 -x 343854 -o \"#FILE_DEST#\"";

	String EXTENSION = "mp4";

	String WAT_HOME = "http://www.wat.tv";

	String VIDEO_INFO = "http://www.wat.tv/interface/contentv3/";

	String CORRECT_VIDEO_CMD = "%s -isync -i %s -c copy %s";

	String ASSEMBLER = "ffmpeg";
}
