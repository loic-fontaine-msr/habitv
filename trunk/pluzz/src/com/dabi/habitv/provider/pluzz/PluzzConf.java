package com.dabi.habitv.provider.pluzz;

public interface PluzzConf {

	String NAME = "pluzz";

	String EXTENSION = "avi";

	String ASSEMBLE_CMD = "%s -isync -i \"concat:%s\" -c copy %s";

	String ZIP_URL = "http://webservices.francetelevisions.fr/catchup/flux/flux_main.zip";

	long MAX_CACHE_ARCHIVE_TIME_MS = 60000L;

	String ASSEMBLER = "ffmpeg";

	String BASE_URL = "http://medias2.francetv.fr/catchup-mobile";
}
