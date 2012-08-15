package com.dabi.habitv.framework.updater;

import org.apache.log4j.Logger;

import com.dabi.habitv.framework.FrameworkConf;

public final class UpdateUpdater {

	private static final Logger LOG = Logger.getLogger(UpdateUpdater.class);

	public static void update() {
		final String site = FrameworkConf.UPDATE_URL;
		try {
			final UpdateManager updateManager = new UpdateManager(site, System.getProperty("user.dir"));
			updateManager.process("updater.jar");
		} catch (final Exception e) {
			LOG.error("updater update error", e);
		}
	}
}
