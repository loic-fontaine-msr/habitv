package com.dabi.habitv.provider.canalplus;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.utils.DownloadUtils;
import com.dabi.habitv.framework.plugin.utils.M3U8Utils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CanalUtils {

	@SuppressWarnings("unchecked")
	public static String findToken(BasePluginWithProxy basePluginWithProxy)
			throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final Map<String, Object> mainData = mapper.readValue(
				basePluginWithProxy
						.getInputStreamFromUrl(CanalPlusConf.URL_HOME),
				Map.class);

		return (String) mainData.get("token");
	}

	public static ProcessHolder doDownload(
			final DownloadParamDTO downloadParam,
			final DownloaderPluginHolder downloaders,
			BasePluginWithProxy basePluginWithProxy, String videoInfoUrl,
			String channel) {
		final String videoUrl;
		if (downloadParam.getDownloadInput().contains("vid=")) {
			final String vid = CanalUtils.getVid(downloadParam);
			try {
				videoUrl = CanalUtils.findVideoUrl(basePluginWithProxy,
						findToken(basePluginWithProxy), vid, channel);
			} catch (IOException e) {
				throw new DownloadFailedException(e);
			}
		} else {
			videoUrl = downloadParam.getDownloadInput();
		}
		return DownloadUtils.download(
				DownloadParamDTO.buildDownloadParam(downloadParam, videoUrl),
				downloaders);
	}

	public static String getVid(final DownloadParamDTO downloadParam) {
		final String vid = downloadParam.getDownloadInput().split("vid=")[1]
				.split("&")[0];
		return vid;
	}

	@SuppressWarnings("unchecked")
	private static List<Object> getVideoUrls(Map<String, Object> catData) {
		return (List<Object>) ((Map<String, Object>) ((Map<String, Object>) catData
				.get("detail")).get("informations")).get("videoURLs");
	}

	@SuppressWarnings("unchecked")
	public static String findUrl(BasePluginWithProxy basePluginWithProxy,
			String urlMedias) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final Map<String, Object> catData = mapper.readValue(
					basePluginWithProxy.getInputStreamFromUrl(urlMedias
							.replace("{FORMAT}", "hls")), Map.class);

			List<Object> videoUrls = getVideoUrls(catData);
			if (videoUrls == null) {
				mapper.readValue(basePluginWithProxy
						.getInputStreamFromUrl(urlMedias.replace("{FORMAT}",
								"hd")), Map.class);
				videoUrls = getVideoUrls(catData);
			}
			for (Object videoUrlObject : videoUrls) {
				Map<String, Object> videoUrlMap = (Map<String, Object>) videoUrlObject;
				String videoUrl = (String) videoUrlMap.get("videoURL");
				if (videoUrl.endsWith(FrameworkConf.M3U8)) {
					return M3U8Utils.keepBestQuality(videoUrl);
				}
				return videoUrl;
			}
			throw new DownloadFailedException("can't find videoUrl");
		} catch (IOException e) {
			throw new DownloadFailedException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static String findUrl2(BasePluginWithProxy basePluginWithProxy,
			String urlMedias) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final Map<String, Object> catData = mapper.readValue(
					basePluginWithProxy.getInputStreamFromUrl(urlMedias),
					Map.class);

			Map<String, Object> videoUrls = (Map<String, Object>) ((Map<String, Object>) catData
					.get("MEDIA")).get("VIDEOS");
			String videoUrl = (String) videoUrls.get("HLS");
			return M3U8Utils.keepBestQuality(videoUrl);
		} catch (IOException e) {
			throw new DownloadFailedException(e);
		}
	}

	public static String findVideoUrl(BasePluginWithProxy basePluginWithProxy,
			String token, String vid, String channel) {
		try {
			return findUrl(basePluginWithProxy, CanalPlusConf.URL_VIDEO
					.replace("{TOKEN}", token).replace("{ID}", vid));
		} catch (Exception e) {
			return findUrl2(basePluginWithProxy, CanalPlusConf.URL_VIDEO_2
					.replace("{CHANNEL}", channel).replace("{ID}", vid));
		}
	}

}
