package com.dabi.habitv.framework.plugin.utils;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;

public class EmptyProgressionListener implements CmdProgressionListener {

	public static final CmdProgressionListener INSTANCE = new EmptyProgressionListener();
	
	private EmptyProgressionListener() {
	}
	
	@Override
	public void listen(String progression) {

	}

}
