package com.dabi.habitv.provider.soirfoot;

import java.util.Set;

import com.dabi.habitv.framework.plugin.api.ProviderPluginInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public class SoirFootPluginManager implements ProviderPluginInterface {

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return SoirFootRetriever.findEpisodeByCategory(category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return SoirFootCategoriesFinder.findCategory();
	}

	@Override
	public String downloadCmd(final String url) {// TODO changer en
													// getDownloadParam ?
		String cmd = null;
		if (url.startsWith(SoirFootConf.RTMPDUMP_PREFIX)) {
			cmd = SoirFootConf.RTMP_DUMP_CMD;
			// split
			// rtmp://video-1-15.rutube.ru/rutube_vod_1/mp4:vol21/movies/5b/c4/5bc45bc80ad9f9597a8e1de3e0cf69f6.mp4?e=1336830894&s=0d33d853ebb8aacaa465e2a2b0fdfc59
			// rtmp://video-1-15.rutube.ru/rutube_vod_1/
			// rutube_vod_1/
			// mp4:vol21/movies/5b/c4/5bc45bc80...
			int mp4Index = url.indexOf("mp4:");
			String baseUrl = url.substring(0, mp4Index);
			String baseUrlSub = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
			String app = baseUrlSub.substring(baseUrlSub.lastIndexOf("/")+1, baseUrlSub.length());
			String mp4Url = url.substring(mp4Index, url.length() - 1);

			cmd = cmd.replace(SoirFootConf.APP, app);
			cmd = cmd.replace(SoirFootConf.BASE_URL_APP, baseUrl);
			cmd = cmd.replace(SoirFootConf.MP4_URL, mp4Url);
		}
		return cmd;
	}

	@Override
	public String getDownloader(final String url) {
		String downloaderName;
		if (url.startsWith(SoirFootConf.RTMPDUMP_PREFIX)) {
			downloaderName = SoirFootConf.RTMDUMP;
		} else {
			downloaderName = SoirFootConf.ARIA2;
		}
		return downloaderName;
	}

	@Override
	public String getName() {
		return SoirFootConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {

	}

}
