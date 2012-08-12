package com.dabi.habitv.framework.updater;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Updater {

	private final String updateSite;

	private final String currentDir;

	public Updater(final String updateSite, final String currentDir) {
		this.updateSite = updateSite;
		this.currentDir = currentDir;
	}

	public void update(final String fileToUpdate) throws IOException {

		final File current = new File(currentDir + "/" + fileToUpdate);
		final File tempDir = new File(currentDir + "/tmp");
		if (!tempDir.exists()) {
			tempDir.mkdir();
		}
		updateFileOrFolder(fileToUpdate, current, tempDir);

	}

	private void updateFileOrFolder(final String fileToUpdate, final File current, final File tempDir) throws IOException {
		if (current.isFile()) {
			final File newVersion = new File(downloadFile(updateSite + "/" + fileToUpdate, tempDir.getAbsolutePath() + "/" + current.getName()));
			updateFile(current, newVersion);
		} else {
			final File[] listFiles = current.listFiles();
			if (listFiles != null) {
				for (final File currentFile : listFiles) {
					updateFileOrFolder(fileToUpdate + "/" + currentFile.getName(), currentFile, tempDir);
				}
			}
		}
	}

	private void updateFile(final File current, final File newVersion) {
		if (newVersion.exists()) {
			current.delete();
			newVersion.renameTo(current);
		}
	}

	/**
	 * Cette méthode télécharge une fichier sur internet et le stocke en local
	 * 
	 * @param filePath
	 *            , chemin du fichier à télécharger
	 * @param destination
	 *            , chemin du fichier en local
	 * @return
	 * @throws IOException
	 */
	private String downloadFile(final String filePath, final String destination) throws IOException {
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
				currentBit = is.read(data, deplacement, data.length - deplacement);
				if (currentBit == -1) {
					break;
				}
				deplacement += currentBit;
			}

			// Si on est pas arrivé à la fin du fichier, on lance une exception
			if (deplacement != length) {
				throw new IOException("Le fichier n'a pas été lu en entier (seulement " + deplacement + " sur " + length + ")");
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
