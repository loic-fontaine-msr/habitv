package com.dabi.habitv.exporter.cmdexecutor;

import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.api.PluginExporterInterface;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.exception.ExportFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdExecutor;


public final class CmdExecutorExporterManager implements PluginExporterInterface {

	@Override
	public String getName() {
		return CmdExporterConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		// no need
	}
	
	@Override
	public void export(final String cmd, final CmdProgressionListener listener) throws ExportFailedException {
		try {
			new CmdExecutor(cmd, listener).execute();
		} catch (ExecutorFailedException e) {
			throw new ExportFailedException(e);
		}
	}

}
