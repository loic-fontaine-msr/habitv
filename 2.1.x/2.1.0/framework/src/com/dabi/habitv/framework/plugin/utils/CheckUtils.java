package com.dabi.habitv.framework.plugin.utils;

public final class CheckUtils {

	private CheckUtils() {
	}

	public static boolean checkMinSize(final String category) {
		return category != null && category.length() >= FrameworkConf.MIN_SIZE;
	}

}
