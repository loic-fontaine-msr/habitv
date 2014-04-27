package com.dabi.habitv.downloader.aria2;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.dabi.habitv.framework.plugin.utils.update.UpdatablePluginEvent;
import com.dabi.habitv.framework.pub.Publisher;

public class Aria2PluginManagerTest {

	@Test
	public void testUpdate() {
		Aria2PluginManager manager = new Aria2PluginManager();

		Map<String, String> parameters = new HashMap<>();
		manager.update(new Publisher<UpdatablePluginEvent>(), parameters);
	}

}
