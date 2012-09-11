package com.dabi.habitv.core.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

import com.dabi.habitv.framework.plugin.exception.TechnicalException;

public class ExportDAO {
	public synchronized void  addExportStep(EpisodeExportState episodeExportState) {
		EpisodeExportIndexRoot root = loadEpisodeExportIndexRoot();
		root.addEpisodeExportStates(episodeExportState);
		try {
			FileOutputStream fichier = new FileOutputStream(getExportIndexFileName(), false);
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
			oos.writeObject(root);
			oos.flush();
			oos.close();
		} catch (java.io.IOException e) {
			throw new TechnicalException(e);
		}
	}

	private EpisodeExportIndexRoot loadEpisodeExportIndexRoot() {
		final File index = new File(getExportIndexFileName());
		final EpisodeExportIndexRoot root;
		if (!index.exists()) {
			root = new EpisodeExportIndexRoot();
		} else {
			try {
				FileInputStream fichier = new FileInputStream(index);
				ObjectInputStream ois = new ObjectInputStream(fichier);
				root = (EpisodeExportIndexRoot) ois.readObject();
				return root;
			} catch (java.io.IOException | ClassNotFoundException e) {
				throw new TechnicalException(e);
			}
		}
		return root;
	}

	private String getExportIndexFileName() {
		return "export.index";
	}

	public Collection<EpisodeExportState> loadExportStep() {
		return loadEpisodeExportIndexRoot().getEpisodeExportStates();
	}

	public void init() {
		final File index = new File(getExportIndexFileName());
		index.delete();
	}
}
