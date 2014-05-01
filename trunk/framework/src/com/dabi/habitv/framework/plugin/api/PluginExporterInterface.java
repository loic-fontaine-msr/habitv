package com.dabi.habitv.framework.plugin.api;

import com.dabi.habitv.framework.plugin.exception.ExportFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public interface PluginExporterInterface extends PluginBaseInterface {

	void export(final String cmdProcessor, final String cmd, final CmdProgressionListener listener) throws ExportFailedException;

}
