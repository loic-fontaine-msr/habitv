package com.dabi.habitv.provider.tf1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.api.downloader.PluginDownloaderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdExecutor;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public class TF1PluginManager implements PluginProviderInterface {

	@Override
	public String getName() {
		return TF1Conf.NAME;
	}

	@Override
	public void setClassLoader(final ClassLoader classLoader) {

	}

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		return TF1Retreiver.findEpisode(category);
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		return TF1Retreiver.findCategory();
	}

	private String getDownloader(final String url) {
		String downloaderName;
		if (url.startsWith(TF1Conf.RTMPDUMP_PREFIX)) {
			downloaderName = TF1Conf.RTMDUMP;
		} else {
			downloaderName = TF1Conf.CURL;
		}
		return downloaderName;
	}

	@Override
	public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener,
			final EpisodeDTO episode) throws DownloadFailedException, NoSuchDownloaderException {
		final VideoStruct videoStruct = TF1Retreiver.findFinalUrl(episode);

		if (videoStruct.getMediaIdList().isEmpty()) {
			throw new DownloadFailedException("no link");
		}

		final String firstMediaID = videoStruct.getMediaIdList().iterator().next();

		String videoUrl;
		try {
			videoUrl = RetrieverUtils.getUrlContent(TF1Retreiver.buildUrlVideoInfo(firstMediaID, "webhd"));
		} catch (final Exception e) {
			videoUrl = RetrieverUtils.getUrlContent(TF1Retreiver.buildUrlVideoInfo(firstMediaID, "web"));
		}
		final String downloaderName = getDownloader(videoUrl);
		final PluginDownloaderInterface pluginDownloader = downloaders.getDownloader(downloaderName);

		final Map<String, String> parameters = new HashMap<>(2);
		parameters.put(FrameworkConf.PARAMETER_BIN_PATH, downloaders.getBinPath(downloaderName));
		parameters.put(FrameworkConf.CMD_PROCESSOR, downloaders.getCmdProcessor());

		if (TF1Conf.RTMDUMP.equals(downloaderName)) {
			videoUrl = videoUrl.replace(",rtmpte", "");
			videoUrl = videoUrl.substring(0, videoUrl.lastIndexOf("?"));
			parameters.put(FrameworkConf.PARAMETER_ARGS, TF1Conf.DUMP_CMD);
			pluginDownloader.download(videoUrl, downloadOuput, parameters, cmdProgressionListener);
		} else {
			if (videoStruct.getMediaIdList().size() == 1) {
				pluginDownloader.download(videoUrl, downloadOuput, parameters, cmdProgressionListener);
			} else {
				final String assemblerBinPath = downloaders.getBinPath(TF1Conf.ASSEMBLER);
				if (assemblerBinPath == null) {
					throw new TechnicalException(TF1Conf.ASSEMBLER + " downloader can't be found, add it the config.xml");
				}
				downloadFragments(videoStruct, downloadOuput, cmdProgressionListener, TF1Conf.CORRECT_VIDEO_CMD, assemblerBinPath);
			}
		}
	}

	private int handleProgression(final int nbMax, final int indice, final int old) {
		final float f = (float) indice / (float) nbMax;
		return Math.min((int) (f * 100), 100);
	}

	// TODO mutualiser avec Pluzz
	private void downloadFragments(final VideoStruct videoStruct, final String downloadOutput, final CmdProgressionListener progressionListener,
			final String assemblerCmd, final String assembler) throws DownloadFailedException {
		FileOutputStream fOutputStream = null;
		int i = 0;
		int old = -1;
		final StringBuilder tsList = new StringBuilder("");
		final List<String> tsFilesList = new LinkedList<>();
		for (final String mediaId : videoStruct.getMediaIdList()) {
			final String tmpVideoFile = downloadOutput + "-" + i + ".frg";
			try {
				fOutputStream = new FileOutputStream(tmpVideoFile);

				final byte[] buffer = new byte[1024]; // Adjust if you want
				int bytesRead;
				final InputStream input = RetrieverUtils.getInputStreamFromUrl(RetrieverUtils.getUrlContent(TF1Retreiver.buildUrlVideoInfo(mediaId, "web")));
				while ((bytesRead = input.read(buffer)) != -1) {
					fOutputStream.write(buffer, 0, bytesRead);
				}//TODO dl avec curl et gérer la progression

				// fOutputStream.write(RetrieverUtils.getUrlContentBytes(RetrieverUtils.getUrlContent(TF1Retreiver.buildUrlVideoInfo(mediaId,
				// "web"))));
				// Affichage de la progression
				final int newP = handleProgression(videoStruct.getMediaIdList().size(), i, old);
				if (newP != old) {
					progressionListener.listen(String.valueOf(newP));
					old = newP;
					// LOGGER.debug("Avancement : " + newP + " %");
				}
				i++;
			} catch (final IOException e) {
				throw new TechnicalException(e);
			} finally {
				if (fOutputStream != null) {
					try {
						fOutputStream.flush();
						fOutputStream.close();
					} catch (final IOException e) {
						// LOGGER.error("", e);
					}
				}
			}
			// to TS
			final String tmpVideoFileTs = tmpVideoFile + ".ts";
			(new File(tmpVideoFileTs)).delete();
			try {
				new CmdExecutor(null, String.format("%s -i %s -c copy -y -bsf:v h264_mp4toannexb -f mpegts %s", assembler, tmpVideoFile, tmpVideoFileTs), 2000,
						null).execute();
			} catch (final ExecutorFailedException e) {
				throw new TechnicalException(e);
			}
			(new File(tmpVideoFile)).delete();
			tsList.append(tmpVideoFileTs + "|");
			tsFilesList.add(tmpVideoFileTs);
		}

		// corriger fichier video ffmpeg -isync -i test.avi -c copy test2.avi
		// ffmpeg -isync -i "concat:file-01.mpeg.ts|file-02.mpeg.ts" -f mpeg
		try {
			// fmpeg veut l'extension .avi
			final String downloadOuputAvi = downloadOutput + ".avi";
			new CmdExecutor(null, String.format("%s -isync -i \"concat:%s\" -c copy %s", assembler, tsList.toString(), downloadOuputAvi), 2000, null).execute();
			for (final String tsFile : tsFilesList) {
				(new File(tsFile)).delete();
			}
			(new File(downloadOuputAvi)).renameTo(new File(downloadOutput));
		} catch (final ExecutorFailedException e) {
			throw new TechnicalException(e);
		}
	}

}
