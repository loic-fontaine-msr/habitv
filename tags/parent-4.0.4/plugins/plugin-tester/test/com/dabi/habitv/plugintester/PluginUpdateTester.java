package com.dabi.habitv.plugintester;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.api.plugin.api.UpdatablePluginInterface;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.pub.Publisher;
import com.dabi.habitv.api.plugin.pub.Subscriber;
import com.dabi.habitv.api.plugin.pub.UpdatablePluginEvent;
import com.dabi.habitv.downloader.aria2.Aria2PluginDownloader;
import com.dabi.habitv.plugin.curl.CurlPluginDownloader;
import com.dabi.habitv.plugin.ffmpeg.FFMPEGPluginDownloader;
import com.dabi.habitv.plugin.rtmpdump.RtmpDumpPluginDownloader;
import com.dabi.habitv.plugin.youtube.YoutubePluginDownloader;

public class PluginUpdateTester implements Subscriber<UpdatablePluginEvent> {

	private static final Logger LOG = Logger.getLogger(PluginUpdateTester.class);
	private DownloaderPluginHolder downloaders;
	private Publisher<UpdatablePluginEvent> publisher;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		downloaders = new DownloaderPluginHolder("", null, null, "downloads", "index", "bin", "plugins");
		publisher = new Publisher<>();
		publisher.attach(this);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public final void testAria2() throws InstantiationException, IllegalAccessException {
		testUpdatablePlugin(Aria2PluginDownloader.class);
	}

	@Test
	public final void testFFMPEG() throws InstantiationException, IllegalAccessException {
		testUpdatablePlugin(FFMPEGPluginDownloader.class);
	}

	@Test
	public final void testCurl() throws InstantiationException, IllegalAccessException {
		testUpdatablePlugin(CurlPluginDownloader.class);
	}

	@Test
	public final void testRtmpDump() throws InstantiationException, IllegalAccessException {
		testUpdatablePlugin(RtmpDumpPluginDownloader.class);
	}

	@Test
	public final void testYoutube() throws InstantiationException, IllegalAccessException {
		testUpdatablePlugin(YoutubePluginDownloader.class);
	}

	private void testUpdatablePlugin(final Class<? extends UpdatablePluginInterface> class1) throws InstantiationException, IllegalAccessException {
		final UpdatablePluginInterface updatablePlugin = class1.newInstance();
		updatablePlugin.update(publisher, downloaders);
	}

	@Override
	public void update(final UpdatablePluginEvent event) {
		LOG.info(event);
	}

}
