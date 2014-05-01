package com.dabi.habitv.provider.rss;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.dto.ProxyDTO;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;

import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class RSSPluginManagerTest {

	RSSPluginManager manager = new RSSPluginManager();

	@Test
	public void testFindEpisode() {
		final CategoryDTO category = new CategoryDTO("RSS", "Java Hub", "http://gdata.youtube.com/feeds/base/users/tariqramadanvideo/uploads?alt=rss&amp;v=2&amp;orderby=published&amp;client=ytapi-youtube-profile", "flv");
		final Set<EpisodeDTO> episodes = manager.findEpisode(category);
		assertTrue(!episodes.isEmpty());
	}

	@Test
	public void testFindCategory() {
		final Set<CategoryDTO> categories = manager.findCategory();
		assertTrue(!categories.isEmpty());
	}

	@Test
	public void testDownload() throws DownloadFailedException {
		final Map<String, PluginDownloaderInterface> downloaderName2downloader = new HashMap<>();
		final PluginDownloaderInterface downloader = new PluginDownloaderInterface() {

			@Override
			public void setClassLoader(final ClassLoader classLoader) {

			}

			@Override
			public String getName() {
				return "youtube";
			}

			@Override
			public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters,
					final CmdProgressionListener listener, final Map<ProxyDTO.ProtocolEnum, ProxyDTO> proxies) throws DownloadFailedException {
				assertTrue(downloadInput.length() > 0);
			}
		};
		downloaderName2downloader.put(downloader.getName(), downloader);
		final Map<String, String> downloaderName2BinPath = new HashMap<>();
		downloaderName2BinPath.put(downloader.getName(), "bin");
		final DownloaderDTO downloaders = new DownloaderDTO(null, downloaderName2downloader, downloaderName2BinPath, null, null);
		final CategoryDTO category = new CategoryDTO("rss", "rss", "rss", "flv");
		final EpisodeDTO episode = new EpisodeDTO(category, "test", "http://www.youtube.com/watch?v=gg3ARiiSAfM&feature=plcp");
		manager.download("downloadOuput", downloaders, new CmdProgressionListener() {

			@Override
			public void listen(final String progression) {
			}
		}, episode);
	}

}
