package com.dabi.habitv.framework.plugin.tpl;

public class TemplateParam {

	private String id;
	private String name;
	private String defaultValue;

	public TemplateParam(String id, String name, String defaultValue) {
		super();
		this.id = id;
		this.name = name;
		this.defaultValue = defaultValue;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

}
