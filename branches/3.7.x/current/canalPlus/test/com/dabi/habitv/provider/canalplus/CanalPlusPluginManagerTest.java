package com.dabi.habitv.provider.canalplus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
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

public class CanalPlusPluginManagerTest {

	private final CanalPlusPluginManager manager = new CanalPlusPluginManager();

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
		assertEquals(manager.getName(), CanalPlusConf.NAME);
	}

	@Test
	public final void testFindEpisode() {
		final Set<EpisodeDTO> episodeList = manager.findEpisode(new CategoryDTO("canalplus", "FOOTBALL", "3", "mp4"));
		assertTrue(!episodeList.isEmpty());
	}


	@Test
	public final void testFindCategory() {
		final Set<CategoryDTO> categories = manager.findCategory();
		assertTrue(!categories.isEmpty());
		testCats(categories);
	}

	private void testCats(final Collection<CategoryDTO> categories) {
		for (final CategoryDTO categoryDTO : categories) {
			System.out.println(categoryDTO.getId()+" "+categoryDTO.getName());
			assertNotNull(categoryDTO.getName());
			if (categoryDTO.getSubCategories() != null && !categoryDTO.getSubCategories().isEmpty()) {
				testCats(categoryDTO.getSubCategories());
			}
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
				assertTrue(downloadInput.contains("inside"));
			}
		};
		downloaderName2downloader.put("curl", downloader);
		final Map<String, String> downloaderName2BinPath = new HashMap<>();
		downloaderName2BinPath.put("curl", "bin");
		final DownloaderDTO downloaders = new DownloaderDTO(null, downloaderName2downloader, downloaderName2BinPath, null, null);
		final EpisodeDTO episode = new EpisodeDTO(new CategoryDTO("50-mn-inside", "50-mn-inside", "50-mn-inside", "50-mn-inside"),
				"Emission du 20 juillet 2013", "/50-mn-inside/emission-du-20-juillet-2013-8136376.html");
		manager.download("downloadOuput", downloaders, new CmdProgressionListener() {

			@Override
			public void listen(final String progression) {
			}
		}, episode);
	}

	@Test
	public final void testDownloadExpert() throws DownloadFailedException, NoSuchDownloaderException {
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
				assertTrue(downloadInput.contains("inside"));
			}
		};
		downloaderName2downloader.put("curl", downloader);
		final Map<String, String> downloaderName2BinPath = new HashMap<>();
		downloaderName2BinPath.put("curl", "bin");
		final DownloaderDTO downloaders = new DownloaderDTO(null, downloaderName2downloader, downloaderName2BinPath, null, null);
		final EpisodeDTO episode = new EpisodeDTO(new CategoryDTO("expert", "expert", "expert", "expert"), "expert",
				"/les-experts-las-vegas/episode-01-saison-11-tic-tac-7035188.html");
		manager.download("downloadOuput", downloaders, new CmdProgressionListener() {

			@Override
			public void listen(final String progression) {
			}
		}, episode);
	}

}
