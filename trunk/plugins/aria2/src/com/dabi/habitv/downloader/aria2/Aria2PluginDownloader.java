package com.dabi.habitv.downloader.aria2;

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

public class Aria2PluginDownloader extends BaseUpdatablePlugin implements PluginDownloaderInterface, PluginWithProxyInterface {

	private static final Pattern VERSION_PATTERN = Pattern.compile("aria2 version ([0-9A-Za-z.-]*).*");
	private Map<ProtocolEnum, ProxyDTO> protocol2proxy;

	@Override
	public String getName() {
		return Aria2Conf.NAME;
	}

	@Override
	public void download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders, final CmdProgressionListener listener)
			throws DownloadFailedException {

		final String binParam = getBinParam(downloaders);
		String cmd = binParam + " ";
		final String cmdParam = downloadParam.getParam(FrameworkConf.PARAMETER_ARGS);
		if (cmdParam == null) {
			cmd += Aria2Conf.CMD;
		} else {
			cmd += cmdParam;
		}
		if (protocol2proxy != null) {
			final ProxyDTO httpProxy = protocol2proxy.get(ProxyDTO.ProtocolEnum.HTTP);
			if (httpProxy != null) {
				cmd += " --all-proxy='http://" + httpProxy.getHost() + ":" + httpProxy.getPort() + "'";
			}
		}
		cmd = cmd.replaceFirst(FrameworkConf.DOWNLOAD_INPUT, downloadParam.getDownloadInput());

		final int lastSlashIndex = downloadParam.getDownloadOutput().lastIndexOf('/');
		final String fileName = downloadParam.getDownloadOutput().substring(lastSlashIndex + 1, downloadParam.getDownloadOutput().length());
		final String dirDest = downloadParam.getDownloadOutput().substring(0, lastSlashIndex);

		cmd = cmd.replaceFirst(Aria2Conf.FILE_NAME, fileName);
		cmd = cmd.replaceFirst(Aria2Conf.DIR_DEST, dirDest);
		try {
			(new Aria2CmdExecutor(downloaders.getCmdProcessor(), cmd, listener)).execute();
		} catch (final ExecutorFailedException e) {
			throw new DownloadFailedException(e);
		}
	}

	@Override
	protected String getWindowsDefaultExe() {
		return Aria2Conf.DEFAULT_WINDOWS_EXE;
	}

	@Override
	protected Pattern getVersionPattern() {
		return VERSION_PATTERN;
	}

	@Override
	protected String[] getFilesToUpdate() {
		return new String[] { "aria2c" };
	}

	@Override
	protected String getVersionParam() {
		return " -v";
	}

	@Override
	public void setProxies(final Map<ProtocolEnum, ProxyDTO> protocol2proxy) {
		this.protocol2proxy = protocol2proxy;
	}

	@Override
	public DownloadableState canDownload(final String downloadInput) {
		return downloadInput.endsWith("torrent") ? DownloadableState.SPECIFIC : DownloadableState.IMPOSSIBLE;
	}

}
