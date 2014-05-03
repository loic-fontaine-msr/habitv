package com.dabi.habitv.framework.plugin.api.update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dabi.habitv.api.plugin.api.UpdatablePluginInterface;
import com.dabi.habitv.api.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.pub.Publisher;
import com.dabi.habitv.api.plugin.pub.UpdatablePluginEvent;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.utils.CmdExecutor;
import com.dabi.habitv.framework.plugin.utils.EmptyProgressionListener;
import com.dabi.habitv.framework.plugin.utils.OSUtils;
import com.dabi.habitv.framework.plugin.utils.update.ZipExeUpdater;

public abstract class BaseUpdatablePlugin implements UpdatablePluginInterface {

	@Override
	public final void update(final Publisher<UpdatablePluginEvent> updatePublisher, final DownloaderPluginHolder downloaders) {
		new ZipExeUpdater(this, downloaders.getBinDir(), FrameworkConf.GROUP_ID, false, updatePublisher, downloaders).update(getFilesToUpdate());
	}

	protected String getBinParam(final DownloaderPluginHolder downloaders) {
		String binParam = downloaders.getBinPath(getName());
		if (binParam == null) {
			if (OSUtils.isWindows()) {
				binParam = downloaders.getBinDir() + "\\" + getWindowsDefaultExe();
			} else {
				binParam = getLinuxDefaultBuildPath();
			}
		}
		return binParam;
	}

	protected String getLinuxDefaultBuildPath() {
		return getName();
	}

	protected String getWindowsDefaultExe() {
		return getName() + ".exe";
	}

	@Override
	public String getCurrentVersion(final DownloaderPluginHolder downloaders) {
		final Matcher matcher = getVersionPattern().matcher(callGetVersionCmd(downloaders));
		final boolean hasMatched = matcher.find();
		String ret = null;
		if (hasMatched) {
			ret = matcher.group(matcher.groupCount());
		}
		return ret;
	}

	protected abstract Pattern getVersionPattern();

	private String callGetVersionCmd(final DownloaderPluginHolder downloaders) {
		final CmdExecutor cmdExecutor = new CmdExecutor(downloaders.getCmdProcessor(), getBinParam(downloaders) + getVersionParam(), 1000,
				EmptyProgressionListener.INSTANCE) {
			@Override
			public boolean isSuccess(final String fullOutput) {
				return true;
			}

		};
		try {
			cmdExecutor.execute();
		} catch (final ExecutorFailedException e) {
			throw new TechnicalException(e);
		}
		return cmdExecutor.getFullOutput();
	}

	protected abstract String getVersionParam();

	protected String[] getFilesToUpdate() {
		return new String[] { getName() };
	}

}
