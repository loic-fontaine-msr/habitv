package com.dabi.habitv.core.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.utils.FileUtils;

public final class ConfigAccess {
	private ConfigAccess() {

	}

	public static final String GRAB_CONF_FILE = "grabconfig.xml";

	public static final String CONF_FILE = "config.xml";

	private static final String CONF_XSD = "config.xsd";

	private static final String CONF_PACKAGE_NAME = "com.dabi.habitv.config.entities";

	@SuppressWarnings("unchecked")
	public static Config initConfig() {
		final JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(CONF_PACKAGE_NAME);
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			FileUtils.setValidation(unmarshaller, CONF_XSD);
			return ((JAXBElement<Config>) unmarshaller.unmarshal(new InputStreamReader(new FileInputStream(CONF_FILE), "UTF-8"))).getValue();
		} catch (JAXBException | UnsupportedEncodingException | FileNotFoundException e) {
			throw new TechnicalException(e);
		}
	}
}
