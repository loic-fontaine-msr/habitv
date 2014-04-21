package com.dabi.habitv.framework.plugin.api.provider;

import java.io.InputStream;
import java.net.Proxy;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dabi.habitv.framework.plugin.api.dto.ProxyDTO;
import com.dabi.habitv.framework.plugin.api.dto.ProxyDTO.ProtocolEnum;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

/**
 * Generics basics for provider
 */
public abstract class BasePluginProvider implements PluginProviderInterface {

	private final Logger logguer = Logger.getLogger(this.getClass().getName());

	private ClassLoader classLoader;

	private Map<ProtocolEnum, ProxyDTO> protocol2proxy;

	@Override
	public final void setClassLoader(final ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public final void setProxy(final Map<ProtocolEnum, ProxyDTO> protocol2proxy) {
		this.protocol2proxy = protocol2proxy;
	}

	protected ClassLoader getClassLoader() {
		return classLoader;
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

	protected Object unmarshalInputStream(final InputStream input, final String unmarshallerPackage) {
		return RetrieverUtils.unmarshalInputStream(input, unmarshallerPackage, getClassLoader());
	}

	public Logger getLog() {
		return logguer;
	}

}
