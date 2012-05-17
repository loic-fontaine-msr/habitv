package com.dabi.habitv.framework.plugin.api;

import com.dabi.habitv.framework.plugin.exception.ExportFailedException;

public interface ExporterPluginInterface extends PluginBase {

	void export(final String cmd, final CmdProgressionListener listener) throws ExportFailedException;

}
