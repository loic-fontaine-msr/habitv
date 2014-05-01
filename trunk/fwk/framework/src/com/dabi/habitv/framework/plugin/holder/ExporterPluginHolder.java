package com.dabi.habitv.framework.plugin.holder;

import java.util.List;
import java.util.Map;

import com.dabi.habitv.framework.plugin.api.PluginExporterInterface;
import com.dabi.habitv.framework.plugin.api.dto.ExportDTO;

public final class ExporterPluginHolder extends AbstractPluginHolder<PluginExporterInterface> {
	private final List<ExportDTO> exporterList;

	public ExporterPluginHolder(final Map<String, PluginExporterInterface> exporterName2exporter, final List<ExportDTO> exporterList) {
		super(exporterName2exporter);
		this.exporterList = exporterList;
	}

	public List<ExportDTO> getExporterList() {
		return exporterList;
	}

}
