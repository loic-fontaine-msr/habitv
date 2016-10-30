package com.dabi.habitv.plugintester;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.plugin.file.FilePluginManager;
import com.dabi.habitv.plugin.rss.RSSPluginManager;
import com.dabi.habitv.provider.arte.ArtePluginManager;
import com.dabi.habitv.provider.canalplus.CanalPlusPluginProvider;
import com.dabi.habitv.provider.canalplus.D17PluginManager;
import com.dabi.habitv.provider.canalplus.D8PluginManager;
import com.dabi.habitv.provider.clubic.ClubicPluginManager;
import com.dabi.habitv.provider.lequipe.LEquipePluginManager;
import com.dabi.habitv.provider.nrj12.NRJ12PluginManager;
import com.dabi.habitv.provider.pluzz.PluzzPluginManager;
import com.dabi.habitv.provider.sfr.SFRPluginManager;
import com.dabi.habitv.provider.wat.WatPluginManager;

public class PluginProviderDownloaderTester extends BasePluginProviderTester {

	@Test
	public final void testProviderArte()
			throws InstantiationException, IllegalAccessException, DownloadFailedException {
		testPluginProvider(ArtePluginManager.class, true);
	}

	@Test
	public final void testProviderD8() throws InstantiationException, IllegalAccessException, DownloadFailedException {
		testPluginProvider(D8PluginManager.class, true);
	}

	@Test
	public final void testProviderD17() throws InstantiationException, IllegalAccessException, DownloadFailedException {
		testPluginProvider(D17PluginManager.class, true);
	}

	@Test
	public final void testProviderLEquipe()
			throws InstantiationException, IllegalAccessException, DownloadFailedException {
		testPluginProvider(LEquipePluginManager.class, true);
	}

	@Test
	public final void testProviderNRJ12()
			throws InstantiationException, IllegalAccessException, DownloadFailedException {
		testPluginProvider(NRJ12PluginManager.class, true);
	}

	@Test
	public final void testProviderCanalPlus()
			throws InstantiationException, IllegalAccessException, DownloadFailedException {
		testPluginProvider(CanalPlusPluginProvider.class, false);
	}

	@Test
	public final void specificFindEpCanal() throws DownloadFailedException {
		Set<EpisodeDTO> ep;
		// ep= new CanalPlusPluginProvider().findEpisode(new
		// CategoryDTO("channel", "name",
		// "http://service.mycanal.fr/page/6d4d60f9c6b98415e0d48f6ab8c027a1/1276.json?cache=300000",
		// "extension"));
		// Assert.assertNotNull(ep);
		// LOG.error(ep);

		ep = new CanalPlusPluginProvider().findEpisode(new CategoryDTO("c+", "Football",
				"http://service.mycanal.fr/page/0e269fd07e3e10af4099b87b77825782/4086.json?cache=300000", "mp4"));
		LOG.error(ep);
	}

	@Test
	public final void testProviderRSS() throws InstantiationException, IllegalAccessException, DownloadFailedException {
		final RSSPluginManager plugin = new RSSPluginManager() {

			@Override
			public Set<CategoryDTO> findCategory() {
				checkCategories(super.findCategory());
				return new LinkedHashSet<>(Arrays.asList(new CategoryDTO("rss", "dessinemoileco",
						"http://www.dailymotion.com/rss/user/Dessinemoileco/1", FrameworkConf.MP4)));
			}
		};
		testPluginProvider(plugin, true);
	}

	@Test
	public final void testProviderFile()
			throws InstantiationException, IllegalAccessException, DownloadFailedException {
		File dest = new File(TEST_FILE);
		dest.delete();
		File done = new File(TEST_FILE + ".done");
		done.renameTo(dest);
		final FilePluginManager plugin = new FilePluginManager() {

			@Override
			public Set<CategoryDTO> findCategory() {
				checkCategories(super.findCategory());
				return new LinkedHashSet<>(
						Arrays.asList(new CategoryDTO("file", TEST_FILE, TEST_FILE, FrameworkConf.MP4)));
			}
		};
		testPluginProvider(plugin, true);
	}

	@Test
	public final void testProviderWat() throws InstantiationException, IllegalAccessException, DownloadFailedException {
		testPluginProvider(WatPluginManager.class, true);
	}

	@Test
	public final void testProviderClubic()
			throws InstantiationException, IllegalAccessException, DownloadFailedException {
		testPluginProvider(ClubicPluginManager.class, true);
	}

	@Test
	public final void testProviderPluzz()
			throws InstantiationException, IllegalAccessException, DownloadFailedException {
		testPluginProvider(PluzzPluginManager.class, true);
	}

	@Test
	public final void specificFindEpPluzz() throws DownloadFailedException {
		Set<EpisodeDTO> ep;
		ep = new PluzzPluginManager()
				.findEpisode(new CategoryDTO("pluzz", "santa_diabla_fo", "santa_diabla_fo", "mp4"));
		LOG.error(ep);
	}

	@Test
	public final void testProviderSfr() throws InstantiationException, IllegalAccessException, DownloadFailedException {
		testPluginProvider(SFRPluginManager.class, true);
	}
}
