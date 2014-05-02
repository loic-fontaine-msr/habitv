package com.dabi.habitv.plugintester;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.api.PluginProviderDownloaderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.framework.plugin.utils.FileUtils;
import com.dabi.habitv.provider.arte.ArtePluginManager;
import com.dabi.habitv.provider.beinsport.BeinSportPluginManager;
import com.dabi.habitv.provider.d8.D17PluginManager;
import com.dabi.habitv.provider.d8.D8PluginManager;
import com.dabi.habitv.provider.lequipe.LEquipePluginManager;
import com.dabi.habitv.provider.nrj12.NRJ12PluginManager;
import com.dabi.habitv.provider.pluzz.PluzzPluginManager;

public class PluginProviderDownloaderTester {

	private List<Class<? extends PluginProviderDownloaderInterface>> providerDownloaderList;
	private DownloaderPluginHolder downloaders;
	private CmdProgressionListener listener;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		providerDownloaderList = new ArrayList<>();
		providerDownloaderList.add(ArtePluginManager.class);
		providerDownloaderList.add(BeinSportPluginManager.class);
		providerDownloaderList.add(D17PluginManager.class);
		providerDownloaderList.add(D8PluginManager.class);
		providerDownloaderList.add(LEquipePluginManager.class);
		providerDownloaderList.add(NRJ12PluginManager.class);
		providerDownloaderList.add(PluzzPluginManager.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testFindCategory() throws InstantiationException, IllegalAccessException, DownloadFailedException {
		for (final Class<? extends PluginProviderDownloaderInterface> prDlPluginClass : providerDownloaderList) {
			final PluginProviderDownloaderInterface plugin = prDlPluginClass.newInstance();
			final Set<CategoryDTO> categories = plugin.findCategory();
			assertTrue(!categories.isEmpty());

			final CategoryDTO category = (new ArrayList<>(categories)).get(getRandomNumber(0, categories.size()));

			final Set<EpisodeDTO> episodeList = plugin.findEpisode(category);

			for (final EpisodeDTO episode : episodeList) {
				assertTrue(!episode.getName().isEmpty());
				assertTrue(!episode.getId().isEmpty());
			}

			final EpisodeDTO episode = (new ArrayList<>(episodeList)).get(getRandomNumber(0, episodeList.size()));

			plugin.download(buildDownloadersHolder(episode), downloaders, listener);

		}
	}

	private DownloadParamDTO buildDownloadersHolder(final EpisodeDTO episode) {
		return new DownloadParamDTO(episode.getId(), FileUtils.sanitizeFilename(episode.getName()), episode.getCategory().getExtension());
	}

	private int getRandomNumber(final int min, final int max) {
		final Random rand = new Random();
		return rand.nextInt(max - min + 1) + min;
	}

}
