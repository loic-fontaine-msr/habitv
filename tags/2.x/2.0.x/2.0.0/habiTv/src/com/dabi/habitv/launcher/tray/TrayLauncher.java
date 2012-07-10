package com.dabi.habitv.launcher.tray;

import com.dabi.habitv.launcher.tray.controller.TrayController;
import com.dabi.habitv.launcher.tray.model.HabitTvTrayModel;
import com.dabi.habitv.launcher.tray.view.HabiTvTrayView;

public class TrayLauncher {

	private TrayLauncher() {

	}

	public static void main(final String[] args) {
		HabitTvTrayModel model = new HabitTvTrayModel();
		TrayController controller = new TrayController(model);
		HabiTvTrayView view = new HabiTvTrayView(controller);
		model.addListener(view);
		controller.startDownloadCheckDemon();
	}

}
