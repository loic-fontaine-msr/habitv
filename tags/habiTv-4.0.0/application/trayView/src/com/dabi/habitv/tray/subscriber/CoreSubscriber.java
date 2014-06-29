package com.dabi.habitv.tray.subscriber;

import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchCategoryEvent;
import com.dabi.habitv.core.event.SearchEvent;

public interface CoreSubscriber extends UpdateSubscriber {

	void update(SearchEvent event);

	void update(RetreiveEvent event);

	void update(SearchCategoryEvent event);

}
