package com.dabi.habitv.provider.pluzz;

import com.dabi.habitv.framework.FrameworkConf;

public interface PluzzConf {

	String NAME = "pluzz";

	String EXTENSION = FrameworkConf.MP4;

	String ZIP_URL = "http://webservices.francetelevisions.fr/catchup/flux/flux_main.zip";

	long MAX_CACHE_ARCHIVE_TIME_MS = 60000L;

	String BASE_URL = "http://medias2.francetv.fr/catchup-mobile";

	String WS_JSON = "http://webservices.francetelevisions.fr/tools/getInfosOeuvre/v2/?idDiffusion=%s&catalogue=Pluzz&callback=webserviceCallback_%s";

}
