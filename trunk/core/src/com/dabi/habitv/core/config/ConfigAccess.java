package com.dabi.habitv.core.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.config.entities.SimultaneousTaskNumber;
import com.dabi.habitv.core.task.TaskTypeEnum;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.grabconfig.entities.GrabConfig;

public final class ConfigAccess {

	private static final Logger LOG = Logger.getLogger(ConfigAccess.class);

	private ConfigAccess() {

	}

	public static final String GRAB_CONF_FILE = "grabconfig.xml";

	private static final String GRAB_CONF_XSD = "grab-config.xsd";

	public static final String GRAB_CONF_PACKAGE_NAME = "com.dabi.habitv.grabconfig.entities";

	public static final String CONF_FILE = "config.xml";

	private static final String CONF_XSD = "config.xsd";

	private static final String CONF_PACKAGE_NAME = "com.dabi.habitv.config.entities";

	@SuppressWarnings("unchecked")
	public static Config initConfig() {
		final JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(CONF_PACKAGE_NAME);
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			setValidation(unmarshaller, CONF_XSD);
			return ((JAXBElement<Config>) unmarshaller.unmarshal(new InputStreamReader(new FileInputStream(CONF_FILE), "UTF-8"))).getValue();
		} catch (JAXBException | UnsupportedEncodingException | FileNotFoundException e) {
			throw new TechnicalException(e);
		}
	}

	private static void setValidation(final Unmarshaller unmarshaller, final String xsdFile) {
		final SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Schema schema;
		try {
			schema = schemaFactory.newSchema(new StreamSource(getInputFileInClasspath(xsdFile)));
		} catch (final SAXException e) {
			throw new TechnicalException(e);
		}
		unmarshaller.setSchema(schema);
	}

	public static Map<String, Integer> buildTaskType2ThreadPool(final Config config) {
		final Map<String, Integer> taskType2ThreadPool = new HashMap<>(TaskTypeEnum.values().length);
		for (final SimultaneousTaskNumber simultaneousTaskNumber : config.getSimultaneousTaskNumber()) {
			taskType2ThreadPool.put(simultaneousTaskNumber.getTaskName(), simultaneousTaskNumber.getSize());
		}
		return taskType2ThreadPool;
	}

	public static GrabConfig initGrabConfig() {
		GrabConfig grabConfig = null;
		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(GRAB_CONF_PACKAGE_NAME);
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			setValidation(unmarshaller, GRAB_CONF_XSD);
			grabConfig = ((GrabConfig) unmarshaller.unmarshal(new InputStreamReader(new FileInputStream(GRAB_CONF_FILE), HabitTvConf.ENCODING)));
		} catch (final JAXBException e) {
			throw new TechnicalException(e);
		} catch (final UnsupportedEncodingException e) {
			throw new TechnicalException(e);
		} catch (final FileNotFoundException e) {
			// will return null
			LOG.debug("", e);
		}
		return grabConfig;
	}

	private static InputStream getInputFileInClasspath(final String file) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
	}
}
