package com.dabi.habitv.tray.controller;

public abstract class AbstractEvenController {

	private final TrayController controller;

	protected AbstractEvenController(final TrayController controller) {
		super();
		this.controller = controller;
	}

	protected TrayController getController() {
		return controller;
	}

}
