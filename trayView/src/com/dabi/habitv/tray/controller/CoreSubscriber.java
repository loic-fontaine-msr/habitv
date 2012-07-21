package com.dabi.habitv.tray.controller;

import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchCategoryEvent;
import com.dabi.habitv.core.event.SearchEvent;

public interface CoreSubscriber {

	void update(SearchEvent event);

	void update(RetreiveEvent event);

	void update(SearchCategoryEvent event);

}
