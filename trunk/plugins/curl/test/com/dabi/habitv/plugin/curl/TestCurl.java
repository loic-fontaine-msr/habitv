package com.dabi.habitv.plugin.curl;

import com.dabi.habitv.api.plugin.exception.ExecutorFailedException;

public final class TestCurl {

	public static void main(final String[] args) {
		try {
			(new CurlCmdExecutor(
					"cmd /c",
					"D:/apps/net/captvty-1.7.1/tools/curl.exe"
							+ " http://vod-flash.canalplus.fr/WWWPLUS/PROGRESSIF/1204/BREF_EPISODES_120411_CAN_253528_video_HD.mp4"
							+ " -C - -L -g -A \"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)\" -o \"D:\\apps\\net\\2012_04_16_20_17.mp4\" "))
					.start();
		} catch (final ExecutorFailedException e) {
			System.err.println(e.getFullOuput());
		}
	}

}
