package com.dabi.habitv.core.config;

public interface HabitTvConf {

	String DEFAULT_DOWNLOADER = "cmdDownloader";

	String DEFAULT_EXPORTER = "cmdExporter";

	String GRABCONFIG_XML_FILE = "grabconfig.xml";

	String ENCODING = "UTF-8";
	Integer DEFAULT_POOL_SIZE = 2;

	long DEFAULT_DEMON_TIME_SEC = 1800;

	int CUT_SIZE = 40;

	String GRAB_CONF_XSD = "grab-config.xsd";

	String GRAB_CONF_PACKAGE_NAME = "com.dabi.habitv.grabconfig.entities";

}
