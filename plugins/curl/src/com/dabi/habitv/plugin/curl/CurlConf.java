package com.dabi.habitv.plugin.curl;

import com.dabi.habitv.framework.FrameworkConf;

public final class CurlConf {

	private CurlConf() {

	}

	public static final String NAME = "curl";

	public static final String CURL_CMD = " \"" + FrameworkConf.DOWNLOAD_INPUT
			+ "\"  -C - -L -g -A \"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)\" -o \"" + FrameworkConf.DOWNLOAD_DESTINATION + "\" ";

	public static final long MAX_HUNG_TIME = 100000L;

	public static final String DEFAULT_WINDOWS_BIN_PATH = "bin\\curl.exe";

	public static final String DEFAULT_LINUX_BIN_PATH = "curl";

}
