package com.dabi.habitv.api.plugin.pub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Publisher<E extends AbstractEvent> {

	private final List<Subscriber<E>> subscribers;

	public Publisher() {
		subscribers = Collections.synchronizedList(new ArrayList<Subscriber<E>>());
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
