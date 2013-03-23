package com.dabi.habitv.provider.tmc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public class TMCPluginManager implements PluginProviderInterface {

	@Override
	public String getName() {
		return TMCConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {

	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return TMCRetreiver.findEpisode(category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return TMCRetreiver.findCategory();
	}

	private String getDownloader(final String url) {
		String downloaderName;
		if (url.startsWith(TMCConf.RTMPDUMP_PREFIX)) {
			downloaderName = TMCConf.RTMDUMP;
		} else {
			downloaderName = TMCConf.CURL;
		}
		return downloaderName;
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener,
			final EpisodeDTO episode) throws DownloadFailedException, NoSuchDownloaderException {
		final String mediaId = TMCRetreiver.findFinalUrl(episode);

		String videoUrl = RetrieverUtils.getUrlContent(TMCRetreiver.buildUrlVideoInfo(mediaId, "web"));

		final String downloaderName = getDownloader(videoUrl);
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());

		if (TMCConf.RTMDUMP.equals(downloaderName)) {
			videoUrl = videoUrl.replace(",rtmpte", "");
			videoUrl = videoUrl.substring(0, videoUrl.lastIndexOf("?"));
			parameters.put(FrameworkConf.PARAMETER_ARGS, TMCConf.DUMP_CMD);
			pluginDownloader.download(videoUrl, downloadOuput, parameters, cmdProgressionListener, downloaders.getProtocol2proxy());
			// pluginDownloader.download(videoUrl, downloadOuput, parameters,
			// cmdProgressionListener, downloaders.getProtocol2proxy());
		} else {
			pluginDownloader.download(videoUrl, downloadOuput, parameters, cmdProgressionListener, downloaders.getProtocol2proxy());
			// pluginDownloader.download(videoUrl, downloadOuput, parameters,
			// cmdProgressionListener, downloaders.getProtocol2proxy());
		}
	}

}
