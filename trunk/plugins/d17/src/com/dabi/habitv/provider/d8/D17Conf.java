package com.dabi.habitv.provider.d8;

import com.dabi.habitv.framework.FrameworkConf;

interface D17Conf {

	String NAME = "d17";

	String EXTENSION = FrameworkConf.MP4;

	String CATALOG_URL = "http://www.d17.tv/index.php/api/applicationv2/flux/replays/theme/%s";

	String PROGRAM_URL = "http://www.d17.tv/pid5316-d17-musique.html?cat=%s";

	String PROGRAM_API_URL = "http://www.d17.tv/index.php/api/applicationv2/flux/programme/id/%s";

	int ROOT_CATEGORY_SIZE = 9;

	String HOME_URL = "http://www.d17.tv";

	String VIDEO_INFO_URL = "http://service.canal-plus.com/video/rest/getVideosLiees/d17/";

	String ENCODING = "ISO-8859-1";

}
