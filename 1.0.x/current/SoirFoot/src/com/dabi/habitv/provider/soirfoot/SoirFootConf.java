package com.dabi.habitv.provider.soirfoot;

import com.dabi.habitv.framework.plugin.utils.FrameworkConf;

public final class SoirFootConf {

	private SoirFootConf() {

	}

	public static final String NAME = "soirFoot";
	public static final String HOME_URL = "http://www.soirfoot.com";
	public static final String JUSTIN_API_URL = "http://api.justin.tv/api/broadcast/by_archive/";
	public static final String RUTUBE_API_URL = "http://bl.rutube.ru/#ID#.f4m";
	public static final String RTMDUMP = "rtmpdump";
	public static final String RTMPDUMP_PREFIX = "rtmp:";
	public static final String ARIA2 = "aria2";
	public static final String BASE_URL_APP = "#BASE_URL_APP#";
	public static final String APP = "#APP#";
	public static final String MP4_URL = "#MP4_URL#";
	public static final String RTMP_DUMP_CMD = " -r \"" + BASE_URL_APP + "\" -a \"" + APP
			+ "/\" -f \"LNX 10,3,162,29\" -W \"http://rutube.ru/player.swf\" -p \"http://rutube.ru/tracks/4322127.html\" -y \"" + MP4_URL + "\" -o \""
			+ FrameworkConf.DOWNLOAD_DESTINATION + "\"";
}
