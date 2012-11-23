package com.dabi.habitv.provider.pluzz;

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

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class PluzzPluginManagerTest {

	private final PluzzPluginManager manager = new PluzzPluginManager();

	private static final Logger LOG = Logger.getLogger(PluzzPluginManagerTest.class);

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
		assertEquals(manager.getName(), PluzzConf.NAME);
	}

	@Test
	public final void testFindEpisode() {
		final CategoryDTO category = new CategoryDTO("channel", "Gumball", "Gumball", "mp4");
		final CategoryDTO surCategory = new CategoryDTO("channel", "Jeunesse", "http://feeds.feedburner.com/Pluzz-Jeunesse?format=xml", "mp4");
		surCategory.addSubCategory(category);
		final Set<EpisodeDTO> episodeList = manager.findEpisode(category);
		for (final EpisodeDTO episode : episodeList) {
			assertNotNull(episode.getUrl());
		}
	}

	@Test
	public final void testFindCategory() {
		final Set<CategoryDTO> categories = manager.findCategory();
		assertTrue(!categories.isEmpty());
		for (final CategoryDTO categoryDTO : categories) {
			assertNotNull(categoryDTO.getName());
			assertTrue(!categoryDTO.getSubCategories().isEmpty());
		}
	}

	@Test
	public void testDownload() throws DownloadFailedException, NoSuchDownloaderException {
		final Map<String, String> downloaderName2BinPath = new HashMap<String, String>();
		downloaderName2BinPath.put(PluzzConf.ASSEMBLER, "ffmpeg");
		final DownloaderDTO downloaders = new DownloaderDTO(null, null, downloaderName2BinPath, null, null);
		manager.download("./test.avi", downloaders, new CmdProgressionListener() {

			@Override
			public void listen(final String progression) {
				LOG.info(progression);
			}
		}, new EpisodeDTO(null, "test", "/france-dom-tom/non-token/non-drm/m3u8/2012/S46/J5/72496473-20121116-840k.m3u8"));
	}
}
