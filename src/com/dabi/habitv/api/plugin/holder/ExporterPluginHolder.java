package com.dabi.habitv.api.plugin.holder;

import java.util.List;
import java.util.Map;

import com.dabi.habitv.api.plugin.api.PluginExporterInterface;
import com.dabi.habitv.api.plugin.dto.ExportDTO;

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
