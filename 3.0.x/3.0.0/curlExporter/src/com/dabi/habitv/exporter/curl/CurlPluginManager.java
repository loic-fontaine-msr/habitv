package com.dabi.habitv.exporter.curl;

import com.dabi.habitv.framework.plugin.api.exporter.PluginExporterInterface;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.exception.ExportFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

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
