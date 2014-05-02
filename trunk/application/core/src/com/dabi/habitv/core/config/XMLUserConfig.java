package com.dabi.habitv.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.dabi.habitv.api.plugin.dto.ExportDTO;
import com.dabi.habitv.api.plugin.dto.ProxyDTO;
import com.dabi.habitv.api.plugin.dto.ProxyDTO.ProtocolEnum;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.config.entities.Downloader;
import com.dabi.habitv.config.entities.Exporter;
import com.dabi.habitv.config.entities.ObjectFactory;
import com.dabi.habitv.config.entities.Proxy;
import com.dabi.habitv.config.entities.TaskDefinition;
import com.dabi.habitv.framework.plugin.utils.OSUtils;
import com.dabi.habitv.utils.FileUtils;

public class XMLUserConfig implements UserConfig {

	private static final String DEFAULT_PLUGIN_DIR = "plugins";

	private static final int DEFAULT_DEMON_TIME_SEC = 1800;

	private static final int DEFAULT_CUT_SIZE = 40;

	private static final String DEFAULT_DL_OUTPUT = "./downloads/#TVSHOW_NAME#-#EPISODE_NAME_CUT#.#EXTENSION#";

	private static final String DEFAULT_INDEX_DIR = "index";

	private final Config config;

	private XMLUserConfig(final Config config) {
		super();
		this.config = config;
	}

	public static final String GRAB_CONF_FILE = "grabconfig.xml";

	public static final String CONF_FILE = "config.xml";

	private static final String CONF_XSD = "config.xsd";

	public static UserConfig initConfig() {
		Config config;
		final JAXBContext jaxbContext;
		try {
			final File confFile = new File(CONF_FILE);
			if (!confFile.exists()) {
				config = buildConfigFile(confFile);
			} else {
				jaxbContext = JAXBContext.newInstance(Config.class.getPackage().getName());
				final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				FileUtils.setValidation(unmarshaller, CONF_XSD);
				config = (Config) unmarshaller.unmarshal(new InputStreamReader(new FileInputStream(confFile), HabitTvConf.ENCODING));
			}
		} catch (JAXBException | UnsupportedEncodingException | FileNotFoundException e) {
			throw new TechnicalException(e);
		}
		return new XMLUserConfig(config);
	}

	private static Config buildConfigFile(final File file) throws JAXBException {
		final Config config = buildDefaultConfig();
		if (!OSUtils.isWindows()) {
			config.setCmdProcessor("/bin/bash -c #CMD#");
		}

		final JAXBContext jaxbContext = JAXBContext.newInstance(Config.class.getPackage().getName());
		final Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		FileUtils.setValidation(marshaller, CONF_XSD);
		marshaller.marshal(config, file);
		return config;
	}

	private static Config buildDefaultConfig() {
		final Config config = (new ObjectFactory()).createConfig();
		config.setDemonTime(1800);
		config.setDownloadOuput(DEFAULT_DL_OUTPUT);
		config.setUpdateOnStartup(true);
		config.setMaxAttempts(5);
		return config;
	}

	@Override
	public String getPluginDir() {
		return config.getPluginDir() == null ? DEFAULT_PLUGIN_DIR : config.getPluginDir();
	}

	@Override
	public Map<String, Integer> getTaskDefinition() {
		return buildTaskName2PoolSizeMap(config.getTaskDefinition());
	}

	private Map<String, Integer> buildTaskName2PoolSizeMap(final List<TaskDefinition> taskList) {
		final Map<String, Integer> taskName2PoolSizeMap = new HashMap<>();
		for (final TaskDefinition taskDefinition : taskList) {
			taskName2PoolSizeMap.put(taskDefinition.getTaskName(), taskDefinition.getSize());
		}
		return taskName2PoolSizeMap;
	}

	@Override
	public Integer getFileNameCutSize() {
		if (config.getFileNameCutSize() == null) {
			return DEFAULT_CUT_SIZE;
		} else {
			return config.getFileNameCutSize();
		}
	}

