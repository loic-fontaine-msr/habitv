package com.dabi.habitv.core.dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.core.config.HabitTvConf;
import com.dabi.habitv.framework.FrameworkConf;

public class DlErrorDAO {

	private static final Logger LOG = Logger.getLogger(DlErrorDAO.class);

	public DlErrorDAO() {
		super();
	}

	private String getErrorFile() {
		return FrameworkConf.ERROR_FILE;
	}

	public Set<String> findDownloadedErrorFiles() {

		BufferedReader lecteurAvecBuffer = null;
		String ligne;

		final Set<String> fileList = new HashSet<>();
		try {

			lecteurAvecBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(getErrorFile()), HabitTvConf.ENCODING));
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

	public synchronized void addDownloadErrorFiles(final String... files) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getErrorFile(), true), HabitTvConf.ENCODING));
			for (final String file : files) {
				writer.println(file);
			}
			writer.close();
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
	}
}
