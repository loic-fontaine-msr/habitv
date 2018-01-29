package com.dabi.habitv.provider.globalnews;

import org.junit.Test;

import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.plugintester.BasePluginProviderTester;

public class GlobalNewsPluginManagerTest extends BasePluginProviderTester {
	
	@Test
	public final void test() throws InstantiationException, IllegalAccessException, DownloadFailedException {
		testPluginProvider(GlobalNewsPluginManager.class, true);
	}

}
