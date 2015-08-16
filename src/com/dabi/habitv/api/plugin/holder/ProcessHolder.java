package com.dabi.habitv.api.plugin.holder;

public interface ProcessHolder {

	public static final ProcessHolder EMPTY_PROCESS_HOLDER = new ProcessHolder() {

		@Override
		public void stop() {

		}

		@Override
		public String getProgression() {
			return null;
		}

		@Override
		public void start() {
			
		}
	};

	void start();
	
	void stop();
	
	String getProgression();

}
