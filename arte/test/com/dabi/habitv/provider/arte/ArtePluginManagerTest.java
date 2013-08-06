package com.dabi.habitv.provider.arte;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.dto.ProxyDTO;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class ArtePluginManagerTest {

	private final ArtePluginManager manager = new ArtePluginManager();

	private static final Logger LOG = Logger.getLogger(ArtePluginManagerTest.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGetName() {
		manager.setClassLoader(null);
		assertEquals(manager.getName(), ArteConf.NAME);
	}
	
	@Test
	public final void testFindCategory() {
		final Set<CategoryDTO> categories = manager.findCategory();
		assertTrue(!categories.isEmpty());
	}

	private void checkFindEpisode() {
		final Set<EpisodeDTO> episodeList = manager.findEpisode(new CategoryDTO("arte", "Les dernières vidéos ARTE +7", "videos/index--3188626", "mp4"));
		for (final EpisodeDTO episode : episodeList) {
			assertTrue(!episode.getName().isEmpty());
			assertTrue(!episode.getUrl().isEmpty());
		}
	}

	@Test
	public final void testFindEpisode() {
		checkFindEpisode();
	}

	@Test
	public final void testSearchEpisode() {
		final Set<EpisodeDTO> episodeList = manager.findEpisode(new CategoryDTO("arte", "le saint", "search",  "mp4"));
		for (final EpisodeDTO episode : episodeList) {
			assertTrue(!episode.getName().isEmpty());
			assertTrue(!episode.getUrl().isEmpty());
		}
	}

	@Test
	public void testDownload() throws DownloadFailedException, NoSuchDownloaderException {
		final DownloaderDTO downloaders = buildDownloaders();
		manager.download("./test.flv", downloaders, new CmdProgressionListener() {

			@Override
			public void listen(final String progression) {
				LOG.info(progression);
			}
		}, new EpisodeDTO(null, "Les montagnes du monde 20130806", "http://videos.arte.tv/fr/videos/les-montagnes-du-monde--7609508.html"));
	}

	private DownloaderDTO buildDownloaders() {
		final Map<String, PluginDownloaderInterface> downloaderName2downloader = new HashMap<>();
		final PluginDownloaderInterface downloader = new PluginDownloaderInterface() {

			@Override
			public void setClassLoader(final ClassLoader classLoader) {

			}

			@Override
			public String getName() {
				return "rtmpdump";
			}

			@Override
			public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters,
					final CmdProgressionListener listener, final Map<ProxyDTO.ProtocolEnum, ProxyDTO> proxies) throws DownloadFailedException {
				assertTrue(!downloadInput.isEmpty());
			}
		};
		downloaderName2downloader.put("rtmpdump", downloader);
		final Map<String, String> downloaderName2BinPath = new HashMap<>();
		downloaderName2BinPath.put("rtmpdump", "bin");
		return new DownloaderDTO(null, downloaderName2downloader, downloaderName2BinPath, null, null);
	}
}
