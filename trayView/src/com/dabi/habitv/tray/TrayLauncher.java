package com.dabi.habitv.tray;

import com.dabi.habitv.tray.controller.TrayController;
import com.dabi.habitv.tray.model.HabitTvTrayModel;
import com.dabi.habitv.tray.view.HabiTvTrayView;

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
