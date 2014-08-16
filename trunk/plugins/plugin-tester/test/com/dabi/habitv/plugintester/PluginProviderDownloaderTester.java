package com.dabi.habitv.plugintester;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.api.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.api.plugin.api.PluginProviderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.plugin.file.FilePluginManager;
import com.dabi.habitv.plugin.rss.RSSPluginManager;
import com.dabi.habitv.provider.arte.ArtePluginManager;
import com.dabi.habitv.provider.beinsport.BeinSportPluginManager;
import com.dabi.habitv.provider.canalplus.CanalPlusPluginProvider;
import com.dabi.habitv.provider.canalplus.D17PluginManager;
import com.dabi.habitv.provider.canalplus.D8PluginManager;
import com.dabi.habitv.provider.clubic.ClubicPluginManager;
import com.dabi.habitv.provider.lequipe.LEquipePluginManager;
import com.dabi.habitv.provider.nrj12.NRJ12PluginManager;
import com.dabi.habitv.provider.pluzz.PluzzPluginManager;
import com.dabi.habitv.provider.tf1.TF1PluginManager;

public class PluginProviderDownloaderTester {

	private static final String TEST_FILE = "testFile.txt";

	private static final Logger LOG = Logger
			.getLogger(PluginProviderDownloaderTester.class);

	private static final int MAX_ATTEMPTS = 10;
	private DownloaderPluginHolder downloaders;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		final Map<String, PluginDownloaderInterface> downloaderName2downloader = new HashMap<>();
		final MockDownloader mockDownloader = new MockDownloader();

		downloaderName2downloader.put(mockDownloader.getName(), mockDownloader);
		downloaderName2downloader.put(FrameworkConf.RTMDUMP, mockDownloader);
		downloaderName2downloader.put(FrameworkConf.CURL, mockDownloader);
		downloaderName2downloader.put(FrameworkConf.FFMPEG, mockDownloader);

		final Map<String, String> downloaderName2BinPath = new HashMap<>();
		downloaders = new DownloaderPluginHolder("cmdProcessor",
				downloaderName2downloader, downloaderName2BinPath,
				"downloadOutputDir", "indexDir", "binDir", "plugins");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void specificCheckDownload() throws DownloadFailedException {
		testEpisode(
				new NRJ12PluginManager(),
				new EpisodeDTO(
						new CategoryDTO("channel", "name", "identifier",
								"extension"),
						"name",
						"http://www.nrj12.fr/allo-nabilla-4503/replay-videos-4535/media/video/895741-en-famille-a-paris-episode-3.html"));
	}

	@Test
	public final void testProviderArte() throws InstantiationException,
			IllegalAccessException, DownloadFailedException {
		testPluginProvider(ArtePluginManager.class, true);
	}

	@Test
	public final void testBeinSport() throws InstantiationException,
			IllegalAccessException, DownloadFailedException {
		testPluginProvider(BeinSportPluginManager.class, true);
	}

	@Test
	public final void testProviderD8() throws InstantiationException,
			IllegalAccessException, DownloadFailedException {
		testPluginProvider(D8PluginManager.class, true);
	}

	@Test
	public final void testProviderD17() throws InstantiationException,
			IllegalAccessException, DownloadFailedException {
		testPluginProvider(D17PluginManager.class, true);
	}

	@Test
	public final void testProviderLEquipe() throws InstantiationException,
			IllegalAccessException, DownloadFailedException {
		testPluginProvider(LEquipePluginManager.class, true);
	}

	@Test
	public final void testProviderNRJ12() throws InstantiationException,
			IllegalAccessException, DownloadFailedException {
		testPluginProvider(NRJ12PluginManager.class, true);
	}

	@Test
	public final void testProviderCanalPlus() throws InstantiationException,
			IllegalAccessException, DownloadFailedException {
		testPluginProvider(CanalPlusPluginProvider.class, false);
	}

	@Test
	public final void testProviderRSS() throws InstantiationException,
			IllegalAccessException, DownloadFailedException {
		final RSSPluginManager plugin = new RSSPluginManager() {

			@Override
			public Set<CategoryDTO> findCategory() {
				checkCategories(super.findCategory());
				return new LinkedHashSet<>(Arrays.asList(new CategoryDTO("rss",
						"dessinemoileco",
						"http://www.dailymotion.com/rss/user/Dessinemoileco/1",
						FrameworkConf.MP4)));
			}
		};
		testPluginProvider(plugin, true);
	}

