package com.dabi.habitv.tray.view.fx;

import java.util.Timer;
import java.util.TimerTask;

import com.dabi.habitv.tray.controller.ViewController;
import com.dabi.habitv.tray.model.HabitTvViewManager;

public abstract class BaseController {

	private static final int SAVE_SPACE = 1000;

	private ViewController controller;

	private HabitTvViewManager model;

	public BaseController() {
		super();
	}

	public final void init(ViewController controller, HabitTvViewManager model) {
		this.controller = controller;
		this.model = model;
		init();
	}

	protected abstract void init();

	protected ViewController getController() {
		return controller;
	}

	protected HabitTvViewManager getModel() {
		return model;
	}

	private Timer timer = new Timer(true);

	private boolean taskPlanned = false;

	private synchronized void setTaskPlanned(boolean taskPlanned) {
		this.taskPlanned = taskPlanned;
	}

	protected void planTaskIfNot(final Runnable task) {
		if (!taskPlanned) {
			setTaskPlanned(true);
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					task.run();
					setTaskPlanned(false);
				}
			}, SAVE_SPACE);
		}
	}
}
