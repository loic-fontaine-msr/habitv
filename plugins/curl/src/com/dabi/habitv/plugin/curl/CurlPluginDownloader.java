package com.dabi.habitv.plugin.curl;

import java.util.Map;
import java.util.regex.Pattern;

import com.dabi.habitv.api.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.api.plugin.api.PluginWithProxyInterface;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.dto.ProxyDTO;
import com.dabi.habitv.api.plugin.dto.ProxyDTO.ProtocolEnum;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.update.BaseUpdatablePlugin;

public class CurlPluginDownloader extends BaseUpdatablePlugin implements
		PluginWithProxyInterface, PluginDownloaderInterface {

	private static final Pattern VERSION_PATTERN = Pattern
			.compile("curl ([\\-0-9A-Za-z.-]*).*");

	private Map<ProtocolEnum, ProxyDTO> protocol2proxy;

	@Override
	public String getName() {
		return CurlConf.NAME;
	}

	@Override
	public ProcessHolder download(final DownloadParamDTO downloadParam,
			final DownloaderPluginHolder downloaders)
			throws DownloadFailedException {
		final String downloaderBin = getBinParam(downloaders);
		String cmd = downloaderBin + CurlConf.CURL_CMD;
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_INPUT,
				downloadParam.getDownloadInput());
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_DESTINATION,
				downloadParam.getDownloadOutput());

		if (protocol2proxy != null) {
			final ProxyDTO httpProxy = protocol2proxy
					.get(ProxyDTO.ProtocolEnum.HTTP);
			if (httpProxy != null) {
				cmd += "--proxy " + httpProxy.getHost() + ":"
						+ httpProxy.getPort();
			}
		}

		try {
			return (new CurlCmdExecutor(downloaders.getCmdProcessor(), cmd));
		} catch (final ExecutorFailedException e) {
			throw new DownloadFailedException(e);
		}
	}

	@Override
	protected Pattern getVersionPattern() {
		return VERSION_PATTERN;
	}

	@Override
	protected String getVersionParam() {
		return " --version";
	}

	@Override
	public void setProxies(final Map<ProtocolEnum, ProxyDTO> protocol2proxy) {
		this.protocol2proxy = protocol2proxy;
	}

	@Override
	public DownloadableState canDownload(final String downloadInput) {
		return downloadInput.startsWith(FrameworkConf.HTTP_PREFIX)
				|| downloadInput.startsWith(FrameworkConf.FTP_PREFIX) ? DownloadableState.POSSIBLE
				: DownloadableState.IMPOSSIBLE;
	}

}
