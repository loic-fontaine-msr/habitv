package com.dabi.habitv.core.publisher;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractPublisher<T> {
	private final Collection<T> subscribers = new ArrayList<>();

	public void attach(final T subscriber) {
		subscribers.add(subscriber);
	}

	public void notifySubscribers() {
		for (final T subscriber : subscribers) {
			updateSubscriber(subscriber);
		}
	}

	protected abstract void updateSubscriber(T subscriber);
}
