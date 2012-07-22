package com.dabi.habitv.core.task;

public interface TaskMgrListener {
	void onAllTreatmentDone();

	void onFailed(Throwable t);
}
