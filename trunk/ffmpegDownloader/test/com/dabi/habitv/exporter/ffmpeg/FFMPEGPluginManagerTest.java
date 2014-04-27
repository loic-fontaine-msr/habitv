package com.dabi.habitv.exporter.ffmpeg;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.dabi.habitv.downloader.ffmpeg.FFMPEGPluginManager;
import com.dabi.habitv.framework.plugin.utils.update.UpdatablePluginEvent;
import com.dabi.habitv.framework.pub.Publisher;

public class FFMPEGPluginManagerTest {

	@Test
	public void testUpdate() {
		FFMPEGPluginManager manager = new FFMPEGPluginManager();

		Map<String, String> parameters = new HashMap<>();
		manager.update(new Publisher<UpdatablePluginEvent>(), parameters);
	}

}
