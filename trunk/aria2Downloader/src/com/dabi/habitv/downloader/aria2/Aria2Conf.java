package com.dabi.habitv.downloader.aria2;

public final class Aria2Conf {

	private Aria2Conf() {

	}

	public static final String NAME = "aria2";

	public static final String CMD2 = "--check-certificate=false --seed-time=0 --enable-dht --allow-overwrite=true --dht-entry-point=dht.transmissionbt.com:6881 --dht-listen-port=6881 --disable-ipv6 \"#VIDEO_URL#\" --dir=\"#DEST_DIR#\" --out=\"#FILE_NAME#\"";

	public static final String FILE_NAME = "#FILE_NAME#";

	public static final String DIR_DEST = "#DEST_DIR#";

}
