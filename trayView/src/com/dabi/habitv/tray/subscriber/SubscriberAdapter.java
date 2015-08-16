package com.dabi.habitv.tray.subscriber;

import com.dabi.habitv.api.plugin.pub.Subscriber;
import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchCategoryEvent;
import com.dabi.habitv.core.event.SearchEvent;

public class SubscriberAdapter extends UpdateSubscriberAdapter {

	private final CoreSubscriber coreSubscriber;

	public SubscriberAdapter(final CoreSubscriber coreSubscriber) {
		super(coreSubscriber);
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

	public Subscriber<SearchEvent> buildSearchSubscriber() {
		return new SearchSubscriber();
	}

	public Subscriber<SearchCategoryEvent> buildSearchCategorySubscriber() {
		return new SearchCategorySubscriber();
	}

	public Subscriber<RetreiveEvent> buildRetreiveSubscriber() {
		return new RetreiveSubscriber();
	}

}
