package com.dabi.habitv.core.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

import com.dabi.habitv.api.plugin.exception.TechnicalException;

public class ExportDAO {

	private String appDir;

	public ExportDAO(String appDir) {
		this.appDir = appDir;
	}

	public synchronized void addExportStep(
			final EpisodeExportState episodeExportState) {
		final EpisodeExportIndexRoot root = loadEpisodeExportIndexRoot();
		if (!root.getEpisodeExportStates().contains(episodeExportState)) {
			root.addEpisodeExportStates(episodeExportState);
			saveExportIndex(root);
		}
	}

	private void saveExportIndex(final EpisodeExportIndexRoot root) {
		FileOutputStream fichier = null;
		ObjectOutputStream oos = null;
		try {
			fichier = new FileOutputStream(getExportIndexFileName(), false);
			oos = new ObjectOutputStream(fichier);
			oos.writeObject(root);
			oos.flush();
		} catch (final java.io.IOException e) {
			throw new TechnicalException(e);
		} finally {
			if (fichier != null) {
				try {
					fichier.close();
				} catch (final IOException e) {
					throw new TechnicalException(e);
				}
			}
			if (oos != null) {
				try {
					oos.close();
				} catch (final IOException e) {
					throw new TechnicalException(e);
				}
			}
		}
	}

	private EpisodeExportIndexRoot loadEpisodeExportIndexRoot() {
		final File index = new File(getExportIndexFileName());
		final EpisodeExportIndexRoot root;
		if (!index.exists()) {
			root = new EpisodeExportIndexRoot();
		} else {
			FileInputStream fichier = null;
			ObjectInputStream ois = null;
			try {
				fichier = new FileInputStream(index);
				ois = new ObjectInputStream(fichier);
				root = (EpisodeExportIndexRoot) ois.readObject();
				return root;
			} catch (java.io.IOException | ClassNotFoundException e) {
				throw new TechnicalException(e);
			} finally {
				if (ois != null) {
					try {
						ois.close();
					} catch (final IOException e) {
						throw new TechnicalException(e);
					}
				}
				if (fichier != null) {
					try {
						fichier.close();
					} catch (final IOException e) {
						throw new TechnicalException(e);
					}
				}
			}
		}
		return root;
	}

	private String getExportIndexFileName() {
		return appDir + "/export.index";
	}

	public Collection<EpisodeExportState> loadExportStep() {
		return loadEpisodeExportIndexRoot().getEpisodeExportStates();
	}

	public void init() {
		final File index = new File(getExportIndexFileName());
		if (index.exists()) {
			if (!index.delete()) {
				throw new TechnicalException("can't delete");
			}
		}
	}

	public synchronized void removeExportStep(
			final EpisodeExportState episodeExportState) {
		final EpisodeExportIndexRoot root = loadEpisodeExportIndexRoot();
		root.removeEpisodeExportStates(episodeExportState);
		saveExportIndex(root);
	}
}
