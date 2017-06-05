package com.dabi.habitv.framework.plugin.tpl;

public class TemplateIdBuilder {

	private static final String PARAM_TAG = "§";
	private static final String KEY_VALUE_SEP = ":";
	private static final String PARAMS_SEP = ",";

	private StringBuilder str = new StringBuilder();

	public TemplateIdBuilder() {
		str.append(buildTemplateParam(TemplateUtils.NAME, "Nom", null));
	}

	private static String buildTemplateParam(String id, String name, String defaultValue) {
		return PARAM_TAG + id + (name == null ? "" : (KEY_VALUE_SEP + name)) + (defaultValue == null ? "" : (KEY_VALUE_SEP + defaultValue))
		        + PARAM_TAG;
	}

	public TemplateIdBuilder addTemplateParam(String id, String name, String defaultValue) {
		str.append(PARAMS_SEP + buildTemplateParam(id, name, defaultValue));
		return this;
	}

	public TemplateIdBuilder addComment(String comment) {
		str.append(TemplateUtils.TEMPLATE_ID_COMMENT_SEP + comment);
		return this;
	}

	public String buildID() {
		return str.toString();
	}

}
