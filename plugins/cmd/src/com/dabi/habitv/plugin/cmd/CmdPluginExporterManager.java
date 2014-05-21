package com.dabi.habitv.plugin.cmd;

import com.dabi.habitv.api.plugin.api.PluginExporterInterface;
import com.dabi.habitv.api.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.api.plugin.exception.ExportFailedException;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;
import com.dabi.habitv.framework.plugin.utils.CmdExecutor;

public final class CmdPluginExporterManager implements PluginExporterInterface {

	@Override
	public String getName() {
		return CmdConf.NAME;
	}

	@Override
	public ProcessHolder export(final String cmdProcessor, final String cmd)
			throws ExportFailedException {
		try {
			return new CmdExecutor(cmdProcessor, cmd, CmdConf.MAX_HUNG_TIME);
		} catch (final ExecutorFailedException e) {
			throw new ExportFailedException(e);
		}
	}

}
