package com.dabi.habitv.provider.m6w9;

public final class W9Conf {

	private W9Conf() {

	}

	public static final String ENCRYPTION = "DES";
	public static final String NAME = "w9";
	// start URL www.w9replay.fr/files/w9configuration_2.8.10.2_lv3_rtmp.xml
	public static final String CATALOG_URL = "http://www.w9replay.fr/catalogue/4398.xml";
	public static final String PACKAGE_NAME = "com.dabi.habitv.provider.m6.entities"; 
	public static final String DUMP_CMD = "-r \"rtmpe://groupemsix.fcod.llnwd.net/a2883/e1/#VIDEO_URL#?#TOKEN#\" -c 1935 -m 10 -o \"#FILE_DEST#\"";

}
