package com.dabi.habitv.framework.plugin.api;

public abstract interface PluginBase {

	String getName();
	
	void setClassLoader(ClassLoader classLoader);
	
}