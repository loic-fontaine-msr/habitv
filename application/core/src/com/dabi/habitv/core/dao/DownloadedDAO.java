package com.dabi.habitv.core.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.core.config.HabitTvConf;
import com.dabi.habitv.utils.FileUtils;

public class DownloadedDAO {

	private static final Logger LOG = Logger.getLogger(DownloadedDAO.class);

	private final String indexDir;

	private boolean indexExist;

	private CategoryDTO category;

	public DownloadedDAO(final CategoryDTO category, final String indexDir) {
		super();
		this.indexDir = indexDir;
		this.category = category;
		final File indexDirectory = new File(indexDir);
		if (indexDirectory.exists()) {
			indexExist = new File(getFileIndex()).exists();
		} else {
			indexExist = false;
			if (!indexDirectory.mkdir()) {
				throw new TechnicalException("Folder can't be created"
						+ indexDirectory.getAbsolutePath());
			}
		}
	}

	public String getFileIndex() {
		return getFileIndex(indexDir, category);
	}

	public static String getFileIndex(String indexDir, CategoryDTO category) {
		return (indexDir + "/" + FileUtils.sanitizeFilename(category
				.getPlugin() + "_" + category.getName() + ".index"));
	}

	public Set<String> findDownloadedFiles() {

		BufferedReader lecteurAvecBuffer = null;
		String ligne;

		final Set<String> fileList = new LinkedHashSet<>();
		try {

			lecteurAvecBuffer = new BufferedReader(new InputStreamReader(
					new FileInputStream(getFileIndex()), HabitTvConf.ENCODING));
			while ((ligne = lecteurAvecBuffer.readLine()) != null) {
				fileList.add(ligne);
			}
		} catch (final FileNotFoundException exc) {
			// will return null
			LOG.debug("", exc);
		} catch (final IOException e) {
			throw new TechnicalException(e);
		} finally {
			if (lecteurAvecBuffer != null) {
				try {
					lecteurAvecBuffer.close();
				} catch (final IOException e) {
					throw new TechnicalException(e);
				}
			}
		}
		return fileList;
	}

	public synchronized void addDownloadedFiles(final EpisodeDTO... episodes) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(getFileIndex(), true),
					HabitTvConf.ENCODING));
			for (final EpisodeDTO episode : episodes) {
				writer.println(episode.getName());
			}
			writer.close();
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
	}

	public boolean isIndexCreated() {
		return indexExist;
	}

	void initIndex() {
		final String fileIndex = getFileIndex();
		(new File(fileIndex)).delete();
		LOG.info("réinitialisation de l'index " + fileIndex);
		indexExist = false;
	}
}
