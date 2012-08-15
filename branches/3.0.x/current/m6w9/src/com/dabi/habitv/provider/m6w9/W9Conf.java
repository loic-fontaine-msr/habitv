package com.dabi.habitv.provider.m6w9;

public final class W9Conf {

	private W9Conf() {

	}

	public static final String ENCRYPTION = "DES";
	public static final String NAME = "w9";
	// start URL www.w9replay.fr/files/w9configuration_2.8.10.2_lv3_rtmp.xml
	public static final String CATALOG_URL = "http://www.w9replay.fr/catalogue/4398.xml";
	public static final String PACKAGE_NAME = "com.dabi.habitv.provider.m6.entities";
	public static final String DUMP_CMD = "-l 2 -n m6replayfs.fplive.net -a \"m6replaytoken/streaming?#TOKEN#\" -y \"#VIDEO_URL#?#TOKEN#\" --port 1935 --swfhash 9de62bb8db4eccec47f6433381ab0728daec808ca591cf79a5bc9a27647ff356 --swfsize 1854813 -o \"#FILE_DEST#\"";

}
