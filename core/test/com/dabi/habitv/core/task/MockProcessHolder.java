package com.dabi.habitv.core.task;

import com.dabi.habitv.api.plugin.holder.ProcessHolder;

public class MockProcessHolder implements ProcessHolder{

	private String progression;

	@Override
	public void stop() {
		
	}

	@Override
	public String getProgression() {
		return progression;
	}

	public void setProgression(String progression) {
		this.progression = progression;
	}

	@Override
	public void start() {
		
	}
	
}
