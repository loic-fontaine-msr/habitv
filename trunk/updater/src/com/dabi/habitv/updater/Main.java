package com.dabi.habitv.updater;

import com.dabi.habitv.framework.FWKProperties;
import com.dabi.habitv.framework.FrameworkConf;

public final class Main {

	public static void main(final String[] args) {
		final String site = FrameworkConf.UPDATE_URL;
		final UpdateManager updateManager = new UpdateManager(site, System.getProperty("user.dir"), FrameworkConf.GROUP_ID, FWKProperties.getString(FrameworkConf.VERSION), true);
		updateManager.process("provider", "downloader", "exporter");
	}
}
