package com.dabi.habitv.downloader.curl;

import com.dabi.habitv.framework.FrameworkConf;

public final class CurlConf {

	private CurlConf() {

	}

	public static final String NAME = "curl";

	public static final String CURL_CMD = " \"" + FrameworkConf.DOWNLOAD_INPUT
			+ "\"  -C - -L -g -A \"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)\" -o \"" + FrameworkConf.DOWNLOAD_DESTINATION + "\" ";

}
