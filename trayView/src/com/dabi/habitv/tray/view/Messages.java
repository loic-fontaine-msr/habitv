package com.dabi.habitv.tray.view;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "com.dabi.habitv.tray.view.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(final String key, final Object... args) {
		try {
			final MessageFormat formatter = new MessageFormat("");
			formatter.applyPattern(RESOURCE_BUNDLE.getString(key));

			final String output = formatter.format(args);
			return output;
		} catch (final MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
