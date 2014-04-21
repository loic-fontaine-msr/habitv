package com.dabi.habitv.framework.plugin.utils;

public class SoccerUtils {

	public static String maskScore(String name) {
		name = name.replaceAll("(\\d\\s*-\\s*\\d)", "").replaceAll("(\\d_*-_*\\d)", "");
		return name;
	}
}
