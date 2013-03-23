package com.dabi.habitv.provider.tmc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

public class TMCPluginManagerTest {

	private final TMCPluginManager manager = new TMCPluginManager();

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
		assertEquals(manager.getName(), TMCConf.NAME);
	}

	@Test
	public final void testFindCategory() {
		final Set<CategoryDTO> categories = manager.findCategory();
		assertTrue(!categories.isEmpty());
		for (final CategoryDTO categoryDTO : categories) {
			assertNotNull(categoryDTO.getName());
		}
	}

	@Test
	public final void testFindEpisode() {
		final CategoryDTO category = new CategoryDTO("channel", "Walker Texas Ranger", "walker-texas-ranger", "flv");
		final Set<EpisodeDTO> episodeList = manager.findEpisode(category);
		boolean contain = false;
		for (final EpisodeDTO episode : episodeList) {
			if (episode.getName().equals("Walker Texas Ranger Saison 4 Episode 7 : Nom de code - Dragonfly")) {
				assertEquals(episode.getUrl(), "/walker-texas-ranger/saison-4/walker-texas-ranger-saison-4-episode-7-nom-de-code-dragonfly-7879490-848.html");
				contain = true;
			}
		}
		assertTrue(contain);
	}

	@Test
	public final void testDownload() throws DownloadFailedException, NoSuchDownloaderException {
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
			//public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters,
			//		final CmdProgressionListener listener, final Map<ProxyDTO.ProtocolEnum, ProxyDTO> proxies) throws DownloadFailedException {
			public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters, final CmdProgressionListener listener,
					final Map<ProxyDTO.ProtocolEnum, ProxyDTO> proxies) throws DownloadFailedException {
				assertTrue(downloadInput.length() > 0);
			}
		};
		downloaderName2downloader.put("rtmpdump", downloader);
		final Map<String, String> downloaderName2BinPath = new HashMap<>();
		downloaderName2BinPath.put("curl", "bin");
		final DownloaderDTO downloaders = new DownloaderDTO(null, downloaderName2downloader, downloaderName2BinPath, null, null);
		final CategoryDTO category = new CategoryDTO("channel", "Walker Texas Ranger", "walker-texas-ranger", "flv");
		final EpisodeDTO episode = new EpisodeDTO(category, "Walker",
				"/walker-texas-ranger/saison-4/walker-texas-ranger-saison-4-episode-7-nom-de-code-dragonfly-7879490-848.html");
		manager.download("downloadOuput", downloaders, new CmdProgressionListener() {

			@Override
			public void listen(final String progression) {
			}
		}, episode);
	}

}
