package com.dabi.habitv.tray.subscriber;

import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchCategoryEvent;
import com.dabi.habitv.core.event.SearchEvent;
import com.dabi.habitv.core.event.UpdatePluginEvent;

public interface CoreSubscriber {

	void update(SearchEvent event);

	void update(RetreiveEvent event);

	void update(SearchCategoryEvent event);

	void update(UpdatePluginEvent event);

}
