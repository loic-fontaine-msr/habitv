package com.dabi.habitv.plugin.ffmpeg;

import com.dabi.habitv.api.plugin.api.PluginExporterInterface;
import com.dabi.habitv.api.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.api.plugin.exception.ExportFailedException;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;

public class FFMPEGPluginExporterManager implements PluginExporterInterface {

	@Override
	public String getName() {
		return FFMPEGConf.NAME;
	}

	@Override
	public ProcessHolder export(final String cmdProcessor, final String cmd)
			throws ExportFailedException {
		try {
			return (new FFMPEGCmdExecutor(cmdProcessor, cmd));
		} catch (final ExecutorFailedException e) {
			throw new ExportFailedException(e);
		}
	}

}
