package com.dabi.habitv.tray.subscriber;

import com.dabi.habitv.api.plugin.pub.UpdatablePluginEvent;
import com.dabi.habitv.core.event.UpdatePluginEvent;

public interface UpdateSubscriber {

	void update(UpdatePluginEvent event);

	void update(UpdatablePluginEvent event);

}
