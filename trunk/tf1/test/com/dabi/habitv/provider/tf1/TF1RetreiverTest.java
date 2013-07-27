package com.dabi.habitv.provider.tf1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TF1RetreiverTest {

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
	public final void testBuildToken() throws IOException {
		//assertEquals("a7dcbaddec5fa76fc3b9c52c558ff4c1/51ec05b6", TF1PluginManager.buildToken("10676343", "1374423122", "web"));
		final URL url = new URL(
				"http://www.wat.tv/get/web/10676343?token=a7dcbaddec5fa76fc3b9c52c558ff4c1/51ec05b6&getURL=1");
		final URLConnection hc = url.openConnection();
		hc.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");

		System.out.println(hc.getContentType());
		final BufferedReader in = new BufferedReader(new InputStreamReader(hc.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
		}
		in.close();
	}

}
