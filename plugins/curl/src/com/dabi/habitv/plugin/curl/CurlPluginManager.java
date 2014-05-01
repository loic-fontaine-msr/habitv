package com.dabi.habitv.plugin.curl;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.api.PluginExporterInterface;
import com.dabi.habitv.api.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.api.plugin.exception.ExportFailedException;

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
