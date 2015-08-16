package com.dabi.habitv.api.plugin.dto;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

public class ProxyDTO {

	public enum ProtocolEnum {
		HTTP, SOCKS;
	}

	private final String host;
	private final int port;
	private Proxy proxy;

	public ProxyDTO(final String host, final int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public Proxy getProxy() {
		if (proxy == null) {
			proxy = initProxy();
		}
		return proxy;
	}

	private Proxy initProxy() {
		final SocketAddress addr = new InetSocketAddress(getHost(), getPort());
		final Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
		return proxy;
	}

}
