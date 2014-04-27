package com.dabi.habitv.framework.plugin.api.provider;

import java.io.InputStream;

import com.dabi.habitv.framework.plugin.api.PluginClassLoader;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public abstract class BasePluginProviderClassloader extends BasePluginProvider implements PluginClassLoader {

	private ClassLoader classLoader;

	@Override
	public final void setClassLoader(final ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	protected ClassLoader getClassLoader() {
		return classLoader;
	}

	protected Object unmarshalInputStream(final InputStream input, final String unmarshallerPackage) {
		return RetrieverUtils.unmarshalInputStream(input, unmarshallerPackage, getClassLoader());
	}

}
