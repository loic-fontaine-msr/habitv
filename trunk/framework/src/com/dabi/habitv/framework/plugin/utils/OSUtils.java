package com.dabi.habitv.framework.plugin.utils;

public class OSUtils {
	private static String OS = System.getProperty("os.name").toLowerCase();

	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);

	}
}
