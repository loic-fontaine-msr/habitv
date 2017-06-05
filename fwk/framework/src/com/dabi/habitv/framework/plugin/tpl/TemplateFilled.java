package com.dabi.habitv.framework.plugin.tpl;

import java.util.List;

public class TemplateFilled {

	private String comment;
	
	private List<TemplateParam> params;

	public TemplateFilled(String comment, List<TemplateParam> params) {
		super();
		this.comment = comment;
		this.params = params;
	}

	public String getComment() {
		return comment;
	}

	public List<TemplateParam> getParams() {
		return params;
	}
	
}
