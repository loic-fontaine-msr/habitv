package com.dabi.habitv.core.updater;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

import com.dabi.habitv.core.event.UpdatePluginEvent;
import com.dabi.habitv.core.event.UpdatePluginStateEnum;
import com.dabi.habitv.core.publisher.Publisher;
import com.dabi.habitv.core.updater.FindArtifactUtils.ArtifactVersion;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;

public class Updater {

	private static final Logger LOG = Logger.getLogger(Updater.class);

	private final String currentDir;

	private final String groupId;

	private final String coreVersion;

	private final boolean autoriseSnapshot;

	private final Publisher<UpdatePluginEvent> updatePublisher;

	public Updater(final String currentDir, final String groupId,
			final String coreVersion, final boolean autoriseSnapshot,
			final Publisher<UpdatePluginEvent> updatePublisher) {
		this.currentDir = currentDir;
		this.groupId = groupId;
		this.coreVersion = coreVersion;
		this.autoriseSnapshot = autoriseSnapshot;
		this.updatePublisher = updatePublisher;
	}

	public void update(final String folderToUpdate,
			final String... filesToUpdate) {

		final String folderPath = currentDir + "/" + folderToUpdate;
		final File currentFolder = new File(folderPath);
		if (!currentFolder.exists()) {
			currentFolder.mkdir();
		} else {
			final List<String> filesToUpdateList = Arrays.asList(filesToUpdate);
			for (final File folderFile : currentFolder.listFiles()) {
				if (!filesToUpdateList.contains(folderFile.getName().replace(
						".jar", ""))) {
					folderFile.delete();
				}
			}
		}

		for (final String fileToUpdate : filesToUpdate) {
			try {
				updateFile(folderToUpdate, folderPath, currentFolder,
						fileToUpdate);
			} catch (final Exception e) {
				LOG.error("", e);
			}
		}

	}

	private void updateFile(final String folderToUpdate,
			final String folderPath, final File currentFolder,
			final String fileToUpdate) {
		updatePublisher.addNews(new UpdatePluginEvent(fileToUpdate, null,
				UpdatePluginStateEnum.CHECKING));
		final ArtifactVersion artifactVersion = FindArtifactUtils
				.findLastVersionUrl(groupId, fileToUpdate, coreVersion,
						autoriseSnapshot);
		if (artifactVersion == null) {
			LOG.info("Nothing found for " + fileToUpdate);
			return;
		}
		final File currentFile = new File(folderPath + "/" + fileToUpdate
				+ ".jar");
		if (currentFile.exists()) {
			final String currentVersion = getCurrentVersion(currentFile);//
			if (currentVersion == null || currentVersion.contains("-SNAPSHOT")
					|| !currentVersion.equals(artifactVersion.getVersion())) {
				updateFile(artifactVersion, currentFile);
			}
		} else {
			updateFile(artifactVersion, currentFile);
		}
	}

	private String getCurrentVersion(final File currentFile) {
		try (JarInputStream jarStream = new JarInputStream(new FileInputStream(
				currentFile));) {
			final Manifest mf = jarStream.getManifest();
			return (String) mf.getMainAttributes().get(
					Attributes.Name.IMPLEMENTATION_VERSION);
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
	}

	private void updateFile(final ArtifactVersion artifactVersion,
			final File current) {
		LOG.info("Update of plugin " + artifactVersion.getArtifactId()
				+ " version " + artifactVersion.getVersion());
		updatePublisher.addNews(new UpdatePluginEvent(artifactVersion
				.getArtifactId(), artifactVersion.getVersion(),
				UpdatePluginStateEnum.DOWNLOADING));
		File newVersion;
		try {
			newVersion = new File(downloadFile(artifactVersion.getUrl(),
					current.getPath() + ".tmp"));
		} catch (final IOException e) {
			LOG.error("Error while updating plugin "
					+ artifactVersion.getArtifactId() + " version "
					+ artifactVersion.getVersion());
			updatePublisher.addNews(new UpdatePluginEvent(artifactVersion
					.getArtifactId(), artifactVersion.getVersion(),
					UpdatePluginStateEnum.ERROR));
			throw new TechnicalException(e);
		}
		updateFile(current, newVersion);

		LOG.info("Update of plugin " + artifactVersion.getArtifactId()
				+ " version " + artifactVersion.getVersion() + " done");
		updatePublisher.addNews(new UpdatePluginEvent(artifactVersion
				.getArtifactId(), artifactVersion.getVersion(),
				UpdatePluginStateEnum.DONE));
	}

	private void updateFile(final File current, final File newVersion) {
		if (newVersion.exists()) {
			if (current.exists()) {
				try {
					Files.delete(current.toPath());
				} catch (final IOException e) {
					throw new TechnicalException(e);
				}
			}
			newVersion.renameTo(current);
		}
	}

	/**
	 * Cette méthode télécharge un fichier sur internet et le stocke en local
	 * 
	 * @param filePath
	 *            , chemin du fichier à télécharger
	 * @param destination
	 *            , chemin du fichier en local
	 * @return
	 * @throws IOException
	 */
	private String downloadFile(final String filePath, final String destination)
			throws IOException {
		URLConnection connection = null;
		InputStream is = null;
		FileOutputStream destinationFile = null;
		try {
			// On crée l'URL
			final URL url = new URL(filePath);

			// On crée une connection vers cet URL
			connection = url.openConnection();

			// On récupère la taille du fichier
			final int length = connection.getContentLength();

			// Si le fichier est inexistant, on lance une exception
			if (length == -1) {
				throw new IOException("Fichier vide");
			}

			// On récupère le stream du fichier
			is = new BufferedInputStream(connection.getInputStream());

			// On prépare le tableau de bits pour les données du fichier
			final byte[] data = new byte[length];

			// On déclare les variables pour se retrouver dans la lecture du
			// fichier
			int currentBit = 0;
			int deplacement = 0;

			// Tant que l'on n'est pas à la fin du fichier, on récupère des
			// données
			while (deplacement < length) {
				currentBit = is.read(data, deplacement, data.length
						- deplacement);
				if (currentBit == -1) {
					break;
				}
				deplacement += currentBit;
			}

			// Si on est pas arrivé à la fin du fichier, on lance une exception
			if (deplacement != length) {
				throw new IOException(
						"Le fichier n'a pas été lu en entier (seulement "
								+ deplacement + " sur " + length + ")");
			}

			// On crée un stream sortant vers la destination
			destinationFile = new FileOutputStream(destination);

			// On écrit les données du fichier dans ce stream
			destinationFile.write(data);

			// On vide le tampon et on ferme le stream
			destinationFile.flush();

		} finally {
			if (is != null) {
				is.close();
			}
			if (destinationFile != null) {
				destinationFile.close();
			}
		}
		return destination;
	}

}
