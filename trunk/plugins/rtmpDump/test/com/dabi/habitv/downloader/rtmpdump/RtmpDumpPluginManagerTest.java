package com.dabi.habitv.downloader.rtmpdump;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.dabi.habitv.framework.plugin.utils.update.UpdatablePluginEvent;
import com.dabi.habitv.framework.pub.Publisher;

public class RtmpDumpPluginManagerTest {

	@Test
	public void testUpdate() {
		RtmpDumpPluginManager rtmpDumpPluginManager = new RtmpDumpPluginManager();
		
		Map<String, String> parameters = new HashMap<>();
		rtmpDumpPluginManager.update(new Publisher<UpdatablePluginEvent>(), parameters);
	}

}
