package com.dabi.habitv.framework.plugin.utils;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class ProcessingThread {

	private static volatile boolean blocker;

	private ProcessingThread() {

	}

	private static final List<Process> processingList = Collections.synchronizedList(new LinkedList<Process>());

	public static void addProcessing(Process process) {
		if (!blocker) {
			processingList.add(process);
		}
	}

	public static void removeProcessing(Process process) {
		if (!blocker) {
			processingList.remove(process);
		}
	}

	public static void killAllProcessing() {
		blocker = true;
		Iterator<Process> it = processingList.iterator();
		while (it.hasNext()) {
			it.next().destroy();
		}
	}

}
