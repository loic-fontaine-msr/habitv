package com.dabi.habitv.provider.tf1;

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

public class TF1PluginManagerTest {

	private final TF1PluginManager manager = new TF1PluginManager();

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
		assertEquals(manager.getName(), TF1Conf.NAME);
	}

	@Test
	public final void testFindEpisode() {
		final Set<EpisodeDTO> episodeList = manager.findEpisode(new CategoryDTO("channel", "casper", "casper", "mp4"));
		boolean contain = false;
		for (final EpisodeDTO episode : episodeList) {
			if (episode.getName().equals("La saison des Gloutchs - Casper")) {
				assertEquals("/casper/la-saison-des-gloutchs-casper-7400892.html", episode.getUrl());
				contain = true;
			}
		}
		assertTrue(contain);
	}

	@Test
	public final void testFindEpisode50MinuteInside() {
		final Set<EpisodeDTO> episodeList = manager.findEpisode(new CategoryDTO("channel", "50-mn-inside", "50-mn-inside", "mp4"));
		boolean contain = false;
		for (final EpisodeDTO episode : episodeList) {
			if (episode.getName().equals("Emission du 28 juillet 2012")) {
				assertEquals("/50-mn-inside/emission-du-28-juillet-2012-7425510.html", episode.getUrl());
				contain = true;
			}
		}
		assertTrue(contain);
	}

	@Test
	public final void testFindEpisodeTelefoot() {
		final Set<EpisodeDTO> episodeList = manager.findEpisode(new CategoryDTO("telefoot", "telefoot", "telefoot", "mp4"));
		boolean contain = false;
		for (final EpisodeDTO episode : episodeList) {
			if (episode.getName().equals("(Re)Voir Téléfoot du dimanche 1er juillet 2012 en intégralité")) {
				assertEquals("/telefoot/re-voir-telefoot-du-dimanche-1er-juillet-2012-en-integralite-7391330.html", episode.getUrl());
				contain = true;
			}
		}
		assertTrue(contain);
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
	public final void testDownload() throws DownloadFailedException, NoSuchDownloaderException {
		final Map<String, PluginDownloaderInterface> downloaderName2downloader = new HashMap<>();
		final PluginDownloaderInterface downloader = new PluginDownloaderInterface() {

			@Override
			public void setClassLoader(final ClassLoader classLoader) {

			}

			@Override
			public String getName() {
				return "aria2";
			}

			@Override
			public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters,
					final CmdProgressionListener listener, final Map<ProxyDTO.ProtocolEnum, ProxyDTO> proxies) throws DownloadFailedException {
				assertTrue(downloadInput.contains("casper-tf1"));
			}
		};
		downloaderName2downloader.put("curl", downloader);
		final Map<String, String> downloaderName2BinPath = new HashMap<>();
		downloaderName2BinPath.put("curl", "bin");
		final DownloaderDTO downloaders = new DownloaderDTO(null, downloaderName2downloader, downloaderName2BinPath, null, null);
		final EpisodeDTO episode = new EpisodeDTO(new CategoryDTO("casper", "casper", "casper", "casper"), "Casper",
				"/casper/l-attaque-des-poux-geants-casper-7460059.html");
		manager.download("downloadOuput", downloaders, new CmdProgressionListener() {

			@Override
			public void listen(final String progression) {
			}
		}, episode);
	}

}
