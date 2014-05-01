package com.dabi.habitv.framework.plugin.api;

import java.io.InputStream;
import java.net.Proxy;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dabi.habitv.api.plugin.api.PluginWithProxyInterface;
import com.dabi.habitv.api.plugin.dto.ProxyDTO;
import com.dabi.habitv.api.plugin.dto.ProxyDTO.ProtocolEnum;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

/**
 * Generics basics for provider
 */
public abstract class BasePluginWithProxy implements PluginWithProxyInterface {

	private final Logger logguer = Logger.getLogger(this.getClass().getName());

	private Map<ProtocolEnum, ProxyDTO> protocol2proxy;

	@Override
	public final void setProxies(final Map<ProtocolEnum, ProxyDTO> protocol2proxy) {
		this.protocol2proxy = protocol2proxy;
	}

	protected Map<ProtocolEnum, ProxyDTO> getProtocol2proxy() {
		return protocol2proxy;
	}

	protected Proxy getHttpProxy() {
		if (protocol2proxy != null) {
			final ProxyDTO proxyDTO = protocol2proxy.get(ProtocolEnum.HTTP);
			return (proxyDTO == null) ? null : proxyDTO.getProxy();
		} else {
			return null;
		}
	}

	protected InputStream getInputStreamFromUrl(final String url) {
		return RetrieverUtils.getInputStreamFromUrl(url, getHttpProxy());
	}

	protected String getUrlContent(final String url) {
		return RetrieverUtils.getUrlContent(url, getHttpProxy());
	}

	protected String getUrlContent(final String url, final String encoding) {
		return RetrieverUtils.getUrlContent(url, encoding, getHttpProxy());
	}

	public Logger getLog() {
		return logguer;
	}

}
