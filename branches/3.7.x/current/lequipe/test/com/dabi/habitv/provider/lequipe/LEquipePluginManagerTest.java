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
import com.dabi.habitv.framework.plugin.api.dto.ProxyDTO;
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

	private void checkFindEpisode(final String mainCategory, final String categoryName, final String categoryId, final String episodeName, final String url) {
		final CategoryDTO mainCategoryDTO = new CategoryDTO("channel", mainCategory, mainCategory, "mp4");
		final CategoryDTO category = new CategoryDTO("channel", categoryName, categoryId, "mp4");
		mainCategoryDTO.addSubCategory(category);

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
		checkFindEpisode("BASKET / HAND /VOLLEY", "handball", "/cat/handball/", "Hand - JO : Fernandez, pourvu que ça dure...",
				"/video/handball/hand-jo-fernandez-pourvu-que-ca-dure/recentes/page/1/?sig=91748711217s");
	}

	@Test
	public final void testFindCategory() {
		final Set<CategoryDTO> categories = manager.findCategory();
		assertTrue(!categories.isEmpty());
		boolean contain = false;
		for (final CategoryDTO categoryDTO : categories) {
			assertNotNull(categoryDTO.getName());
			if ("FOOTBALL".equals(categoryDTO.getName())) {
				for (final CategoryDTO subCategoryDTO : categoryDTO.getSubCategories()) {
					if ("Ligue 1".equals(subCategoryDTO.getName())) {
						contain = true;
					}
				}
			}
		}
		assertTrue(contain);
	}

	@Test
	public void testDownload() throws DownloadFailedException, NoSuchDownloaderException {
		final DownloaderDTO downloaders = buildDownloaders();
		manager.download("./test.flv", downloaders, new CmdProgressionListener() {

			@Override
			public void listen(final String progression) {
				LOG.info(progression);
			}
		}, new EpisodeDTO(null, "test", "/video/football/ligue-1/2013-2014/19e-journee/marseille-bordeaux/foot-l1-marseille-bordeaux-2-2/86f34508368s/"));
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
					final CmdProgressionListener listener, final Map<ProxyDTO.ProtocolEnum, ProxyDTO> proxies) throws DownloadFailedException {
				assertTrue(downloadInput.contains("sig"));
			}
		};
		downloaderName2downloader.put("curl", downloader);
		final Map<String, String> downloaderName2BinPath = new HashMap<>();
		downloaderName2BinPath.put("curl", "bin");
		return new DownloaderDTO(null, downloaderName2downloader, downloaderName2BinPath, null, null);
	}
}
