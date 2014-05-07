package com.dabi.habitv.core.task;

import java.io.File;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.api.PluginDownloaderInterface;
import com.dabi.habitv.api.plugin.api.PluginProviderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.pub.Publisher;
import com.dabi.habitv.core.dao.DownloadedDAO;
import com.dabi.habitv.core.event.EpisodeStateEnum;
import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.token.TokenReplacer;
import com.dabi.habitv.framework.plugin.utils.DownloadUtils;

public class DownloadTask extends AbstractEpisodeTask {

	private final PluginProviderInterface provider;

	private final DownloaderPluginHolder downloaders;

	private final Publisher<RetreiveEvent> publisher;

	private final DownloadedDAO downloadedDAO;

	public DownloadTask(final EpisodeDTO episode,
			final PluginProviderInterface provider,
			final DownloaderPluginHolder downloaders,
			final Publisher<RetreiveEvent> publisher,
			final DownloadedDAO downloadedDAO) {
		super(episode);
		this.provider = provider;
		this.downloaders = downloaders;
		this.publisher = publisher;
		this.downloadedDAO = downloadedDAO;
	}

	@Override
	protected void added() {
		LOG.info("Waiting for download of " + getEpisode());
	}

	@Override
	protected void failed(final Throwable e) {
		LOG.error("Download failed for " + getEpisode(), e);
		publisher.addNews(new RetreiveEvent(getEpisode(),
				EpisodeStateEnum.DOWNLOAD_FAILED, e, "download"));
	}

	@Override
	protected void ended() {
		LOG.info("Download of " + getEpisode() + " done");
		downloadedDAO.addDownloadedFiles(getEpisode().getName());
		publisher.addNews(new RetreiveEvent(getEpisode(),
				EpisodeStateEnum.DOWNLOADED));
	}

	@Override
	protected void started() {
		LOG.info("Download of " + getEpisode() + " is starting");
		publisher.addNews(new RetreiveEvent(getEpisode(),
				EpisodeStateEnum.DOWNLOADING));
	}

	@Override
	protected Object doCall() throws DownloadFailedException {
		final String outputFilename = TokenReplacer.replaceAll(
				downloaders.getDownloadOutput(), getEpisode());
		final String outputTmpFileName = outputFilename + ".tmp";
		// delete to prevent resuming since most of the download can't resume
		final File outputFile = new File(outputFilename);
		// create download dir if doesn't exist
		if (outputFile.getParentFile() != null
				&& !outputFile.getParentFile().exists()) {
			outputFile.getParentFile().mkdir();
		}
		if (outputFile.exists()) {
			if (!outputFile.delete()) {
				throw new TechnicalException("can't delete file "
						+ outputFile.getAbsolutePath());
			}
		}
		download(outputTmpFileName);
		final File file = new File(outputTmpFileName);
		if (file.exists() && !file.renameTo(new File(outputFilename))) {
			throw new TechnicalException("can't rename");
		}
		return null;
	}

	private void download(final String outputTmpFileName)
			throws DownloadFailedException {
		final DownloadParamDTO downloadParam = buildDownloadParam(outputTmpFileName);
		final CmdProgressionListener listener = buildProgressionListener();

		final PluginDownloaderInterface downloader;
		if (PluginDownloaderInterface.class.isInstance(provider)) {
			downloader = (PluginDownloaderInterface) provider;
		} else {
			downloader = DownloadUtils
					.getDownloader(downloadParam, downloaders);
		}
		downloader.download(downloadParam, downloaders, listener);
	}

	private CmdProgressionListener buildProgressionListener() {
		final CmdProgressionListener cmdProgressionListener = new CmdProgressionListener() {
			@Override
			public void listen(final String progression) {
				publisher.addNews(new RetreiveEvent(getEpisode(),
						EpisodeStateEnum.DOWNLOADING, progression));
			}
		};
		return cmdProgressionListener;
	}

	private DownloadParamDTO buildDownloadParam(final String outputTmpFileName) {
		final CategoryDTO category = getEpisode().getCategory();
		final DownloadParamDTO downloadParam = new DownloadParamDTO(
				getEpisode().getId(), outputTmpFileName,
				category.getExtension());
		downloadParam.getParams().putAll(category.getParameters());
		return downloadParam;
	}

	@Override
	public int hashCode() {
		return getEpisode().hashCode() + downloaders.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		boolean ret;
		if (obj instanceof DownloadTask) {
			final DownloadTask downloadTask = (DownloadTask) obj;
			ret = getEpisode().equals(downloadTask.getEpisode());
			if (getCategory() != null) {
				ret = ret && getCategory().equals(downloadTask.getCategory());
			}
		} else {
			ret = false;
		}
		return ret;
	}

	@Override
	public String toString() {
		return "DL" + getEpisode() + " " + provider.getName() + " "
				+ downloaders.getDownloadOutput();
	}

}
