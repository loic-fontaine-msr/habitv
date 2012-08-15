package com.dabi.habitv.provider.m6w9;

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
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class M6W9PluginManagerTest {

	M6W9PluginManager manager = new M6W9PluginManager();

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
	public final void testFindEpisode() {
		final CategoryDTO category = new CategoryDTO("channel", "Un gars, une fille", "6026", "mp4");
		final CategoryDTO father = new CategoryDTO("channel", "w9", "w9", "mp4");
		father.addSubCategory(category);
		final Set<EpisodeDTO> episodeList = manager.findEpisode(category);
		boolean contain = false;
		for (final EpisodeDTO episode : episodeList) {
			if (episode.getName().equals("Best of - La bouffe (1)")) {
				assertEquals("mp4:production/w9replay/w9_un-gars-une-fille_345860_210620121730.mp4", episode.getUrl());
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
				return "rtmpdump";
			}

			@Override
			public void download(final String downloadInput, final String downloadDestination, final Map<String, String> parameters,
					final CmdProgressionListener listener) throws DownloadFailedException {
				assertTrue(downloadInput.contains("fille"));
			}
		};
		downloaderName2downloader.put("rtmpdump", downloader);
		final Map<String, String> downloaderName2BinPath = new HashMap<>();
		downloaderName2BinPath.put("rtmpdump", "bin");
		final DownloaderDTO downloaders = new DownloaderDTO(downloaderName2downloader, downloaderName2BinPath, null, null);
		final CategoryDTO category = new CategoryDTO("w9", "w9", "w9", "mp4");
		final CategoryDTO subcategory = new CategoryDTO("w9", "w9", "w9", "mp4");
		category.addSubCategory(subcategory);
		final EpisodeDTO episode = new EpisodeDTO(category, "Best of - La bouffe (1)", "mp4:production/w9replay/w9_un-gars-une-fille_345860_210620121730.mp4");
		manager.download("downloadOuput", downloaders, new CmdProgressionListener() {

			@Override
			public void listen(final String progression) {
			}
		}, episode);
	}

}
