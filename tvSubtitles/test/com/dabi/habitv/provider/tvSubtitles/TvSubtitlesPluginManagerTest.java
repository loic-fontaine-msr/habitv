package com.dabi.habitv.provider.tvSubtitles;

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
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class TvSubtitlesPluginManagerTest {

	private final TvSubtitlesPluginManager manager = new TvSubtitlesPluginManager();

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
	public final void testFindEpisodeBreakingBad() {
		final Set<EpisodeDTO> episodeList = manager.findEpisode(new CategoryDTO("", "", "tvshow-133-4.html", ""));
		assertTrue(episodeList.size()>0);
	}

	@Test
	public final void testFindEpisode() {
		final Set<EpisodeDTO> episodeList = manager.findEpisode(new CategoryDTO("", "", "tvshow-950-2.html", ""));
		assertTrue(episodeList.size()>0);
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
					final CmdProgressionListener listener) throws DownloadFailedException {
				assertEquals(downloadInput, "http://www.tvsubtitles.net/download-222987.html");
			}
		};
		downloaderName2downloader.put("aria2", downloader);
		final Map<String, String> downloaderName2BinPath = new HashMap<>();
		downloaderName2BinPath.put("aria2", "bin");
		final DownloaderDTO downloaders = new DownloaderDTO(null, downloaderName2downloader, downloaderName2BinPath, null, null);
		final EpisodeDTO episode = new EpisodeDTO(null, "Resolutions", "subtitle-222987.html");
		manager.download("downloadOuput", downloaders, new CmdProgressionListener() {

			@Override
			public void listen(final String progression) {
			}
		}, episode);
	}

	@Test(expected = TechnicalException.class)
	public final void testTechnicalExceptionOnUrlUnknown() {
		manager.setClassLoader(null);
		assertEquals(manager.getName(), TvSubtitlesConf.NAME);
		manager.findEpisode(new CategoryDTO("channel", "name", "identifier", "extension"));
	}
}
