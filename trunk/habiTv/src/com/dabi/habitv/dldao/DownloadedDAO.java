package com.dabi.habitv.dldao;

import java.io.BufferedReader;
import java.io.File;
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

import com.dabi.habitv.config.HabitTvConf;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.utils.FileUtils;

public final class DownloadedDAO {

	private static final Logger LOG = Logger.getLogger(DownloadedDAO.class);

	private final String indexDir;

	private final String tvShow;

	private final String channelName;

	private final boolean indexExist;

	public DownloadedDAO(final String workingDir, final String channelName, final String tvShow) {
		super();
		this.indexDir = workingDir + "/index";
		this.tvShow = tvShow;
		this.channelName = channelName;
		File indexDirectory = new File(indexDir);
		if (indexDirectory.exists()) {
			this.indexExist = new File(getFileIndex()).exists();
		} else {
			this.indexExist = false;
			indexDirectory.mkdir();
		}
	}

	private String getFileIndex() {
		return (indexDir + "/" + FileUtils.sanitizeFilename(channelName + "_" + tvShow + ".index"));
	}

	public Set<String> findDownloadedFiles() {

		BufferedReader lecteurAvecBuffer = null;
		String ligne;

		Set<String> fileList = null;
		try {
			fileList = new HashSet<>();
			lecteurAvecBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(getFileIndex()), HabitTvConf.ENCODING));
			while ((ligne = lecteurAvecBuffer.readLine()) != null) {
				fileList.add(ligne);
			}
		} catch (FileNotFoundException exc) {
			// will return null
			LOG.debug("", exc);
		} catch (IOException e) {
			throw new TechnicalException(e);
		} finally {
			if (lecteurAvecBuffer != null) {
				try {
					lecteurAvecBuffer.close();
				} catch (IOException e) {
					throw new TechnicalException(e);
				}
			}
		}
		return fileList;
	}

	public void addDownloadedFiles(final String... files) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getFileIndex(), true), "UTF-8"));
			for (String file : files) {
				writer.println(file);
			}
			writer.close();
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
	}

	public boolean isIndexCreated() {
		return indexExist;
	}
}
