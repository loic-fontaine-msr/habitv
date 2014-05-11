package com.dabi.habitv;

import java.awt.SystemTray;

import com.dabi.habitv.console.ConsoleLauncher;
import com.dabi.habitv.framework.plugin.utils.OSUtils;
import com.dabi.habitv.tray.view.window.TreeViewSample;

public class HabitvLauncher {

	public static void main(final String[] args) throws InterruptedException {
		if (OSUtils.isWindows() && SystemTray.isSupported() && (args == null || args.length == 0)) {
			//TrayLauncher.main(args);
			TreeViewSample.main(args);
		} else {
			ConsoleLauncher.main(args);
		}
	}

}
