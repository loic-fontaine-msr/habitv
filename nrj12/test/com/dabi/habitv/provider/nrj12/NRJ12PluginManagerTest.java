package com.dabi.habitv.provider.nrj12;

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
import com.dabi.habitv.framework.plugin.api.dto.ProxyDTO.ProtocolEnum;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class NRJ12PluginManagerTest {

	private final NRJ12PluginManager manager = new NRJ12PluginManager();

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
		assertEquals(manager.getName(), NRJ12Conf.NAME);
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
		final CategoryDTO category = new CategoryDTO("channel", "STARGATE ATLANTIS", "STARGATE ATLANTIS", "mp4");
		final Set<EpisodeDTO> episodeList = manager.findEpisode(category);
		boolean contain = false;
		for (final EpisodeDTO episode : episodeList) {
			if (episode.getName().equals("LA GUERRE DES GENIES")) {
				assertEquals(episode.getUrl(), "/replay-4203/media/video/664573-la-guerre-des-genies.html");
				contain = true;
			}
		}
		assertTrue(contain);
	}

	@Test
	public final void testFindSanctuaryEpisode() {
		final CategoryDTO category = new CategoryDTO("channel", "SANCTUARY", "SANCTUARY", "mp4");
		final Set<EpisodeDTO> episodeList = manager.findEpisode(category);
		boolean contain = false;
		for (final EpisodeDTO episode : episodeList) {
			if (episode.getName().equals("LES GLADIATEURS")) {
				assertEquals(episode.getUrl(), "/replay-4203/media/video/663231-les-gladiateurs.html");
				contain = true;
			}
		}
		assertTrue(contain);
	}

	@Test
	public final void testFindOtherEpisode() {
		final CategoryDTO category = new CategoryDTO("channel", "_other_", "_other_", "mp4");
		final Set<EpisodeDTO> episodeList = manager.findEpisode(category);
		boolean contain = false;
		for (final EpisodeDTO episode : episodeList) {
			if (episode.getName().equals("AIRLINE DISASTER")) {
				assertEquals(episode.getUrl(), "/replay-4203/media/video/665199-airline-disaster.html");
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
			public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters, final CmdProgressionListener listener,
					final Map<ProtocolEnum, ProxyDTO> proxies) throws DownloadFailedException {
				assertTrue(downloadInput.length() > 0);
			}
		};
		downloaderName2downloader.put("rtmpdump", downloader);
		final Map<String, String> downloaderName2BinPath = new HashMap<>();
		downloaderName2BinPath.put("curl", "bin");
		final DownloaderDTO downloaders = new DownloaderDTO(null, downloaderName2downloader, downloaderName2BinPath, null, null);
		final CategoryDTO category = new CategoryDTO("channel", "STARGATE ATLANTIS", "STARGATE ATLANTIS", "mp4");
		final EpisodeDTO episode = new EpisodeDTO(category, "LA GUERRE DES GENIES", "/replay-4203/media/video/664573-la-guerre-des-genies.html");
		final CmdProgressionListener cmdProgressionListener = new CmdProgressionListener() {
			@Override
			public void listen(final String progression) {
			}
		};
		manager.download("downloadOuput", downloaders, cmdProgressionListener, episode);
	}

}
