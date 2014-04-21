package com.dabi.habitv.core.task;

import java.util.Set;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;

public class SearchCategoryResult {

	private final String channel;

	private final Set<CategoryDTO> categoryList;

	SearchCategoryResult(final String channel, final Set<CategoryDTO> categoryList) {
		super();
		this.channel = channel;
		this.categoryList = categoryList;
	}

	public String getChannel() {
		return channel;
	}

	public Set<CategoryDTO> getCategoryList() {
		return categoryList;
	}

}
