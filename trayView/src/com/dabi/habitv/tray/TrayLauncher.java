package com.dabi.habitv.tray;

import org.apache.log4j.Logger;

import com.dabi.habitv.tray.controller.TrayController;
import com.dabi.habitv.tray.model.HabitTvTrayModel;
import com.dabi.habitv.tray.view.HabiTvTrayView;

public final class TrayLauncher {

	private static final Logger LOG = Logger.getLogger(TrayLauncher.class);

	private TrayLauncher() {

	}

	public static void main(final String[] args) {
		try {
			final HabitTvTrayModel model = new HabitTvTrayModel();
			final TrayController controller = new TrayController(model);
			final HabiTvTrayView view = new HabiTvTrayView(controller);
			model.attach(view);
			model.attach(controller);
			controller.startDownloadCheckDemon();
		} catch (final Exception e) {
			LOG.error("", e);
			System.exit(1);
		}
	}

}
