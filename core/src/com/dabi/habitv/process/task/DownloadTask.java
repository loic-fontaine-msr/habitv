package com.dabi.habitv.process.task;

import com.dabi.habitv.dldao.DownloadedDAO;
import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.process.publisher.EpisodeStateEnum;
import com.dabi.habitv.process.publisher.Publisher;
import com.dabi.habitv.process.publisher.RetreiveEvent;

public class DownloadTask extends AbstractEpisodeTask {

	private final PluginProviderInterface provider;

	private final DownloaderDTO downloader;

	private final Publisher<RetreiveEvent> publisher;

	private final DownloadedDAO downloadedDAO;

	public DownloadTask(final EpisodeDTO episode, final PluginProviderInterface provider, final DownloaderDTO downloader,
			final Publisher<RetreiveEvent> publisher, final DownloadedDAO downloadedDAO) {
		super(episode);
		this.provider = provider;
		this.downloader = downloader;
		this.publisher = publisher;
		this.downloadedDAO = downloadedDAO;
	}

	@Override
	protected void added() {

	}

	@Override
	protected void failed() {
		publisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.DOWNLOAD_FAILED));
	}

	@Override
	protected void ended() {
		downloadedDAO.addDownloadedFiles(getEpisode().getName());
		publisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.DOWNLOADED));
	}

	@Override
	protected void started() {
		publisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.DOWNLOADING));
	}

	@Override
	protected Object doCall() throws DownloadFailedException, NoSuchDownloaderException {
		provider.download(TokenReplacer.replaceAll(downloader.getDownloadOutputDir(), getEpisode()), downloader, new CmdProgressionListener() {
			@Override
			public void listen(final String progression) {
				publisher.addNews(new RetreiveEvent(getEpisode(), EpisodeStateEnum.DOWNLOADING, progression));
			}
		}, getEpisode());
		return null;
	}

}
