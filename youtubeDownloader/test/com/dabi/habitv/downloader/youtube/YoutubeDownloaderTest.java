package com.dabi.habitv.downloader.youtube;

import org.junit.Test;

import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class YoutubeDownloaderTest {

	private final YoutubePluginManager manager = new YoutubePluginManager();

	@Test
	public void testDownload() throws DownloadFailedException {
		manager.download("http://www.youtube.com/watch?v=gg3ARiiSAfM&feature=plcp", "./test.flv", null, new CmdProgressionListener() {

			@Override
			public void listen(final String progression) {
				System.out.println(progression);
			}
		}, null);
	}

	@Test
	public void testDownloadProtected() throws DownloadFailedException {

		manager.download("http://www.youtube.com/watch?v=FzRH3iTQPrk", "./test.flv", null, new CmdProgressionListener() {

			@Override
			public void listen(final String progression) {
				System.out.println(progression);
			}
		}, null);
	}
}
