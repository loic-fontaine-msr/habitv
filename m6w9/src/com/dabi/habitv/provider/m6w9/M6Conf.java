package com.dabi.habitv.provider.m6w9;

public final class M6Conf {

	private M6Conf() {

	}

	public static final String NAME = "m6";
	public static final String ENCRYPTION = "DES";
	public static final String CATALOG_URL = "http://www.m6replay.fr/catalogue/catalogueWeb3.xml";
	public static final String SECRET_KEY = "ElFsg.Ot";
	public static final String DUMP_CMD = "-r \"rtmpe://groupemsix.fcod.llnwd.net/a2883/e1/#VIDEO_URL#?#TOKEN#\" -c 1935 -m 10 -o \"#FILE_DEST#\"";

}
