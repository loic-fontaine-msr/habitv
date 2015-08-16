package com.dabi.habitv.utils;

import java.io.File;

import com.dabi.habitv.core.config.HabitTvConf;
import com.dabi.habitv.core.config.XMLUserConfig;
import com.dabi.habitv.framework.FrameworkConf;

public class DirUtils {

	public static final String APP_USER_HOME_DIR = FrameworkConf.USER_HOME
			+ "/" + "habitv";

	public static final String LOCAL_PATH = getLocalPath();

	public static final String USER_CONF_PATH = APP_USER_HOME_DIR + "/"
			+ HabitTvConf.CONF_FILE;

	public static final String LOCAL_CONF_PATH = LOCAL_PATH + "/"
			+ HabitTvConf.CONF_FILE;

	public static final String USER_OLD_CONF_PATH = APP_USER_HOME_DIR + "/"
			+ HabitTvConf.OLD_CONF_FILE;

	public static final String LOCAL_OLD_CONF_PATH = LOCAL_PATH + "/"
			+ HabitTvConf.OLD_CONF_FILE;

	public static final String LOCAL_GRABCONFIG_PATH = LOCAL_PATH + "/"
			+ HabitTvConf.GRABCONFIG_XML_FILE;

	public static final boolean IS_LOCAL_MODE = exists(LOCAL_CONF_PATH)
			|| exists(LOCAL_OLD_CONF_PATH) || exists(LOCAL_GRABCONFIG_PATH);

	private static boolean exists(String path) {
		return (new File(path)).exists();
	}

	public static String getAppDir() {
		return IS_LOCAL_MODE ? LOCAL_PATH : APP_USER_HOME_DIR;
	}

	private static String getLocalPath() {
		String absolutePath = new File(XMLUserConfig.class
				.getProtectionDomain().getCodeSource().getLocation().getPath())
				.getAbsolutePath();
		return absolutePath.endsWith("\\target\\classes") ? absolutePath
				.replace("\\target\\classes", "") : absolutePath.substring(0,
				absolutePath.lastIndexOf(File.separator));
	}

	public static String getGrabConfigPath() {
		return getAppDir() + "/" + HabitTvConf.GRABCONFIG_XML_FILE;
	}

	public static String getConfFile() {
		return getAppDir() + "/" + HabitTvConf.CONF_FILE;
	}

	public static String getOldConfFile() {
		return getAppDir() + "/" + HabitTvConf.OLD_CONF_FILE;
	}

	public static String getLogFile() {
		return getAppDir() + "/" + HabitTvConf.LOG_FILE;
	}

	public static String getLogFileSlash() {
		return getLogFile().replace("\\", "/");
	}

}
