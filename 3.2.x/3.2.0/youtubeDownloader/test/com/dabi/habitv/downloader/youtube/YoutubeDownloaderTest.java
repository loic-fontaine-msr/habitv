package com.dabi.habitv.downloader.youtube;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class YoutubeDownloaderTest {

	private final YoutubePluginManager manager = new YoutubePluginManager();

	@Test
	public void testFormat() throws UnsupportedEncodingException {
		assertEquals(Integer.valueOf(44), YoutubeDownloader.findBestFormat("http://www.youtube.com/watch?v=gg3ARiiSAfM&feature=plcp"));
		assertEquals("gg3ARiiSAfM", YoutubeDownloader.getYoutubeId("http://www.youtube.com/watch?v=gg3ARiiSAfM&feature=plcp"));
	}

	@Test
	public void testDownload() throws DownloadFailedException {
		manager.download("http://www.youtube.com/watch?v=gg3ARiiSAfM&feature=plcp", "./test.flv", null, new CmdProgressionListener() {

			@Override
			public void listen(final String progression) {
				System.out.println(progression);
			}
		});
	}
	
	@Test
	public void testDownloadProtected() throws DownloadFailedException {

		manager.download("http://www.youtube.com/watch?v=FzRH3iTQPrk", "./test.flv", null, new CmdProgressionListener() {

			@Override
			public void listen(final String progression) {
				System.out.println(progression);
			}
		});
	}
}
