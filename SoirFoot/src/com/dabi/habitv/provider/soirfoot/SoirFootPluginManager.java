package com.dabi.habitv.provider.soirfoot;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.utils.FrameworkConf;

public class SoirFootPluginManager implements PluginProviderInterface {

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return SoirFootRetriever.findEpisodeByCategory(category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return SoirFootCategoriesFinder.findCategory();
	}

	private String downloadCmd(final String url) {
		String cmd = null;
		if (url.startsWith(SoirFootConf.RTMPDUMP_PREFIX)) {
			cmd = SoirFootConf.RTMP_DUMP_CMD;
			// split
			// rtmp://video-1-15.rutube.ru/rutube_vod_1/mp4:vol21/movies/5b/c4/5bc45bc80ad9f9597a8e1de3e0cf69f6.mp4?e=1336830894&s=0d33d853ebb8aacaa465e2a2b0fdfc59
			// rtmp://video-1-15.rutube.ru/rutube_vod_1/
			// rutube_vod_1/
			// mp4:vol21/movies/5b/c4/5bc45bc80...
			final int mp4Index = url.indexOf("mp4:");
			final String baseUrl = url.substring(0, mp4Index);
			final String baseUrlSub = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
			final String app = baseUrlSub.substring(baseUrlSub.lastIndexOf("/") + 1, baseUrlSub.length());
			final String mp4Url = url.substring(mp4Index, url.length() - 1);

			cmd = cmd.replace(SoirFootConf.APP, app);
			cmd = cmd.replace(SoirFootConf.BASE_URL_APP, baseUrl);
			cmd = cmd.replace(SoirFootConf.MP4_URL, mp4Url);
		}
		return cmd;
	}

	private String getDownloader(final String url) {
		String downloaderName;
		if (url.startsWith(SoirFootConf.RTMPDUMP_PREFIX)) {
			downloaderName = SoirFootConf.RTMDUMP;
		} else {
			downloaderName = SoirFootConf.ARIA2;
		}
		return downloaderName;
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener listener, final EpisodeDTO episode)
			throws DownloadFailedException, NoSuchDownloaderException {
		final String downloaderName = getDownloader(episode.getVideoUrl());
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.PARAMETER_ARGS, downloadCmd(episode.getVideoUrl()));

		pluginDownloader.download(episode.getVideoUrl(), downloadOuput, parameters, listener);
	}

	@Override
	public String getName() {
		return SoirFootConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {

	}

}
