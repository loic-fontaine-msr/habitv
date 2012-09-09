package com.dabi.habitv.core.publisher;

import com.dabi.habitv.core.event.AbstractEvent;

public interface Subscriber<E extends AbstractEvent> {
	void update(E event);
}
