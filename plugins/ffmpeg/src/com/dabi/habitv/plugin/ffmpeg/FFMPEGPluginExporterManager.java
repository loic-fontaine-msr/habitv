package com.dabi.habitv.plugin.ffmpeg;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.api.PluginExporterInterface;
import com.dabi.habitv.api.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.api.plugin.exception.ExportFailedException;

public class FFMPEGPluginExporterManager implements PluginExporterInterface {

	@Override
	public String getName() {
		return FFMPEGConf.NAME;
	}

	@Override
	public void export(final String cmdProcessor, final String cmd, final CmdProgressionListener listener) throws ExportFailedException {
		try {
			(new FFMPEGCmdExecutor(cmdProcessor, cmd, listener)).execute();
		} catch (final ExecutorFailedException e) {
			throw new ExportFailedException(e);
		}
	}

}
