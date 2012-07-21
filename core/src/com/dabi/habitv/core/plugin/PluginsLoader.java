package com.dabi.habitv.core.plugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.dabi.habitv.framework.plugin.api.PluginBase;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;

public class PluginsLoader<P extends PluginBase> {

	private final List<File> files;

	private final List<Plugin> classPluginProviders;

	private final Class<P> pluginInterface;

	private class Plugin {
		public Plugin(final Class<P> classPluginProvider, final ClassLoader classLoaders) {
			super();
			this.classPluginProvider = classPluginProvider;
			this.classLoaders = classLoaders;
		}

		final Class<P> classPluginProvider;
		final ClassLoader classLoaders;
	}

	public PluginsLoader(final Class<P> pluginInterface, final File[] files) {
		if (files == null || files.length == 0) {
			throw new IllegalArgumentException("files lists can't be empty");
		}
		this.files = Arrays.asList(files);
		this.classPluginProviders = new LinkedList<>();
		this.pluginInterface = pluginInterface;
	}

	public List<P> loadAllProviderPlugins() {

		this.initializeLoader();

		final List<P> tmpPlugins = new ArrayList<>(this.classPluginProviders.size());
		for (Plugin plugin : this.classPluginProviders) {
			try {
				final P pluginProviderInterface = plugin.classPluginProvider.newInstance();
				pluginProviderInterface.setClassLoader(plugin.classLoaders);
				tmpPlugins.add(pluginProviderInterface);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new TechnicalException(e);
			}
		}

		return tmpPlugins;
	}

	private void initializeLoader() {
		// On vérifie que la liste des plugins à charger à été initialisé
		if (this.files == null || this.files.size() == 0) {
			throw new TechnicalException("No files");
		}

		// Pour eviter le double chargement des plugins
		if (this.classPluginProviders.size() != 0) {
			return;
		}

		for (File file : this.files) {
			if (!file.exists()) {
				break;
			}

			// recherche tous les plugins dans le fichier
			findAllPlugins(file);
		}

	}

	private void findAllPlugins(final File file) {

		// Pour charger le .jar en memoire
		URLClassLoader loader;
		// Pour la comparaison de chaines
		String tmp = "";
		// Pour le contenu de l'archive jar
		Enumeration<JarEntry> enumeration;
		// Pour déterminer quels sont les interfaces implémentées
		Class<P> tmpClass = null;

		final URL url = getFileUrl(file);
		// On créer un nouveau URLClassLoader pour charger le jar qui se
		// trouve ne dehors du CLASSPATH
		loader = new URLClassLoader(new URL[] { url });

		// On charge le jar en mémoire
		final JarFile jar = loadJar(file);

		// On récupére le contenu du jar
		enumeration = jar.entries();

		while (enumeration.hasMoreElements()) {

			tmp = enumeration.nextElement().toString();

			// On vérifie que le fichier courant est un .class (et pas un
			// fichier d'informations du jar )
			if (tmp.length() > 6 && tmp.substring(tmp.length() - 6).compareTo(".class") == 0) {
				tmpClass = getClass(tmp.substring(0, tmp.length() - 6).replaceAll("/", "."), loader);
				for (int i = 0; i < tmpClass.getInterfaces().length; i++) {

					// Une classe ne doit pas appartenir à deux catégories
					// de plugins différents.
					// Si tel est le cas on ne la place que dans la
					// catégorie de la première interface correct
					// trouvée
					if (tmpClass.getInterfaces()[i].getName().equals(this.pluginInterface.getName())) {
						this.classPluginProviders.add(new Plugin(tmpClass, loader));
					}
				}

			}
		}
	}

	private Class<P> getClass(final String tmp, final URLClassLoader loader) {
		Class<P> tmpClass;
		try {
			@SuppressWarnings("unchecked")
			final Class<P> forName = (Class<P>) Class.forName(tmp, true, loader);
			tmpClass = forName;
		} catch (ClassNotFoundException e) {
			throw new TechnicalException(e);
		}
		return tmpClass;
	}

	private static JarFile loadJar(final File file) {
		JarFile jar;
		try {
			jar = new JarFile(file.getAbsolutePath());
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
		return jar;
	}

	private static URL getFileUrl(final File file) {
		URL url;
		try {
			url = file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new TechnicalException(e);
		}
		return url;
	}

}
