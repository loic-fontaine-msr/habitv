package com.dabi.habitv.exporter.ffmpeg;

import org.junit.Assert;
import org.junit.Test;

import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class TestFfmpegCmdExecutor {

	private class FakeProgressionListener implements CmdProgressionListener {

		String progression = null;

		@Override
		public void listen(String progression) {
			this.progression = progression;
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
		}

	}

	private class FakeCurlCmdExecutor extends FFMPEGCmdExecutor {

		int ret;

		public FakeCurlCmdExecutor(int ret, String cmd, CmdProgressionListener listener) {
			super(cmd, listener);
			this.ret = ret;
		}

		@Override
		protected Process buildProcess(String cmd) throws ExecutorFailedException {
			String inputString = "% Total    % Received % Xferd  Average Speed   Time    Time     Time  Current"
					+ "                                 Dload  Upload   Total   Spent    Left  Speed\n"
					// +
					// "100 18.6M    12.5     0  100 18.6M      0   928k  0:00:20  0:00:20 --:--:--  952k";
					+ "  7.5 26.4M    0     0    7 2096k      0  1049k  0:00:25  0:00:01  0:00:24 1066k\n"
					+ "100 33.9M    12.5     0    1  368k      0   502k  0:01:09 --:--:--  0:01:09  535k";
			// 1 33.9M 0 0 1 368k 0 502k 0:01:09 --:--:-- 0:01:09 535k
			return new FakeFfmpegProcess(ret, inputString);
		}

	}

	@Test
	public void displayProgressionAndEndProperly() throws ExecutorFailedException {
		FakeProgressionListener fakeProgressionListener = new FakeProgressionListener();
		final FFMPEGCmdExecutor curlCmdExecutor = new FakeCurlCmdExecutor(0, "", fakeProgressionListener);
		curlCmdExecutor.execute();
		Assert.assertEquals("100", fakeProgressionListener.progression);
		Assert.assertTrue(curlCmdExecutor.isSuccess(""));
	}

	@Test
	public void displayProgressionAndEndWithError() {
		FakeProgressionListener fakeProgressionListener = new FakeProgressionListener();
		final FFMPEGCmdExecutor curlCmdExecutor = new FakeCurlCmdExecutor(1, "", fakeProgressionListener);
		try {
			curlCmdExecutor.execute();
			Assert.fail("must be in error");
		} catch (ExecutorFailedException e) {
			Assert.assertEquals("100", fakeProgressionListener.progression);
		}
	}

}
