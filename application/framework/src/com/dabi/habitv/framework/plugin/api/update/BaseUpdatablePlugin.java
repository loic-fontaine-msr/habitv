package com.dabi.habitv.framework.plugin.api.update;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdExecutor;
import com.dabi.habitv.framework.plugin.utils.EmptyProgressionListener;
import com.dabi.habitv.framework.plugin.utils.OSUtils;
import com.dabi.habitv.framework.plugin.utils.update.UpdatablePluginEvent;
import com.dabi.habitv.framework.plugin.utils.update.ZipExeUpdater;
import com.dabi.habitv.framework.pub.Publisher;

public abstract class BaseUpdatablePlugin implements UpdatablePluginInterface {

	@Override
	public final void update(Publisher<UpdatablePluginEvent> updatePublisher,
			Map<String, String> parameters) {
		new ZipExeUpdater(this, OSUtils.getCurrentDir(),
				FrameworkConf.GROUP_ID, false, updatePublisher, parameters)
				.update("bin", getFilesToUpdate()); // FIXME updateFOlder ?
	}

	protected String getBinParam(final Map<String, String> parameters) {
		String binParam = parameters.get(FrameworkConf.PARAMETER_BIN_PATH);
		if (binParam == null) {
			if (OSUtils.isWindows()) {
				binParam = getWindowsDefaultBuildPath();
			} else {
				binParam = getLinuxDefaultBuildPath();
			}
		}
		return binParam;
	}

	protected abstract String getLinuxDefaultBuildPath();

	protected abstract String getWindowsDefaultBuildPath();

	@Override
	public String getCurrentVersion(final Map<String, String> parameters) {
		final Matcher matcher = getVersionPattern().matcher(
				callGetVersionCmd(parameters));
		final boolean hasMatched = matcher.find();
		String ret = null;
		if (hasMatched) {
			ret = matcher.group(matcher.groupCount());
		}
		return ret;
	}

	protected abstract Pattern getVersionPattern();

	private String callGetVersionCmd(Map<String, String> parameters) {
		CmdExecutor cmdExecutor = new CmdExecutor(
				parameters.get(FrameworkConf.CMD_PROCESSOR),
				getBinParam(parameters) + getVersionParam(), 1000,
				EmptyProgressionListener.INSTANCE) {
			public boolean isSuccess(final String fullOutput) {
				return true;
			}

		};
		try {
			cmdExecutor.execute();
		} catch (ExecutorFailedException e) {
			throw new TechnicalException(e);
		}
		return cmdExecutor.getFullOutput();
	}

	protected abstract String getVersionParam();

	protected String[] getFilesToUpdate() {
		return new String[] { getName() };
	}

}
