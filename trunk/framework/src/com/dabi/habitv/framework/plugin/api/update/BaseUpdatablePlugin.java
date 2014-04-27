package com.dabi.habitv.framework.plugin.api.update;

import com.dabi.habitv.framework.plugin.utils.update.ZipExeUpdater;

public abstract class BaseUpdatablePlugin implements UpdatablePluginInterface {

	@Override
	public void update() {
		new ZipExeUpdater(this, currentDir, groupId, autoriseSnapshot, updatePublisher).update("bin", getFilesToUpdate());
	}

}
