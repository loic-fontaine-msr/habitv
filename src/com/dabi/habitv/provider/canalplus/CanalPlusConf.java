package com.dabi.habitv.provider.canalplus;


interface CanalPlusConf {

	String NAME = "canalPlus";

	String VIDEO_INFO_URL = "http://service.canal-plus.com/video/rest/getVideosLiees/cplus/";
	
	String URL_HOME = "http://service.mycanal.fr/authenticate.json/Android_Phone/1.1?highResolution=1";
	
	String URL_VIDEO = "http://service.mycanal.fr/getMediaUrl/{TOKEN}/{ID}.json?pfv={FORMAT}";
	
	String URL_VIDEO_2 = "http://service.canal-plus.com/video/rest/getvideos/{CHANNEL}/{ID}?format=json";
	
}
