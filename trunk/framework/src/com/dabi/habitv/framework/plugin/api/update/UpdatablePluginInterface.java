package com.dabi.habitv.framework.plugin.api.update;

import com.dabi.habitv.framework.plugin.api.PluginBase;


/**
 * Define the interface for an auto updatable plugins.
 * 
 */
public interface UpdatablePluginInterface extends PluginBase {

	void update();
}
