package com.dabi.habitv.updater;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.framework.updater.UpdateManager;

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
		final String site = "http://dabiboo.fr/habitv";
		final UpdateManager updateManager = new UpdateManager(site, System.getProperty("user.dir") + "/habitv");
		updateManager.process();
	}

}
