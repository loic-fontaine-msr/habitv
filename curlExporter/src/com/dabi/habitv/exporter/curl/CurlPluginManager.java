package com.dabi.habitv.exporter.curl;

import com.dabi.habitv.framework.plugin.api.PluginExporterInterface;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.exception.ExportFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class CurlPluginManager implements PluginExporterInterface {

	@Override
	public String getName() {
		return CurlConf.NAME;
	}

	@Override
	public void export(final String cmdProcessor, final String cmd, final CmdProgressionListener listener) throws ExportFailedException {
		try {
			(new CurlCmdExecutor(cmdProcessor, cmd, listener)).execute();
		} catch (final ExecutorFailedException e) {
			throw new ExportFailedException(e);
		}
	}

}
