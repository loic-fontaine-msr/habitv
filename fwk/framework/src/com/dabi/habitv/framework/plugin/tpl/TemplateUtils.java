package com.dabi.habitv.framework.plugin.tpl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.StatusEnum;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.FrameworkConf;
import com.google.common.base.Joiner;

public class TemplateUtils {

	private static final String KEY_VALUE_SEP = "=";

	private static final String PARAMS_SEP = ",";

	public static final String NAME = "NAME";

	public static final String TEMPLATE_ID_COMMENT_SEP = "!!!";

	public static Map<String, String> getParamValues(String id) {
		Map<String, String> values = new HashMap<>();
		for (String idValue : id.split(PARAMS_SEP)) {
			if (idValue.contains(KEY_VALUE_SEP)) {
				String[] idValueTab = idValue.split(KEY_VALUE_SEP);
				if (idValueTab.length > 1) {
					values.put(idValueTab[0], idValueTab[1]);
				}
			}
		}
		return values;
	}

	public static CategoryDTO buildSampleCat(String plugin, String name, Map<String, String> params) {
		CategoryDTO categoryDTO = new CategoryDTO(plugin, name, buildIDFromParams(params), FrameworkConf.MP4);
		categoryDTO.setDownloadable(true);
		categoryDTO.setState(StatusEnum.USER);
		return categoryDTO;
	}

	private static String buildIDFromParams(Map<String, String> params) {
		List<String> paramsList = new ArrayList<>(params.size());
		for (Entry<String, String> entry : params.entrySet()) {
			paramsList.add(entry.getKey() + KEY_VALUE_SEP + entry.getValue());
		}
		return Joiner.on(PARAMS_SEP).join(paramsList);
	}

	public static CategoryDTO buildCategoryTemplate(String plugin, String name, String id) {
		final CategoryDTO categoryDTO = new CategoryDTO(plugin, name, id, FrameworkConf.MP4);
		categoryDTO.setTemplate(true);
		categoryDTO.setDownloadable(false);
		categoryDTO.setState(StatusEnum.USER);
		return categoryDTO;
	}

	public static TemplateFilled parseTemplateFilled(String idCat) {
		String[] config = idCat.split(TemplateUtils.TEMPLATE_ID_COMMENT_SEP);
		String params = config[0];
		String text = config[1];
		String[] paramsTab = params.split(",");
		List<TemplateParam> paramsList = new ArrayList<>(paramsTab.length);
		for (String param : paramsTab) {
			String[] paramTab = param.replaceAll("\\§", "").split(":");
			String id = paramTab[0];
			String name = paramTab.length > 1 ? paramTab[1] : id;
			String defaultValue = paramTab.length > 2 ? paramTab[2] : "";
			paramsList.add(new TemplateParam(id, name, defaultValue));
		}
		return new TemplateFilled(text, paramsList);
	}
	
	public static String buildIdValues(Map<String, String> values) {
		StringBuilder str = new StringBuilder();
		for (Entry<String, String> entry : values.entrySet()) {
			try {
				str.append(PARAMS_SEP + entry.getKey() + KEY_VALUE_SEP + URLEncoder.encode(entry.getValue(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new TechnicalException(e);
			}
		}
		return str.toString();
	}
}
