package com.dabi.habitv.core.config;

import java.util.List;
import java.util.Map;

import com.dabi.habitv.api.plugin.dto.ExportDTO;
import com.dabi.habitv.api.plugin.dto.ProxyDTO;
import com.dabi.habitv.api.plugin.dto.ProxyDTO.ProtocolEnum;


public interface UserConfig {

	String getProviderPluginDir();

	Map<String, Integer> getTaskDefinition();

	Integer getFileNameCutSize();

	Map<String, Map<ProtocolEnum, ProxyDTO>> getProxy();

	String getCmdProcessor();

	String getDownloaderPluginDir();

	Map<String, String> getDownloader();

	String getIndexDir();

	String getDownloadOuput();

	List<ExportDTO> getExporter();

	Integer getMaxAttempts();

	String getExporterPluginDir();

	Integer getDemonTime();

	boolean updateOnStartup();

	boolean autoriseSnapshot();

}
