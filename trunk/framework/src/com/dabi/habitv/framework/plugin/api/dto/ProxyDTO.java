package com.dabi.habitv.framework.plugin.api.dto;

public class ProxyDTO {

	public enum ProtocolEnum {
		HTTP, SOCKS;
	}

	private final String host;
	private final int port;

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

}
