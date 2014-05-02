package com.dabi.habitv.plugin.curl;

import java.util.Map;
import java.util.regex.Pattern;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.api.plugin.api.PluginWithProxyInterface;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.dto.ProxyDTO;
import com.dabi.habitv.api.plugin.dto.ProxyDTO.ProtocolEnum;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.update.BaseUpdatablePlugin;

public class CurlPluginDownloaderManager extends BaseUpdatablePlugin implements PluginDownloaderInterface, PluginWithProxyInterface {

	private static final Pattern VERSION_PATTERN = Pattern.compile("curl ([\\-0-9A-Za-z.-]*).*");

	private Map<ProtocolEnum, ProxyDTO> protocol2proxy;

	@Override
	public String getName() {
		return CurlConf.NAME;
	}

	@Override
	public void download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders, final CmdProgressionListener listener)
			throws DownloadFailedException {
		final String downloaderBin = getBinParam(downloaders);
		String cmd = downloaderBin + CurlConf.CURL_CMD;
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_INPUT, downloadParam.getDownloadInput());
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_DESTINATION, downloadParam.getDownloadOutput());

		if (protocol2proxy != null) {
			final ProxyDTO httpProxy = protocol2proxy.get(ProxyDTO.ProtocolEnum.HTTP);
			if (httpProxy != null) {
				cmd += "--proxy " + httpProxy.getHost() + ":" + httpProxy.getPort();
			}
		}

		try {
			(new CurlCmdExecutor(downloaders.getCmdProcessor(), cmd, listener)).execute();
		} catch (final ExecutorFailedException e) {
			throw new DownloadFailedException(e);
		}
	}

	@Override
	protected String getLinuxDefaultBuildPath() {
		return CurlConf.DEFAULT_LINUX_BIN_PATH;
	}

	@Override
	protected String getWindowsDefaultBuildPath() {
		return CurlConf.DEFAULT_WINDOWS_BIN_PATH;
	}

	@Override
	protected Pattern getVersionPattern() {
		return VERSION_PATTERN;
	}

	@Override
	protected String getVersionParam() {
		return " -version";
	}

	@Override
	public void setProxies(final Map<ProtocolEnum, ProxyDTO> protocol2proxy) {
		this.protocol2proxy = protocol2proxy;
	}

}
