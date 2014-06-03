package com.dabi.habitv.core.config;

import com.dabi.habitv.framework.FrameworkConf;

public interface HabitTvConf {

	String DEFAULT_EXPORTER = "cmdExporter";

	String GRABCONFIG_XML_FILE = "grabconfig.xml";

	String ENCODING = "UTF-8";

	String GRAB_CONF_XSD = "grab-config.xsd";

	String STAT_URL = "http://dabiboo.free.fr/cpt.php";
	
	String LOG_FILE = FrameworkConf.USER_HOME+"/habitv/"+"habiTv.log"; 

}
