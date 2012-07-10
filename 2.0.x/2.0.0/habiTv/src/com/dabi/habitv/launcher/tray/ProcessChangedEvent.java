package com.dabi.habitv.launcher.tray;

import java.util.EventObject;

import com.dabi.habitv.launcher.tray.model.HabitTvTrayModel;
import com.dabi.habitv.launcher.tray.model.ProcessStateEnum;

public class ProcessChangedEvent extends EventObject {

	private static final long serialVersionUID = -7279520721917340700L;

	private final ProcessStateEnum state;
	
	private final String info;

	public ProcessChangedEvent(final HabitTvTrayModel source, final ProcessStateEnum state, final String info) {
		super(source);
		this.state = state;
		this.info = info;
	}

	public ProcessStateEnum getState() {
		return state;
	}

	public String getInfo() {
		return info;
	}

}
