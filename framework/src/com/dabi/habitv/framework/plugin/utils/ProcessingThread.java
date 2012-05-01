package com.dabi.habitv.framework.plugin.utils;

import java.util.LinkedList;
import java.util.List;

public final class ProcessingThread {

	private ProcessingThread() {

	}

	private static final List<Process> processingList = new LinkedList<>();

	public static synchronized void addProcessing(Process process) {
		processingList.add(process);
	}

	public static synchronized void removeProcessing(Process process) {
		processingList.remove(process);
	}

	public static void killAllProcessing() {
		for (Process process : processingList) {
			process.destroy();
		}
	}

}
