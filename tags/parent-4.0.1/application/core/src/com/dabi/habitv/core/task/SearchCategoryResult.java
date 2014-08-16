package com.dabi.habitv.core.task;

import java.util.Collections;
import java.util.Set;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;

public class SearchCategoryResult {

	private final String channel;

	private final Set<CategoryDTO> categoryList;
	
	private final boolean success;

	SearchCategoryResult(final String channel, final Set<CategoryDTO> categoryList) {
		super();
		this.channel = channel;
		this.categoryList = categoryList;
		this.success = true;
	}
	
	public SearchCategoryResult(String channel) {
		super();
		this.channel = channel;
		this.categoryList = Collections.emptySet();
		this.success = false;
	}

	public String getChannel() {
		return channel;
	}

	public Set<CategoryDTO> getCategoryList() {
		return categoryList;
	}

	public boolean isSuccess() {
		return success;
	}

	
}
