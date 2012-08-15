package com.dabi.habitv.framework.plugin.api.exporter;

import com.dabi.habitv.framework.plugin.api.PluginBase;
import com.dabi.habitv.framework.plugin.exception.ExportFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public interface PluginExporterInterface extends PluginBase {

	void export(final String cmd, final CmdProgressionListener listener) throws ExportFailedException;

}
