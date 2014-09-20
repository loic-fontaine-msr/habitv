package com.dabi.habitv.api.plugin.pub;


public interface Subscriber<E extends AbstractEvent> {
	void update(E event);
}
