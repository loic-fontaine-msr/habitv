package com.dabi.habitv.exporter.ffmpeg;
import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.plugin.ffmpeg.FFMPEGCmdExecutor;

public final class TestFFMPEG {

	public static void main(final String[] args) {
		try {
			(new FFMPEGCmdExecutor("cmd /c #CMD#", "C:/tools/habiTv/bin/ffmpeg.exe"
					+ " -i http://us-cplus-aka.canal-plus.com/i/geo2/1404/14/nip_NIP_10717_,1500k,.mp4.csmil/master.m3u8 -c copy -absf aac_adtstoasc test.mp4",
					new CmdProgressionListener() {

				@Override
				public void listen(final String progression) {
					System.out.println(progression);
				}
			})).execute();
		} catch (final ExecutorFailedException e) {
			System.err.println(e.getFullOuput());
		}
	}

}
