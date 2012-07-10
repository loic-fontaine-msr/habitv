package com.dabi.habitv.exporter.curl;

import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.api.PluginExporterInterface;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.exception.ExportFailedException;

public class CurlPluginManager implements PluginExporterInterface {

	@Override
	public String getName() {
		return CurlConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		// no need
	}

	@Override
	public void export(String cmd, CmdProgressionListener listener) throws ExportFailedException {
		try {
			(new CurlCmdExecutor(cmd, listener)).execute();
		} catch (ExecutorFailedException e) {
			throw new ExportFailedException(e);
		}
	}

}
