package com.dabi.habitv.framework.plugin.api.update;

import java.util.Map;

import com.dabi.habitv.framework.plugin.api.PluginBaseInterface;
import com.dabi.habitv.framework.plugin.utils.update.UpdatablePluginEvent;
import com.dabi.habitv.framework.pub.Publisher;

/**
 * Define the interface for an auto updatable plugins.
 * 
 */
public interface UpdatablePluginInterface extends PluginBaseInterface {

	void update(Publisher<UpdatablePluginEvent> updatePublisher,
			Map<String, String> parameters);

	String getCurrentVersion(final Map<String, String> parameters);
}
