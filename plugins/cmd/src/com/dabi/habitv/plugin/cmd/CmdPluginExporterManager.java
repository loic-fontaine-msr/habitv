package com.dabi.habitv.plugin.cmd;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.api.PluginExporterInterface;
import com.dabi.habitv.api.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.api.plugin.exception.ExportFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdExecutor;

public final class CmdPluginExporterManager implements PluginExporterInterface {

	@Override
	public String getName() {
		return CmdConf.NAME;
	}

	@Override
	public void export(final String cmdProcessor, final String cmd, final CmdProgressionListener listener) throws ExportFailedException {
		try {
			new CmdExecutor(cmdProcessor, cmd, CmdConf.MAX_HUNG_TIME, listener).execute();
		} catch (final ExecutorFailedException e) {
			throw new ExportFailedException(e);
		}
	}

}
