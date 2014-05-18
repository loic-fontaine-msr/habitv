package com.dabi.habitv.tray.subscriber;

import com.dabi.habitv.api.plugin.pub.Subscriber;
import com.dabi.habitv.api.plugin.pub.UpdatablePluginEvent;
import com.dabi.habitv.core.event.UpdatePluginEvent;

public class UpdateSubscriberAdapter {

	private final UpdateSubscriber updateSubscriber;

	public UpdateSubscriberAdapter(final UpdateSubscriber updateSubscriber) {
		super();
		this.updateSubscriber = updateSubscriber;
	}

	class UpdateMainSubscriber implements Subscriber<UpdatePluginEvent> {

		@Override
		public void update(final UpdatePluginEvent event) {
			updateSubscriber.update(event);
		}

	}

	class UpdatablePluginSubscriber implements Subscriber<UpdatablePluginEvent> {

		@Override
		public void update(final UpdatablePluginEvent event) {
			updateSubscriber.update(event);
		}

	}

	public Subscriber<UpdatePluginEvent> buildUpdateSubscriber() {
		return new UpdateMainSubscriber();
	}

	public Subscriber<UpdatablePluginEvent> buildUpdatablePluginSubscriber() {
		return new UpdatablePluginSubscriber();
	}
}
