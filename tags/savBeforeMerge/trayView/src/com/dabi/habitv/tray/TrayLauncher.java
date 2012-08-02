package com.dabi.habitv.tray;

import com.dabi.habitv.tray.controller.TrayController;
import com.dabi.habitv.tray.model.HabitTvTrayModel;
import com.dabi.habitv.tray.view.HabiTvTrayView;

public final class TrayLauncher {

	private TrayLauncher() {

	}

	public static void main(final String[] args) {
		final HabitTvTrayModel model = new HabitTvTrayModel();
		final TrayController controller = new TrayController(model);
		final HabiTvTrayView view = new HabiTvTrayView(controller);
		model.attach(view);
		controller.startDownloadCheckDemon();
	}

}
