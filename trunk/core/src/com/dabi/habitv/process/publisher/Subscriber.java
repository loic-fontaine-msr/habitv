package com.dabi.habitv.process.publisher;

public interface Subscriber<E extends AbstractEvent> {
	void update(E event);
}
