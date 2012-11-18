package com.dabi.habitv.provider.pluzz.jpluzz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdExecutor;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.utils.FileUtils;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.provider.pluzz.PluzzConf;

public class PluzzDLM3U8 {

	private static final Logger LOGGER = Logger.getLogger(PluzzDLM3U8.class);

	private final CmdProgressionListener progressionListener;

	private final String downloadOuput;

	private final String assemblerCmd;

	private final String assembler;

	public PluzzDLM3U8(final CmdProgressionListener progressionListener, final String downloadOuput, final String assemblerCmd, final String assembler) {
		this.progressionListener = progressionListener;
		this.downloadOuput = downloadOuput;
		this.assemblerCmd = assemblerCmd;
		this.assembler = assembler;
	}

	public void dl(final String manifestURLRelative) throws DownloadFailedException {
		String manifestUrl = PluzzConf.BASE_URL + manifestURLRelative;
		final String fragmentBaseUrl = buildFragmentBaseUrl(manifestUrl);
		String manifest = RetrieverUtils.getUrlContent(manifestUrl);
		if (!manifest.contains("EXT-X-TARGETDURATION")) {
			manifestUrl = fragmentBaseUrl + findBestQuality(manifest);
			manifest = RetrieverUtils.getUrlContent(manifestUrl);
		}
		final List<String> fragments = parseManifest(manifest);

		final int nbFragMax = fragments.size();
		LOGGER.debug("Estimation du nombre de fragments : " + nbFragMax);

		// Ajout des fragments
		int i = 1;
		int old = -1;
		final StringBuilder finalFragList = new StringBuilder();
		final List<File> fragmentFile = new ArrayList<>(nbFragMax);
		for (final String fragment : fragments) {
			final String fragmentFileName = FileUtils.sanitizeFilename(fragment);
			try {
				downloadFragment(fragmentBaseUrl + fragment, fragmentFileName);
				fragmentFile.add(new File(fragmentFileName));
				// Affichage de la progression
				final int newP = handleProgression(nbFragMax, i, old);
				if (newP != old) {
					progressionListener.listen(String.valueOf(newP));
					old = newP;
					LOGGER.debug("Avancement : " + newP + " %");
				}
				i++;
				finalFragList.append(fragmentFileName + "|");
			} catch (final IOException e) {
				throw new TechnicalException(e);
			}
		}

		// ffmpeg -isync -i "concat:file-01.mpeg.ts|file-02.mpeg.ts" -f mpeg
		try {
			new File(downloadOuput).delete();
			new CmdExecutor(null, String.format(assemblerCmd, assembler, finalFragList.toString(), downloadOuput), 2000, null).execute();
		} catch (final ExecutorFailedException e) {
			throw new TechnicalException(e);
		}
		for (final File fragment : fragmentFile) {
			fragment.delete();
		}
		LOGGER.debug("done");
	}

	private String findBestQuality(final String manifest) {
		final String[] lines = manifest.split("\r");
		String manifestUrl = null;
		for (String line : lines) {
			line = line.trim();
			if (!line.contains("#") && !line.isEmpty() && line.contains(".m3u8")) {
				manifestUrl = line;
			}
		}
		return manifestUrl;
	}

	private String buildFragmentBaseUrl(final String manifestUrl) {
		return manifestUrl.substring(0, manifestUrl.lastIndexOf("/")) + "/";
	}

	private void downloadFragment(final String fragmentUrl, final String fragmentFileName) throws FileNotFoundException, IOException {
		final FileOutputStream out = new FileOutputStream(fragmentFileName);
		final byte[] frag = RetrieverUtils.getUrlContentBytes(fragmentUrl);
		try {
			out.write(frag);
			out.flush();
		} finally {
			out.close();
		}
	}

	private int handleProgression(final int nbMax, final int indice, final int old) {
		final float f = (float) indice / (float) nbMax;
		return Math.min((int) (f * 100), 100);
	}

	private List<String> parseManifest(final String manifest) {
		final String[] lines = manifest.split("\r");
		final List<String> fragments = new ArrayList<>(lines.length);
		for (String line : lines) {
			line = line.trim();
			if (!line.startsWith("#") && !line.isEmpty()) {
				fragments.add(line);
			}
		}
		return fragments;
	}
}
