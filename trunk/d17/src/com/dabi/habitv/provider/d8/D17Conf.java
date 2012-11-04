package com.dabi.habitv.provider.d8;

interface D17Conf {

	String NAME = "d17";

	String EXTENSION = "mp4";

	String CURL = "curl";

	String CATALOG_URL = "http://www.d17.tv/index.php/api/applicationv2/flux/replays/theme/%s";
	
	String PROGRAM_URL = "http://www.d17.tv/replay/index/filter/program/id/%s";
	
	String PROGRAM_API_URL = "http://www.d17.tv/index.php/api/applicationv2/flux/programme/id/%s";

	int ROOT_CATEGORY_SIZE = 9;
}
