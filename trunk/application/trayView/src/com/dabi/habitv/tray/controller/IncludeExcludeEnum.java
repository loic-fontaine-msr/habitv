package com.dabi.habitv.tray.controller;

public enum IncludeExcludeEnum {

	INCLUDE("Inclusion"), EXCLUDE("Exclusion");

	private String lib;

	private IncludeExcludeEnum(String lib) {
		this.lib = lib;
	}

	@Override
	public String toString() {
		return this.lib;
	}

	public boolean isInclude() {
		return this == INCLUDE;
	}

}
