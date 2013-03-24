package com.dabi.habitv.provider.tmc;

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

public class TMCRetreiverTest {

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
		assertEquals("d2fcec2208324ba9bdc73e0143c24e66/511960cf", TMCPluginManager.buildToken("8249273", "1360617679", "web"));
		final URL url = new URL(
				"http://www.wat.tv/get/web/9919167?token=f53ae02a07a1a468de31db84d2f12b3a/514dd0ac&domain=videos.tmc.tv&domain2=null&refererURL=%2Fwalker-texas-ranger%2Fsaison-4%2Fwalker-texas-ranger-saison-4-episode-11-le-meilleur-ami-de-l-homme-7886945-848.html&revision=4.1.151&synd=0&helios=1&context=swftmc&pub=1&country=FR&sitepage=tmc.tv%2Fvideos-tmc%2Fcatchup%2Fwalker-texas-ranger%2Fsaison-4%2Fint%2F20130322&lieu=tmc&playerContext=CONTEXT_TMC&getURL=1&version=WIN%2011,6,602,180");
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
