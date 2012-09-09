package com.dabi.habitv.core.publisher;

import java.util.ArrayList;
import java.util.List;

import com.dabi.habitv.core.event.AbstractEvent;

public class Publisher<E extends AbstractEvent> {

	private final List<Subscriber<E>> subscribers;

	public Publisher() {
		subscribers = new ArrayList<>();
	}

	public void attach(final Subscriber<E> subscriber) {
		subscribers.add(subscriber);
	}

	public void addNews(final E event) {
		notify(event);
	}

	protected void notify(final E event) {
		for (final Subscriber<E> subscriber : subscribers) {
			subscriber.update(event);
		}
	}

}
