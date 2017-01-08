package com.dabi.habitv.provider.sfr;

import com.dabi.habitv.framework.FrameworkConf;

public interface SFRConf {

	String NAME = "sfr";

	String HOME_URL = "https://sport.sfr.fr";

	String EXTENSION = FrameworkConf.MP4;

	String USER_AGENT = "\"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)\"";

	int MAX_PAGE = 5;

	String VIDEOS_URL = "https://sport.sfr.fr/api/rest/tracking/published/category/%s/?page=%s";
}
