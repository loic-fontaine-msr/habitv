package com.dabi.habitv.framework;

public interface FrameworkConf {

	String DOWNLOAD_INPUT = "#VIDEO_URL#";

	String DOWNLOAD_DESTINATION = "#FILE_DEST#";

	String PARAMETER_BIN_PATH = "BIN_PATH";

	String PARAMETER_ARGS = "ARGUMENTS";

	long TIME_BETWEEN_LOG = 2000L;

	long HUNG_PROCESS_TIME = 180000L;

	String UPDATE_URL = "http://dabiboo.free.fr/repository";

	String CMD_PROCESSOR = "CMD_PROCESSOR";

	Integer TIME_OUT_MS = 30000;

	String EXTENSION = "#EXTENSION#";

	String GROUP_ID = "com.dabi.habitv";

	String VERSION = "version";

	String RTMDUMP = "rtmpdump";

	String RTMPDUMP_PREFIX = "rtmp:";

	String CURL = "curl";

	String MP4 = "mp4";

	String FFMPEG = "ffmpeg";

	String M3U8 = "m3u8";

	String DOWNLOADER_PARAM = "downloader";

	String UTF8 = "UTF-8";

	String DEFAULT_DOWNLOADER = "curl";

	String HTTP_PREFIX = "http://";

	String FTP_PREFIX = "ftp://";

}
