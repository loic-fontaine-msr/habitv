package com.dabi.habitv.tray.controller;

import com.dabi.habitv.core.event.SearchEvent;
import com.dabi.habitv.core.publisher.Subscriber;

public final class SearchEventController extends AbstractEvenController implements Subscriber<SearchEvent> {

	public SearchEventController(final TrayController trayController) {
		super(trayController);
	}

	@Override
	public void update(final SearchEvent event) {
		switch (event.getState()) {
		case BUILD_INDEX:

			break;
		case CHECKING_EPISODES:

			break;
		case DONE:

			break;
		case ERROR:

			break;
		case IDLE:

			break;
		default:
			break;
		}
	}

}
