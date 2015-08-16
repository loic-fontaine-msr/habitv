package com.dabi.habitv.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import com.dabi.habitv.api.plugin.exception.TechnicalException;

public class LogUtils {

	public static void updateLog4jConfiguration() {
		Properties props = new Properties();
		try {
			InputStream configStream = ClassLoader.getSystemClassLoader()
					.getResourceAsStream("log4j.properties");
			props.load(configStream);
			configStream.close();
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
		props.setProperty("log4j.appender.file.File", DirUtils.getLogFileSlash());
		LogManager.resetConfiguration();
		PropertyConfigurator.configure(props);
	}
}
