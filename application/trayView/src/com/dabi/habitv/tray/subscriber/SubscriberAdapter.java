package com.dabi.habitv.tray.subscriber;

import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchCategoryEvent;
import com.dabi.habitv.core.event.SearchEvent;
import com.dabi.habitv.core.event.UpdatePluginEvent;
import com.dabi.habitv.framework.plugin.utils.update.UpdatablePluginEvent;
import com.dabi.habitv.framework.pub.Subscriber;

public class SubscriberAdapter {

	private final CoreSubscriber coreSubscriber;

	public SubscriberAdapter(final CoreSubscriber coreSubscriber) {
		super();
		this.coreSubscriber = coreSubscriber;
	}

	class SearchSubscriber implements Subscriber<SearchEvent> {

		@Override
		public void update(final SearchEvent event) {
			coreSubscriber.update(event);
		}

	}

	class RetreiveSubscriber implements Subscriber<RetreiveEvent> {

		@Override
		public void update(final RetreiveEvent event) {
			coreSubscriber.update(event);
		}

	}

	class SearchCategorySubscriber implements Subscriber<SearchCategoryEvent> {

		@Override
		public void update(final SearchCategoryEvent event) {
			coreSubscriber.update(event);
		}

	}

	class UpdateSubscriber implements Subscriber<UpdatePluginEvent> {

		@Override
		public void update(final UpdatePluginEvent event) {
			coreSubscriber.update(event);
		}

	}

	class UpdatablePluginSubscriber implements Subscriber<UpdatablePluginEvent> {

		@Override
		public void update(final UpdatablePluginEvent event) {
			coreSubscriber.update(event);
		}

	}

	public Subscriber<SearchEvent> buildSearchSubscriber() {
		return new SearchSubscriber();
	}

	public Subscriber<SearchCategoryEvent> buildSearchCategorySubscriber() {
		return new SearchCategorySubscriber();
	}

	public Subscriber<RetreiveEvent> buildRetreiveSubscriber() {
		return new RetreiveSubscriber();
	}

	public Subscriber<UpdatePluginEvent> buildUpdateSubscriber() {
		return new UpdateSubscriber();
	}

	public Subscriber<UpdatablePluginEvent> buildUpdatablePluginSubscriber() {
		return new UpdatablePluginSubscriber();
	}
}
