package com.dabi.habitv.provider.canalplus;

import com.dabi.habitv.provider.canalplus.initplayer.entities.INITPLAYER;
import com.dabi.habitv.provider.canalplus.mea.entities.MEA;
import com.dabi.habitv.provider.canalplus.video.entities.VIDEO;

interface CanalPlusConf {

	String NAME = "canalPlus";

	String INITPLAYER_URL = "http://service.canal-plus.com/video/rest/initPlayer/cplus";

	String MEA_URL = "http://service.canal-plus.com/video/rest/getMEAs/cplus/";

	String VIDEO_INFO_URL = "http://service.canal-plus.com/video/rest/getVideosLiees/cplus/";

	String MEA_PACKAGE_NAME = MEA.class.getPackage().getName();

	String VIDEO_PACKAGE_NAME = VIDEO.class.getPackage().getName();

	String INITPLAYER_PACKAGE_NAME = INITPLAYER.class.getPackage().getName();

}
