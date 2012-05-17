package com.dabi.habitv.exporter.ffmpeg;

import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.api.ExporterPluginInterface;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.exception.ExportFailedException;

public class FFMPEGManager implements ExporterPluginInterface {

	@Override
	public String getName() {
		return FFMPEGConf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {
		// no need
	}

	@Override
	public void export(final String cmd, final CmdProgressionListener listener) throws ExportFailedException {
		try {
			(new FFMPEGCmdExecutor(cmd, listener)).execute();
		} catch (ExecutorFailedException e) {
			throw new ExportFailedException(e);
		}
	}

}
