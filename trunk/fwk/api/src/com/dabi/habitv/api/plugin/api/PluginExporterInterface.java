package com.dabi.habitv.api.plugin.api;

import com.dabi.habitv.api.plugin.exception.ExportFailedException;


public interface PluginExporterInterface extends PluginBaseInterface {

	void export(final String cmdProcessor, final String cmd, final CmdProgressionListener listener) throws ExportFailedException;

}
