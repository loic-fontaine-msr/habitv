package com.dabi.habitv.updater;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.framework.updater.UpdateManager;

public final class Main {

	public static void main(final String[] args) {
		final String site = FrameworkConf.UPDATE_URL;
		final UpdateManager updateManager = new UpdateManager(site, System.getProperty("user.dir"));
		updateManager.process(RetrieverUtils.getUrlContent(site + "/toupdate.txt").split("\\n"));
	}
}
