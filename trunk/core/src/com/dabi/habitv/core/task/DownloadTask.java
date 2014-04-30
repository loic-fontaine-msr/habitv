package com.dabi.habitv.core.task;

import java.io.File;

import com.dabi.habitv.core.dao.DownloadedDAO;
import com.dabi.habitv.core.event.EpisodeStateEnum;
import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.token.TokenReplacer;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;
import com.dabi.habitv.framework.pub.Publisher;

public class DownloadTask extends AbstractEpisodeTask {

	private final PluginProviderInterface provider;

	private final DownloaderPluginHolder downloader;

	private final Publisher<RetreiveEvent> publisher;

	private final DownloadedDAO downloadedDAO;

	public DownloadTask(final EpisodeDTO episode, final PluginProviderInterface provider, final DownloaderPluginHolder downloader,
			final Publisher<RetreiveEvent> publisher, final DownloadedDAO downloadedDAO) {
		super(episode);
		this.provider = provider;
		this.downloader = downloader;
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
		publisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.DOWNLOAD_FAILED, e, "download"));
	}

	@Override
	protected void ended() {
		LOG.info("Download of " + getEpisode() + " done");
		downloadedDAO.addDownloadedFiles(getEpisode().getName());
		publisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.DOWNLOADED));
	}

	@Override
	protected void started() {
		LOG.info("Download of " + getEpisode() + " is starting");
		publisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.DOWNLOADING));
	}

	@Override
	protected Object doCall() throws DownloadFailedException {
		final String outputFilename = TokenReplacer.replaceAll(downloader.getDownloadOutput(), getEpisode());
		final String outputTmpFileName;
		if (!outputFilename.contains(".torrent")) {
			outputTmpFileName = outputFilename + ".tmp";
		} else {
			outputTmpFileName = outputFilename;
		}
		//
		// delete to prevent resuming since most of the download can't resume
		final File outputFile = new File(outputFilename);
		// create download dir if doesn't exist
		if (!outputFile.getParentFile().exists()) {
			outputFile.getParentFile().mkdir();
		}
		if (outputFile.exists()) {
			if (!outputFile.delete()) {
				throw new TechnicalException("can't delete file " + outputFile.getAbsolutePath());
			}
		}
		provider.download(outputTmpFileName, downloader, new CmdProgressionListener() {
			@Override
			public void listen(final String progression) {
				publisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.DOWNLOADING, progression));
			}
		}, getEpisode());
		final File file = new File(outputTmpFileName);
		if (file.exists() && !file.renameTo(new File(outputFilename))) {
			throw new TechnicalException("can't rename");
		}
		return null;
	}

	@Override
	public int hashCode() {
		return getEpisode().hashCode() + downloader.hashCode();
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
		return "DL" + getEpisode() + " " + provider.getName() + " " + downloader.getDownloadOutput();
	}

}
