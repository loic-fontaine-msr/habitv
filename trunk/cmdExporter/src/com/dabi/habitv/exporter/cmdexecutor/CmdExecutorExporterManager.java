package com.dabi.habitv.exporter.cmdexecutor;

import com.dabi.habitv.framework.plugin.api.exporter.PluginExporterInterface;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.exception.ExportFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdExecutor;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public final class CmdExecutorExporterManager implements PluginExporterInterface { // NO_UCD
																					// (unused
																					// code)

	@Override
	public String getName() {
		return CmdExporterConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		// no need
	}

	@Override
	public void export(final String cmdProcessor, final String cmd, final CmdProgressionListener listener) throws ExportFailedException {
		try {
			new CmdExecutor(cmdProcessor, cmd, CmdExporterConf.MAX_HUNG_TIME, listener).execute();
		} catch (ExecutorFailedException e) {
			throw new ExportFailedException(e);
		}
	}

}
