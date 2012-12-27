package com.dabi.habitv.provider.tf1;

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

public class TF1PluginManager implements PluginProviderInterface {

	@Override
	public String getName() {
		return TF1Conf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {

	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return TF1Retreiver.findEpisode(category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return TF1Retreiver.findCategory();
	}

	private String getDownloader(final String url) {
		String downloaderName;
		if (url.startsWith(TF1Conf.RTMPDUMP_PREFIX)) {
			downloaderName = TF1Conf.RTMDUMP;
		} else {
			downloaderName = TF1Conf.CURL;
		}
		return downloaderName;
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener,
			final EpisodeDTO episode) throws DownloadFailedException, NoSuchDownloaderException {
		String videoUrl = TF1Retreiver.findFinalUrl(episode);
		final String downloaderName = getDownloader(videoUrl);
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());

		if (TF1Conf.RTMDUMP.equals(downloaderName)) {
			videoUrl = videoUrl.replace(",rtmpte", "");
			videoUrl = videoUrl.substring(0, videoUrl.lastIndexOf("?"));
			parameters.put(FrameworkConf.PARAMETER_ARGS, TF1Conf.DUMP_CMD);
		}

		pluginDownloader.download(videoUrl, downloadOuput, parameters, cmdProgressionListener);
	}

}
