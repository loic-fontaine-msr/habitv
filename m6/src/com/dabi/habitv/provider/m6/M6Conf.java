package com.dabi.habitv.provider.m6;

public final class M6Conf {
	
	private M6Conf(){
		
	}

	public static final String NAME = "m6";
	public static final String ENCRYPTION = "DES";
	public static final String CATALOG_URL = "http://www.m6replay.fr/catalogue/catalogueWeb3.xml";
	public static final String SECRET_KEY = "ElFsg.Ot";
	public static final String PACKAGE_NAME = "com.dabi.habitv.provider.m6.entities";
	public static final String DUMP_CMD = "-r rtmpe://groupemsix.fcod.llnwd.net/a2883/d1 -a a2883/d1 -f \"LNX 10,0,45,2\" -W http://groupemsix.vo.llnwd.net/o24/u/production/swf/v10_videoPlayer.swf -t rtmpe://95.140.226.59/a2883/d1 -y #VIDEO_URL# -o \"#FILE_DEST#\"";
	public static final String RTMDUMP = "rtmpdump";

}
