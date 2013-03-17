package com.dabi.habitv.provider.m6w9;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.dto.ProxyDTO;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class M6W9PluginManagerTest {

	M6W9PluginManager manager = new M6W9PluginManager() {

		@Override
		protected Date getServerDate() {
			return new Date(1348406067L);
		}

	};

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
	public final void testFindCategory() {
		final Set<CategoryDTO> categories = manager.findCategory();
		assertTrue(!categories.isEmpty());
		for (final CategoryDTO categoryDTO : categories) {
			assertNotNull(categoryDTO.getName());
		}
	}

	@Test
	public final void testFindEpisode() {
		final CategoryDTO category = new CategoryDTO("channel", "Un gars, une fille", "6026", "mp4");
		final CategoryDTO father = new CategoryDTO("channel", "w9", "w9", "mp4");
		father.addSubCategory(category);
		final Set<EpisodeDTO> episodeList = manager.findEpisode(category);
		for (final EpisodeDTO episode : episodeList) {
			assertNotNull(episode.getUrl());
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
					final CmdProgressionListener listener, final Map<ProxyDTO.ProtocolEnum, ProxyDTO> proxies) throws DownloadFailedException {
				System.out.println(parameters.get(FrameworkConf.PARAMETER_ARGS).replace("#FILE_DEST#", "test.flv").replace("#VIDEO_URL#", downloadInput));
				assertEquals(parameters.get(FrameworkConf.PARAMETER_ARGS),
						"-r \"rtmpe://groupemsix.fcod.llnwd.net/a2883/e1/#VIDEO_URL#?s=1348406&e=1434806&h=688d2d5b2d2050db299abd8bd2f3497c\" -c 1935 -m 10 -o \"#FILE_DEST#\"");
			}
		};
		downloaderName2downloader.put("rtmpdump", downloader);
		final Map<String, String> downloaderName2BinPath = new HashMap<>();
		downloaderName2BinPath.put("rtmpdump", "bin");
		final DownloaderDTO downloaders = new DownloaderDTO(null, downloaderName2downloader, downloaderName2BinPath, null, null);
		final CategoryDTO category = new CategoryDTO("m6", "m6", "m6", "mp4");
		final CategoryDTO subcategory = new CategoryDTO("m6", "m6", "m6", "mp4");
		category.addSubCategory(subcategory);
		// "tools\rtmpdump" -r
		// "rtmpe://groupemsix.fcod.llnwd.net/a2883/e1/mp4:production/regienum/m6_le-1945_426675_220920121945.mp4?s=1348406067&e=1348492467&h=8a560ba69be703f6cd0cc038dc9003f6"
		// -c 1935 -m 10 -o
		// "Le 1945 - Edition du 22 septembre_M6_2012_09_22_19_45.tmp.flv"
		final EpisodeDTO episode = new EpisodeDTO(category, "scène", "11257469");
		manager.download("downloadOuput", downloaders, new CmdProgressionListener() {

			@Override
			public void listen(final String progression) {
			}
		}, episode);
	}

}
