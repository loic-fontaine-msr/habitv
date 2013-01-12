package com.dabi.habitv.framework;

public interface FrameworkConf {

	String DOWNLOAD_INPUT = "#VIDEO_URL#";

	String DOWNLOAD_DESTINATION = "#FILE_DEST#";

	String PARAMETER_BIN_PATH = "BIN_PATH";

	String PARAMETER_ARGS = "ARGUMENTS";

	int MIN_SIZE = 0;

	long TIME_BETWEEN_LOG = 2000L;

	long HUNG_PROCESS_TIME = 180000L;

	String UPDATE_URL = "http://dabiboo.free.fr/habitv";

	String CMD_PROCESSOR = "CMD_PROCESSOR";

	Integer TIME_OUT_MS = 15000;
}
