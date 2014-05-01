package com.dabi.habitv.downloader.youtube;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.dabi.habitv.framework.plugin.utils.update.UpdatablePluginEvent;
import com.dabi.habitv.framework.pub.Publisher;

public class YoutubePluginManagerTest {

	@Test
	public void testUpdate() {
		final YoutubePluginManager manager = new YoutubePluginManager();

		final Map<String, String> parameters = new HashMap<>();
		manager.update(new Publisher<UpdatablePluginEvent>(), parameters);
	}

}
