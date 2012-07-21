package com.dabi.habitv.process.mgr;

public interface TaskMgrListener {
	void onAllTreatmentDone();

	void onFailed(Throwable t);
}
