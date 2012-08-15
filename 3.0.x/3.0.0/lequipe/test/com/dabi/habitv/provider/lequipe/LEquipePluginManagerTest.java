package com.dabi.habitv.provider.lequipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class LEquipePluginManagerTest {

	private final LEquipePluginManager manager = new LEquipePluginManager();

	private static final Logger LOG = Logger.getLogger(LEquipePluginManagerTest.class);

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
		assertEquals(manager.getName(), LEquipeConf.NAME);
	}

	private void checkFindEpisode(final String categoryName, final String episodeName, final String url) {
		final CategoryDTO category = new CategoryDTO("channel", categoryName, categoryName, "mp4");
		final Set<EpisodeDTO> episodeList = manager.findEpisode(category);
		boolean contain = false;
		for (final EpisodeDTO episode : episodeList) {
			if (episode.getName().equals(episodeName)) {
				assertEquals(url, episode.getUrl());
				contain = true;
			}
		}
		assertTrue(contain);
	}

	@Test
	public final void testFindEpisode() {
		checkFindEpisode("Rencontre", "Sochaux - Bastia", "/video/football/ligue-1/2012-2013/1ere-journee/sochaux-bastia/");
		checkFindEpisode("Résumé", "Le résumé de la 1ère journée de Ligue 1", "/video/football/ligue-1/");
		checkFindEpisode("Avant-Match", "Reims trace son histoire",
				"/video/football/ligue-1/2012-2013/1ere-journee/reims-marseille/reims-trace-son-histoire/31f5765d039s/");
	}

	@Test
	public final void testFindCategory() {
		final Set<CategoryDTO> categories = manager.findCategory();
		assertTrue(!categories.isEmpty());
		for (final CategoryDTO categoryDTO : categories) {
			assertNotNull(categoryDTO.getName());
			assertTrue("Rencontre".equals(categoryDTO.getName()) || "Résumé".equals(categoryDTO.getName()) || "Avant-Match".equals(categoryDTO.getName()));
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
		}, new EpisodeDTO(null, "test", "/video/football/ligue-1/2012-2013/1ere-journee/reims-marseille/reims-marseille-0-1/2ee01cfaa2ds/"));
	}

	private DownloaderDTO buildDownloaders() {
		final Map<String, PluginDownloaderInterface> downloaderName2downloader = new HashMap<>();
		final PluginDownloaderInterface downloader = new PluginDownloaderInterface() {

			@Override
			public void setClassLoader(final ClassLoader classLoader) {

			}

			@Override
			public String getName() {
				return "curl";
			}

			@Override
			public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters,
					final CmdProgressionListener listener) throws DownloadFailedException {
				assertTrue(downloadInput.contains("sig"));
			}
		};
		downloaderName2downloader.put("curl", downloader);
		final Map<String, String> downloaderName2BinPath = new HashMap<>();
		downloaderName2BinPath.put("curl", "bin");
		return new DownloaderDTO(downloaderName2downloader, downloaderName2BinPath, null, null);
	}
}
