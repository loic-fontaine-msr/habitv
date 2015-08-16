package com.dabi.habitv.framework.plugin.utils;

import java.util.ArrayList;
import java.util.List;

public class M3U8Utils {

	public static String keepBestQuality(final String videoUrl) {
		final String[] tokens = videoUrl.split(",");
		final List<String> qualityList = new ArrayList<>();
		for (final String token : tokens) {
			if (!token.startsWith("http") && !token.contains("m3u8")) {
				qualityList.add(token);
			}
		}
		if (qualityList.isEmpty()) {
			return videoUrl;
		}

		String newVideoUrl = videoUrl;
		qualityList.remove(qualityList.get(qualityList.size() - 1));
		for (final String quality : qualityList) {
			newVideoUrl = newVideoUrl.replace(quality + ",", "");
		}
		return newVideoUrl;
	}
}
