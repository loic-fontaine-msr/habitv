package com.dabi.habitv.tray;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.dabi.habitv.core.config.HabitTvConf;
import com.dabi.habitv.core.dao.GrabConfigDAO;
import com.dabi.habitv.tray.view.window.WindowRootView;

public class WindowLauncher {

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new WindowRootView(new GrabConfigDAO(HabitTvConf.GRABCONFIG_XML_FILE));
	}

}
