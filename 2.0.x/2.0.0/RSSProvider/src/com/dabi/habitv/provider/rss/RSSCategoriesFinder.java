package com.dabi.habitv.provider.rss;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;

public final class RSSCategoriesFinder {

	private RSSCategoriesFinder() {

	}

	public static Set<CategoryDTO> findCategory(final ClassLoader classLoader) {
		final Set<CategoryDTO> categoryList = new HashSet<>();
		List<String> include = new ArrayList<String>(1);
		include.add("Add include pattern");
		List<String> exclude = new ArrayList<String>(1);
		exclude.add("Add exclude pattern");
		categoryList.add(new CategoryDTO(RSSConf.NAME, "Give RSS Label Here", "Give RSS Url Here", include, exclude,"Give files extension Here"));
		return categoryList;
	}

}
