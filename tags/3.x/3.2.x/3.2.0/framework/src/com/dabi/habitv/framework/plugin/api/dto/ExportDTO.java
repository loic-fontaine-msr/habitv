package com.dabi.habitv.framework.plugin.api.dto;

import java.util.List;

public class ExportDTO {

	private final String conditionReference;
	private final String conditionPattern;
	private final String name;
	private final String output;
	private final String cmd;
	private final List<ExportDTO> exporter;

	public ExportDTO(final String conditionReference, final String conditionPattern, final String name, final String output, final String cmd,
			final List<ExportDTO> exporter) {
		super();
		this.conditionReference = conditionReference;
		this.conditionPattern = conditionPattern;
		this.name = name;
		this.output = output;
		this.cmd = cmd;
		this.exporter = exporter;
	}

	public String getConditionReference() {
		return conditionReference;
	}

	public String getConditionPattern() {
		return conditionPattern;
	}

	public String getName() {
		return name;
	}

	public String getOutput() {
		return output;
	}

	public String getCmd() {
		return cmd;
	}

	public List<ExportDTO> getExporter() {
		return exporter;
	}

}
