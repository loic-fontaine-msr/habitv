package com.dabi.habitv.provider.lequipe;

import com.dabi.habitv.framework.FrameworkConf;

public interface LEquipeConf {

	String NAME = "lequipe";

	String VIDEO_HOME_URL = "https://video.lequipe.fr";

	String VIDEOS_URL = "http://video.lequipe.fr/morevideos";

	String EXTENSION = FrameworkConf.MP4;

	String USER_AGENT = "\"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)\"";

	int MAX_PAGE = 5;
}
