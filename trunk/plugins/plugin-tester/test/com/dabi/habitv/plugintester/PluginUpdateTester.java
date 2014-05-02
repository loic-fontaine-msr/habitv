package com.dabi.habitv.plugintester;

import java.util.ArrayList;
import java.util.List;

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
import com.dabi.habitv.plugin.rtmpdump.RtmpDumpDPluginDownloader;
import com.dabi.habitv.plugin.youtube.YoutubePluginDownloader;

public class PluginUpdateTester implements Subscriber<UpdatablePluginEvent> {

	private static final Logger LOG = Logger.getLogger(PluginUpdateTester.class);
	private List<Class<? extends UpdatablePluginInterface>> updatablePlugins;
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
	}

	@After
	public void tearDown() throws Exception {
		updatablePlugins = new ArrayList<>();
		updatablePlugins.add(Aria2PluginDownloader.class);
		updatablePlugins.add(FFMPEGPluginDownloader.class);
		updatablePlugins.add(CurlPluginDownloader.class);
		updatablePlugins.add(RtmpDumpDPluginDownloader.class);
		updatablePlugins.add(YoutubePluginDownloader.class);

		downloaders = new DownloaderPluginHolder("", null, null, "downloadOutputDir", "indexDir");
		publisher = new Publisher<>();
		publisher.attach(this);
	}

	@Test
	public final void test() throws InstantiationException, IllegalAccessException {
		for (final Class<? extends UpdatablePluginInterface> class1 : updatablePlugins) {
			final UpdatablePluginInterface updatablePlugin = class1.newInstance();
			LOG.info(updatablePlugin.getName());
			updatablePlugin.update(publisher, downloaders);
		}
	}

	@Override
	public void update(final UpdatablePluginEvent event) {
		LOG.info(event);
	}

}
