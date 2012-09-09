package com.dabi.habitv.framework.plugin.api.dto;

public class ConditionTypeDTO {

	private final String reference;

	private final String pattern;

	public ConditionTypeDTO(final String reference, final String pattern) {
		super();
		this.reference = reference;
		this.pattern = pattern;
	}

	public String getReference() {
		return reference;
	}

	public String getPattern() {
		return pattern;
	}

}
