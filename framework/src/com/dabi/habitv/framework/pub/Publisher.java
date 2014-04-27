package com.dabi.habitv.framework.pub;

import java.util.ArrayList;
import java.util.List;

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

	private void notify(final E event) {
		for (final Subscriber<E> subscriber : subscribers) {
			subscriber.update(event);
		}
	}

}
