package com.dabi.habitv.downloader.aria2;

interface Aria2Conf {

	String NAME = "aria2";

	String CMD = "--timeout=600 --check-certificate=false --seed-time=0 --enable-dht --allow-overwrite=true --dht-entry-point=dht.transmissionbt.com:6881 --dht-listen-port=6881 --disable-ipv6 \"#VIDEO_URL#\" --dir=\"#DEST_DIR#\" --out=\"#FILE_NAME#\"";

	String FILE_NAME = "#FILE_NAME#";

	String DIR_DEST = "#DEST_DIR#";

	long MAX_HUNG_TIME = 360000L;
}
