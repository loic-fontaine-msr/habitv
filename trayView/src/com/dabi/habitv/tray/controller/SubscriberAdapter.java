package com.dabi.habitv.tray.controller;

import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchCategoryEvent;
import com.dabi.habitv.core.event.SearchEvent;
import com.dabi.habitv.core.publisher.Subscriber;

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

	public Subscriber<SearchEvent> getSearchSubscriber() {
		return new SearchSubscriber();
	}

	public Subscriber<SearchCategoryEvent> getSearchCategorySubscriber() {
		return new SearchCategorySubscriber();
	}

	public Subscriber<RetreiveEvent> getRetreiveSubscriber() {
		return new RetreiveSubscriber();
	}
}
