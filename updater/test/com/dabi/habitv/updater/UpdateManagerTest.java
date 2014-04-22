package com.dabi.habitv.updater;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.framework.FWKProperties;
import com.dabi.habitv.framework.FrameworkConf;

public class UpdateManagerTest {

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
	}

	@Test
	public final void testProcess() {
		final String site = "http://dabiboo.free.fr/repository";
		String version = FWKProperties.getString(FrameworkConf.VERSION);
		version = "4.0.1";
		final UpdateManager updateManager = new UpdateManager(site, System.getProperty("user.dir") + "/habitv", FrameworkConf.GROUP_ID,
				version, true);
		updateManager.process("provider", "downloader", "exporter");
	}

}