	@Test
	public final void testProviderFile() throws InstantiationException,
			IllegalAccessException, DownloadFailedException {
		File dest = new File(TEST_FILE);
		dest.delete();
		File done = new File(TEST_FILE + ".done");
		done.renameTo(dest);
		final FilePluginManager plugin = new FilePluginManager() {

			@Override
			public Set<CategoryDTO> findCategory() {
				checkCategories(super.findCategory());
				return new LinkedHashSet<>(Arrays.asList(new CategoryDTO(
						"file", TEST_FILE, TEST_FILE, FrameworkConf.MP4)));
			}
		};
		testPluginProvider(plugin, true);
	}

	@Test
	public final void testProviderTF1() throws InstantiationException,
			IllegalAccessException, DownloadFailedException {
		testPluginProvider(TF1PluginManager.class, true);
	}

	@Test
	public final void testProviderClubic() throws InstantiationException,
			IllegalAccessException, DownloadFailedException {
		testPluginProvider(ClubicPluginManager.class, true);
	}

	@Test
	public final void testProviderPluzz() throws InstantiationException,
			IllegalAccessException, DownloadFailedException {
		testPluginProvider(PluzzPluginManager.class, true);
	}

	private void testPluginProvider(
			final Class<? extends PluginProviderInterface> prDlPluginClass,
			final boolean episodeOnlyOnLeaf) throws InstantiationException,
			IllegalAccessException, DownloadFailedException {
		final PluginProviderInterface plugin = prDlPluginClass.newInstance();
		testPluginProvider(plugin, episodeOnlyOnLeaf);
	}

	private void testPluginProvider(final PluginProviderInterface plugin,
			final boolean episodeOnlyOnLeaf) throws DownloadFailedException {
		LOG.error("---------------------------------------------------------------------");
		LOG.error("searching categories for " + plugin.getName());
		final Set<CategoryDTO> categories = plugin.findCategory();
		checkCategories(categories);

		Set<EpisodeDTO> episodeList = Collections.emptySet();
		int i = 0;
		while (episodeList.isEmpty() && i < MAX_ATTEMPTS) {
			LOG.error("no ep found, searching againg categories for "
					+ plugin.getName());
			final CategoryDTO category = findCategory(episodeOnlyOnLeaf,
					categories);
			LOG.error("search episodes for " + plugin.getName() + "/"
					+ category);
			episodeList = plugin.findEpisode(category);
			i++;
		}
		if (i == MAX_ATTEMPTS) {
			Assert.fail("no ep found in " + MAX_ATTEMPTS + " attempts");
		}

		checkEpisodes(episodeList);

		final EpisodeDTO episode = (new ArrayList<>(episodeList))
				.get(getRandomIndex(episodeList));
		testEpisode(plugin, episode);
	}

	private void testEpisode(final PluginProviderInterface plugin,
			final EpisodeDTO episode) throws DownloadFailedException {
		LOG.error("episode found " + episode);
		LOG.error("episode id " + episode.getId());

		if (PluginDownloaderInterface.class.isInstance(plugin)) {
			final PluginDownloaderInterface pluginDownloader = (PluginDownloaderInterface) plugin;
			pluginDownloader.download(buildDownloadersHolder(episode),
					downloaders);
		}
	}

	private void checkCategories(final Set<CategoryDTO> categories) {
		assertTrue("categorie liste vide", !categories.isEmpty());
		for (final CategoryDTO categoryDTO : categories) {
			if (categoryDTO.getName().isEmpty()
					|| categoryDTO.getId().isEmpty()) {
				LOG.error(categoryDTO);
				Assert.fail("category incorrect : " + categoryDTO);
			}
		}
	}

	private void checkEpisodes(final Set<EpisodeDTO> episodeList) {
		for (final EpisodeDTO episode : episodeList) {
			if (episode.getName().isEmpty() || episode.getId().isEmpty()) {
				LOG.error("episode incorrect : " + episode);
				Assert.fail("episode incorrect");
			}
		}
	}

	private CategoryDTO findCategory(final boolean episodeOnlyOnLeaf,
			final Collection<CategoryDTO> categories) {
		final CategoryDTO category = (new ArrayList<>(categories))
				.get(getRandomIndex(categories));
		if ((episodeOnlyOnLeaf || !category.isDownloadable())
				&& !category.getSubCategories().isEmpty()) {
			return findCategory(episodeOnlyOnLeaf, category.getSubCategories());
		}
		return category;
	}

	private DownloadParamDTO buildDownloadersHolder(final EpisodeDTO episode) {
		return new DownloadParamDTO(episode.getId(), episode.getName(), episode
				.getCategory().getExtension());
	}

	private int getRandomIndex(final Collection<?> collection) {
		final int min = 0;
		final int max = collection.size() - 1;
		final Random rand = new Random();
		return rand.nextInt(max - min + 1) + min;
	}
}