	@Override
	public Map<String, Map<ProtocolEnum, ProxyDTO>> getProxy() {
		return buildProxyMap(config.getProxy());
	}

	private Map<String, Map<ProtocolEnum, ProxyDTO>> buildProxyMap(final List<Proxy> configProxyList) {
		final Map<String, Map<ProxyDTO.ProtocolEnum, ProxyDTO>> plugin2protocol2proxy = new HashMap<>();
		if (configProxyList != null && !configProxyList.isEmpty()) {
			for (final Proxy proxy : configProxyList) {
				if (proxy.getPluginSupport() != null) {
					for (final String plugin : proxy.getPluginSupport().getPlugin()) {
						setProxyByPluginName(proxy, plugin, plugin2protocol2proxy);
					}
				} else {
					// default proxy
					setProxyByPluginName(proxy, null, plugin2protocol2proxy);
				}
			}
		}
		return plugin2protocol2proxy;
	}

	private void setProxyByPluginName(final Proxy proxy, final String plugin, final Map<String, Map<ProtocolEnum, ProxyDTO>> plugin2protocol2proxy) {
		Map<ProxyDTO.ProtocolEnum, ProxyDTO> protocol2Proxy;
		protocol2Proxy = plugin2protocol2proxy.get(plugin);
		if (protocol2Proxy == null) {
			protocol2Proxy = new HashMap<>();
			plugin2protocol2proxy.put(plugin, protocol2Proxy);
		}
		protocol2Proxy.put(ProxyDTO.ProtocolEnum.valueOf(proxy.getProtocol()), new ProxyDTO(proxy.getHost(), proxy.getPort()));
	}

	@Override
	public String getCmdProcessor() {
		return config.getCmdProcessor();
	}

	@Override
	public Map<String, String> getDownloader() {
		return buildDownloadersBinPath(config.getDownloader());
	}

	private Map<String, String> buildDownloadersBinPath(final List<Downloader> downloaders) {
		final Map<String, String> downloaderName2BinPath = new HashMap<>(downloaders.size());
		for (final Downloader downloader : downloaders) {
			downloaderName2BinPath.put(downloader.getName(), downloader.getBinPath());
		}
		return downloaderName2BinPath;
	}

	@Override
	public String getIndexDir() {
		return config.getIndexDir() == null ? DEFAULT_INDEX_DIR : config.getIndexDir();
	}

	@Override
	public String getDownloadOuput() {
		return config.getDownloadOuput() == null ? DEFAULT_DL_OUTPUT : config.getDownloadOuput();
	}

	@Override
	public List<ExportDTO> getExporter() {
		return buildExporterListDTO(config.getExporter());
	}

	private List<ExportDTO> buildExporterListDTO(final List<Exporter> exporterList) {
		final List<ExportDTO> exportDTOList = new ArrayList<>(exporterList.size());
		ExportDTO exportDTO;
		String reference;
		String pattern;
		for (final Exporter exporter : exporterList) {

			if (exporter.getCondition() != null) {
				reference = exporter.getCondition().getReference();
				pattern = exporter.getCondition().getPattern();
			} else {
				reference = null;
				pattern = null;
			}
			exportDTO = new ExportDTO(reference, pattern, exporter.getName(), exporter.getOutput(), config.getCmdProcessor(), exporter.getCmd(),
					buildExporterListDTO(exporter.getExporter()));
			exportDTOList.add(exportDTO);
		}
		return exportDTOList;
	}

	@Override
	public Integer getMaxAttempts() {
		return config.getMaxAttempts();
	}

	@Override
	public Integer getDemonTime() {
		if (config.getDemonTime() == null) {
			return DEFAULT_DEMON_TIME_SEC;
		} else {
			return config.getDemonTime();
		}
	}

	@Override
	public boolean updateOnStartup() {
		return config.getUpdateOnStartup() == null ? true : config.getUpdateOnStartup();
	}

	@Override
	public boolean autoriseSnapshot() {
		return config.getAutoriseSnapshot() == null ? false : config.getAutoriseSnapshot();
	}

}
