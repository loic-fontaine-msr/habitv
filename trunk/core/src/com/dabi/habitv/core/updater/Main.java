package com.dabi.habitv.core.updater;


public final class Main {

	public static void main(final String[] args) {
		final UpdateManager updateManager = new UpdateManager(true);
		updateManager.process();
	}
}
