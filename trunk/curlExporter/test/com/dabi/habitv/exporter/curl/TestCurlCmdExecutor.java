package com.dabi.habitv.exporter.curl;

import org.junit.Assert;
import org.junit.Test;

import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;

public class TestCurlCmdExecutor {

	private class FakeProgressionListener implements CmdProgressionListener {

		String progression = null;

		@Override
		public void listen(String progression) {
			this.progression = progression;
		}

	}

	private class FakeCurlCmdExecutor extends CurlCmdExecutor {

		int ret;

		public FakeCurlCmdExecutor(int ret, String cmd, CmdProgressionListener listener) {
			super(cmd, listener);
			this.ret = ret;
		}

		@Override
		protected Process buildProcess(String cmd) throws ExecutorFailedException {
			String inputString = "% Total    % Received % Xferd  Average Speed   Time    Time     Time  Current"
					+ "                                 Dload  Upload   Total   Spent    Left  Speed\n"
					+ "100 18.6M    12.5     0  100 18.6M      0   928k  0:00:20  0:00:20 --:--:--  952k";
			return new FakeCurlUploadProcess(ret, inputString);
		}

	}

	@Test
	public void displayProgressionAndEndProperly() throws ExecutorFailedException {
		FakeProgressionListener fakeProgressionListener = new FakeProgressionListener();
		final CurlCmdExecutor curlCmdExecutor = new FakeCurlCmdExecutor(0, "", fakeProgressionListener);
		curlCmdExecutor.execute();
		Assert.assertEquals("12.5", fakeProgressionListener.progression);
		Assert.assertTrue(curlCmdExecutor.isSuccess(""));
	}

	@Test
	public void displayProgressionAndEndWithError() {
		FakeProgressionListener fakeProgressionListener = new FakeProgressionListener();
		final CurlCmdExecutor curlCmdExecutor = new FakeCurlCmdExecutor(1, "", fakeProgressionListener);
		try {
			curlCmdExecutor.execute();
			Assert.fail("must be in error");
		} catch (ExecutorFailedException e) {
			Assert.assertEquals("12.5", fakeProgressionListener.progression);
		}
	}

}
