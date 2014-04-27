package com.dabi.habitv.core.plugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.dabi.habitv.framework.plugin.api.PluginBase;
import com.dabi.habitv.framework.plugin.api.PluginClassLoader;
import com.dabi.habitv.framework.plugin.api.update.UpdatablePluginInterface;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;

class PluginsLoader<P extends PluginBase> {

	private static final String CLASS_EXTENSION = ".class";

	private static final int CLASS_EXTENSION_SIZE = CLASS_EXTENSION.length();

	private final List<File> files;

	private final List<Plugin> classPluginProviders;

	private final Class<P> pluginInterface;

	private class Plugin {
		private final Class<P> classPluginProvider;
		private final ClassLoader classLoaders;

		public Plugin(final Class<P> classPluginProvider, final ClassLoader classLoaders) {
			super();
			this.classPluginProvider = classPluginProvider;
			this.classLoaders = classLoaders;
		}

		public Class<P> getClassPluginProvider() {
			return classPluginProvider;
		}

		public ClassLoader getClassLoaders() {
			return classLoaders;
		}

	}

	@SuppressWarnings("unchecked")
	PluginsLoader(final Class<P> pluginInterface, final File[] files) {
		this.files = (List<File>) (files == null ? Collections.emptyList() : Arrays.asList(files));
		this.classPluginProviders = new LinkedList<>();
		this.pluginInterface = pluginInterface;
	}

	List<P> loadAllProviderPlugins() {

		this.initializeLoader();

		final List<P> tmpPlugins = new ArrayList<>(this.classPluginProviders.size());
		for (final Plugin plugin : this.classPluginProviders) {
			try {
				final P pluginProviderInterface = plugin.getClassPluginProvider().newInstance();
				if (UpdatablePluginInterface.class.isInstance(pluginProviderInterface)) {
					((UpdatablePluginInterface) pluginProviderInterface).update();
				}
				if (PluginClassLoader.class.isInstance(pluginProviderInterface)) {
					((PluginClassLoader) pluginProviderInterface).setClassLoader(plugin.getClassLoaders());
				}
				tmpPlugins.add(pluginProviderInterface);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new TechnicalException(e);
			}
		}

		return tmpPlugins;
	}

	private void initializeLoader() {

		// Pour eviter le double chargement des plugins
		if (this.classPluginProviders.size() != 0) {
			return;
		}

		for (final File file : this.files) {
			if (!file.exists()) {
				break;
			}

			// recherche tous les plugins dans le fichier
			findAllPlugins(file);
		}

	}

	private void findAllPlugins(final File file) {
		// Pour la comparaison de chaines
		String tmp = "";
		// Pour le contenu de l'archive jar
		Enumeration<JarEntry> enumeration;
		// Pour déterminer quels sont les interfaces implémentées
		Class<P> tmpClass = null;

		final URL url = getFileUrl(file);
		// On créer un nouveau URLClassLoader pour charger le jar qui se
		// trouve ne dehors du CLASSPATH
		try (URLClassLoader loader = new URLClassLoader(new URL[] { url })) {
			// On charge le jar en mémoire
			try (final JarFile jar = new JarFile(file.getAbsolutePath());) {

				// On récupére le contenu du jar
				enumeration = jar.entries();

				while (enumeration.hasMoreElements()) {

					final JarEntry nextElement = enumeration.nextElement();
					tmp = nextElement.toString();

					// On vérifie que le fichier courant est un .class (et pas
					// un
					// fichier d'informations du jar )
					if (tmp.length() > CLASS_EXTENSION_SIZE && tmp.substring(tmp.length() - CLASS_EXTENSION_SIZE).compareTo(CLASS_EXTENSION) == 0) {
						tmpClass = getClass(tmp.substring(0, tmp.length() - CLASS_EXTENSION_SIZE).replaceAll("/", "."), loader);
						final List<Class<?>> interfaces = getInterfaces(tmpClass);
						for (final Class<?> interfaceClass : interfaces) {
							// Une classe ne doit pas appartenir à deux
							// catégories
							// de plugins différents.
							// Si tel est le cas on ne la place que dans la
							// catégorie de la première interface correct
							// trouvée
							if (interfaceClass.getName().equals(this.pluginInterface.getName())) {
								this.classPluginProviders.add(new Plugin(tmpClass, loader));
							}
						}

					}
				}
			} catch (final IOException e) {
				throw new TechnicalException(e);
			}
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
	}

	private List<Class<?>> getInterfaces(final Class<P> tmpClass) {
		final List<Class<?>> interfaces = new LinkedList<>();
		interfaces.addAll(Arrays.asList(tmpClass.getInterfaces()));
		if (tmpClass.getGenericSuperclass() != null) {
			interfaces.addAll(Arrays.asList((tmpClass.getSuperclass().getInterfaces())));
		}
		return interfaces;
	}

	private Class<P> getClass(final String tmp, final URLClassLoader loader) {
		Class<P> tmpClass;
		try {
			@SuppressWarnings("unchecked")
			final Class<P> forName = (Class<P>) Class.forName(tmp, true, loader);
			tmpClass = forName;
		} catch (final ClassNotFoundException e) {
			throw new TechnicalException(e);
		}
		return tmpClass;
	}

	private static URL getFileUrl(final File file) {
		URL url;
		try {
			url = file.toURI().toURL();
		} catch (final MalformedURLException e) {
			throw new TechnicalException(e);
		}
		return url;
	}

}
