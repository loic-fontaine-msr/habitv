package com.dabi.habitv.framework.pub;


public interface Subscriber<E extends AbstractEvent> {
	void update(E event);
}
