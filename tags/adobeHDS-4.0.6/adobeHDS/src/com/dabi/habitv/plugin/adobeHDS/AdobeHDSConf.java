package com.dabi.habitv.plugin.adobeHDS;

public final class AdobeHDSConf {

	private AdobeHDSConf() {

	}

	public static final String NAME = "adobeHDS";

	public static final String PRE_CMD_ADOBEHDS_EXE="";
	
	public static final String CMD=" --delete --manifest \"#VIDEO_URL#\" --outfile \"#FILE_DEST#\""; 

	public static final long MAX_HUNG_TIME = 300000L;

	public static final long HUNG_PROCESS_TIME = 30000L;

}
