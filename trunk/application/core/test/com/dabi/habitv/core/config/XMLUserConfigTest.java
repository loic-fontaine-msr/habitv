package com.dabi.habitv.core.config;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class XMLUserConfigTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInitConfig() throws IOException {
		File configFile = new File(
				"config.xml");
		configFile.delete();
		Files.copy(new File("testOldConfig.xml").toPath(), configFile.toPath());
		UserConfig config = XMLUserConfig.initConfig();
		assertNotNull(config);
	}

}
