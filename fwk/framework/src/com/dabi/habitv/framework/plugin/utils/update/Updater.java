package com.dabi.habitv.framework.plugin.utils.update;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.update.FindArtifactUtils.ArtifactVersion;

public abstract class Updater {

	private static final Logger LOG = Logger.getLogger(Updater.class);

	private final String currentDir;

	private final String groupId;

	private final String coreVersion;

	private final boolean autoriseSnapshot;

	public Updater(final String currentDir, final String groupId,
			final String coreVersion, final boolean autoriseSnapshot) {
		this.currentDir = currentDir;
		this.groupId = groupId;
		this.coreVersion = coreVersion;
		this.autoriseSnapshot = autoriseSnapshot;
	}

	public void update(final String folderToUpdate,
			final String... filesToUpdate) {

		final String folderPath = currentDir + "/" + folderToUpdate;
		final File currentFolder = new File(folderPath);
		if (!currentFolder.exists()) {
			currentFolder.mkdir();
		} else {
			if (deleteFiles()) {
				final List<String> filesToUpdateList = Arrays
						.asList(filesToUpdate);
				for (final File folderFile : currentFolder.listFiles()) {
					if (!filesToUpdateList.contains(folderFile.getName()
							.replace("." + getLocalExtension(), ""))) {
						folderFile.delete();
					}
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

	protected abstract boolean deleteFiles();

	private void updateFile(final String folderToUpdate,
			final String folderPath, final File currentFolder,
			final String fileToUpdate) {
		onChecking(fileToUpdate);
		final ArtifactVersion artifactNewVersion = FindArtifactUtils
				.findLastVersionUrl(groupId, fileToUpdate, coreVersion,
						autoriseSnapshot, getServerExtension());
		if (artifactNewVersion == null) {
			LOG.info("Nothing found for " + fileToUpdate);
			return;
		}
		final File currentFile = new File(folderPath + "/" + fileToUpdate + "."
				+ getLocalExtension());
		if (currentFile.exists()) {
			final String currentVersion = getCurrentVersion(currentFile);//
			if (currentVersion == null
					|| currentVersion.contains("-SNAPSHOT")
					|| currentVersion
							.compareTo(artifactNewVersion.getVersion()) < 0) {
				updateFile(folderToUpdate, artifactNewVersion, currentFile);
			}
		} else {
			updateFile(folderToUpdate, artifactNewVersion, currentFile);
		}
	}

	protected abstract void onChecking(String fileToUpdate);

	protected abstract String getCurrentVersion(File currentFile);

	protected abstract String getLocalExtension();

	protected abstract String getServerExtension();

	private void updateFile(String folderToUpdate,
			final ArtifactVersion artifactNewVersion, final File current) {
		if (performUpdate(current, artifactNewVersion)) {
			onUpdate(current, artifactNewVersion);
			File newVersion;
			try {
				newVersion = new File(downloadFile(artifactNewVersion.getUrl(),
						current.getPath() + ".tmp"));
			} catch (final IOException e) {
				onUpdateError(current, artifactNewVersion);
				throw new TechnicalException(e);
			}
			updateFile(folderToUpdate, current, newVersion);

			onUpdateDone(current, artifactNewVersion);
		}
	}

	protected abstract void onUpdateError(File current,
			ArtifactVersion artifactNewVersion);

	protected abstract void onUpdateDone(File current,
			ArtifactVersion artifactNewVersion);

	protected abstract void onUpdate(File current,
			ArtifactVersion artifactNewVersion);

	protected abstract boolean performUpdate(File current,
			ArtifactVersion artifactNewVersion);

	protected void updateFile(final String folderToUpdate, final File current,
			final File newVersion) {
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
