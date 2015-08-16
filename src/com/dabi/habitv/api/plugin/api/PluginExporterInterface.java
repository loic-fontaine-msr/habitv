package com.dabi.habitv.api.plugin.api;

import com.dabi.habitv.api.plugin.exception.ExportFailedException;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;

public interface PluginExporterInterface extends PluginBaseInterface {

	ProcessHolder export(final String cmdProcessor, final String cmd)
			throws ExportFailedException;

}
