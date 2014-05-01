package com.dabi.habitv.framework.plugin.utils;

public class EmptyProgressionListener implements CmdProgressionListener {

	public static final CmdProgressionListener INSTANCE = new EmptyProgressionListener();
	
	private EmptyProgressionListener() {
	}
	
	@Override
	public void listen(String progression) {

	}

}
