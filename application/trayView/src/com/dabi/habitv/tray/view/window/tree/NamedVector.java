package com.dabi.habitv.tray.view.window.tree;

import java.util.Collection;
import java.util.Vector;

public class NamedVector<E> extends Vector<E> {

	private static final long serialVersionUID = 1L;

	private String name;

	public NamedVector(String name) {
		this.name = name;
	}

	public NamedVector(String name, Collection<E> elements) {
		this.name = name;
		for (E element : elements) {
			add(element);
		}
	}

	public String toString() {
		return "[" + name + "]";
	}
}