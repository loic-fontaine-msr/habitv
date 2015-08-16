package com.dabi.habitv.plugin.adobeHDS;

import com.dabi.habitv.api.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;

public final class TestAdobeHDS {

	public static void main(final String[] args) throws InterruptedException {
		try {
			final ProcessHolder adobeHDSCmdExecutor = new AdobeHDSCmdExecutor(
					"cmd /c #CMD#",
					AdobeHDSPluginDownloader.getAdobePHPScriptPath()
							+ "--delete --manifest \"http://usp-05.dmcloud.net/52f0ce9994a6f65ac1125958/54f4281d9473993f0790b10b/abs-1425283756.ism/abs.f4m?e=1425612273&st=mVjJ2gTrqA4rGI7crAFRfQ#cell=usp_abs_dc_std\" --outfile \"D:\\divx\\regular\\test.mp4\"");

			(new Thread() {

				@Override
				public void run() {
					adobeHDSCmdExecutor.start();
				}
			}).start();
			String progression = adobeHDSCmdExecutor.getProgression();
			while (progression == null || Double.valueOf(progression) < 100) {
				System.out.println(progression);
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				progression = adobeHDSCmdExecutor.getProgression();
			}

		} catch (final ExecutorFailedException e) {
			System.err.println(e.getFullOuput());
		}
	}
}
