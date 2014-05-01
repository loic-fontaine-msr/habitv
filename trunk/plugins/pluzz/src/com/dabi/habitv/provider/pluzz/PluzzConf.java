package com.dabi.habitv.provider.pluzz;

public interface PluzzConf {

	String NAME = "pluzz";

	String EXT = "mp4";

	String CORRECT_VIDEO_CMD_MP4 = "%s -isync -i %s -c copy -absf aac_adtstoasc -f mp4 -y %s";

	String ZIP_URL = "http://webservices.francetelevisions.fr/catchup/flux/flux_main.zip";

	long MAX_CACHE_ARCHIVE_TIME_MS = 60000L;

	String FFMPEG = "ffmpeg";

	String BASE_URL = "http://medias2.francetv.fr/catchup-mobile";

}
