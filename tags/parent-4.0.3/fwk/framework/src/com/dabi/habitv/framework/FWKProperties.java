package com.dabi.habitv.framework;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class FWKProperties {
	private static final String BUNDLE_NAME = "com.dabi.habitv.framework.fwk_properties"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private FWKProperties() {
	}

	public static String getString(final String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (final MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public static String getVersion() {
		final String version = System.getProperty("habitv.version");
		return version == null ? getString(FrameworkConf.VERSION) : version;
	}
}
