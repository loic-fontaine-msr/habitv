package com.dabi.habitv.api.plugin.api;

import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.pub.Publisher;
import com.dabi.habitv.api.plugin.pub.UpdatablePluginEvent;

/**
 * Define the interface for an auto updatable plugins.
 * 
 */
public interface UpdatablePluginInterface extends PluginBaseInterface {

	void update(Publisher<UpdatablePluginEvent> updatePublisher, DownloaderPluginHolder downloaders);

	String getCurrentVersion(DownloaderPluginHolder downloaders);
}
