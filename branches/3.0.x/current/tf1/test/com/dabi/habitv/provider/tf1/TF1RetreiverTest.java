package com.dabi.habitv.provider.tf1;

import static org.junit.Assert.assertEquals;

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
		assertEquals("9e302fdbbb5aa2c17d58587ab6c684d2/50168a49", TF1Retreiver.buildToken("8635245", 1343654473L, "webhd"));
		final URL url = new URL(
				"http://www.wat.tv/get/webhd/8639443?domain=videos.tf1.fr&version=WIN%2010,2,152,32&country=FR&getURL=1&token=e6f3c1df3a252d8f21da772cec1b3192/50167cb4");
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
