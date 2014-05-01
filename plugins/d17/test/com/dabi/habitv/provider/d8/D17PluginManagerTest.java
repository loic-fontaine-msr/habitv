package com.dabi.habitv.provider.d8;

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
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class D17PluginManagerTest {

	private final D17PluginManager manager = new D17PluginManager();

	private static final Logger LOG = Logger.getLogger(D17PluginManagerTest.class);

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
		assertEquals(manager.getName(), D17Conf.NAME);
	}

	@Test
	public final void testFindEpisode() {
		final Set<EpisodeDTO> episodeList = manager.findEpisode(new CategoryDTO(null, null, "/pid6278-musique.html", null));
		assertTrue(!episodeList.isEmpty());
	}

	@Test
	public final void testFindCategory() {
		final Set<CategoryDTO> categories = manager.findCategory();
		assertTrue(!categories.isEmpty());
	}

	@Test
	public void testDownload() throws DownloadFailedException {
		final DownloaderDTO downloaders = buildDownloaders();
		manager.download("./test.flv", downloaders, new CmdProgressionListener() {

			@Override
			public void listen(final String progression) {
				LOG.info(progression);
			}
		}, new EpisodeDTO(null, "test", "933914"));
	}

	private DownloaderDTO buildDownloaders() {
		final Map<String, PluginDownloaderInterface> downloaderName2downloader = new HashMap<>();
		PluginDownloaderInterface downloader = new PluginDownloaderInterface() {

			@Override
			public void setClassLoader(final ClassLoader classLoader) {

			}

			@Override
			public String getName() {
				return "curl";
			}

			@Override
			public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters,
					final CmdProgressionListener listener, final Map<ProxyDTO.ProtocolEnum, ProxyDTO> proxies) throws DownloadFailedException {
				assertTrue(downloadInput.length() > 0);
			}
		};
		downloaderName2downloader.put("curl", downloader);
		downloader = new PluginDownloaderInterface() {

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
				assertTrue(downloadInput.length() > 0);
			}
		};
		downloaderName2downloader.put("rtmpdump", downloader);
		final Map<String, String> downloaderName2BinPath = new HashMap<>();
		downloaderName2BinPath.put("curl", "bin");
		return new DownloaderDTO(null, downloaderName2downloader, downloaderName2BinPath, null, null);
	}
}
