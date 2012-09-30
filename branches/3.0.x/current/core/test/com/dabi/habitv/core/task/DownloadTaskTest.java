package com.dabi.habitv.core.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.core.dao.DownloadedDAO;
import com.dabi.habitv.core.event.EpisodeStateEnum;
import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.publisher.Publisher;
import com.dabi.habitv.core.publisher.Subscriber;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class DownloadTaskTest {

	private DownloadTask task;

	private boolean downloaded = false;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	public void init(final boolean toFail) {
		final CategoryDTO category = new CategoryDTO("channel", "category", "identifier", "extension");
		final EpisodeDTO episode = new EpisodeDTO(category, "episode1234567890123456789012345678901234567890123456789", "videoUrl");
		final ExecutorFailedException executorFailedException = new ExecutorFailedException("cmd", "fullOuput", null);
		final DownloadFailedException downloadFailedException = new DownloadFailedException(executorFailedException);
		final PluginProviderInterface provider = new PluginProviderInterface() {

			@Override
			public void setClassLoader(final ClassLoader classLoader) {
			}

			@Override
			public String getName() {
				return "provider";
			}

			@Override
			public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
				return null;
			}

			@Override
			public Set<CategoryDTO> findCategory() {
				return null;
			}

			@Override
			public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener,
					final EpisodeDTO episode) throws DownloadFailedException, NoSuchDownloaderException {
				assertEquals("episode1234567890123456789012345678901234567890123456789_episode123456789012345678901234567890123_channel_category_extension",
						downloadOuput);

				cmdProgressionListener.listen("0");

				try {
					Thread.sleep(10);
				} catch (final InterruptedException e) {
					fail(e.getMessage());
				}

				cmdProgressionListener.listen("50");

				if (toFail) {
					throw downloadFailedException;
				}

				try {
					Thread.sleep(10);
				} catch (final InterruptedException e) {
					fail(e.getMessage());
				}

				cmdProgressionListener.listen("100");
			}
		};
		final DownloaderDTO downloader = new DownloaderDTO(null, null, "#EPISODE_NAME#_#EPISODE_NAME_CUT#_#CHANNEL_NAME#_#TVSHOW_NAME#_#EXTENSION#", "indexDir");
		final Publisher<RetreiveEvent> publisher = new Publisher<>();
		final Subscriber<RetreiveEvent> subscriber = new Subscriber<RetreiveEvent>() {

			private int i = 0;

			@Override
			public void update(final RetreiveEvent event) {
				switch (i) {
				case 0:
					assertEquals(new RetreiveEvent(episode, EpisodeStateEnum.DOWNLOADING), event);
					break;
				case 1:
					assertEquals(new RetreiveEvent(episode, EpisodeStateEnum.DOWNLOADING, "0"), event);
					break;
				case 2:
					assertEquals(new RetreiveEvent(episode, EpisodeStateEnum.DOWNLOADING, "50"), event);
					break;
				case 3:
					if (toFail) {
						assertEquals(new RetreiveEvent(episode, EpisodeStateEnum.DOWNLOAD_FAILED, downloadFailedException, "download"), event);
					} else {
						assertEquals(new RetreiveEvent(episode, EpisodeStateEnum.DOWNLOADING, "100"), event);
					}
					break;
				case 4:
					assertEquals(new RetreiveEvent(episode, EpisodeStateEnum.DOWNLOADED), event);
					break;
				default:
					fail("unexpected event" + event);
					break;
				}
				i++;
			}
		};
		publisher.attach(subscriber);
		final DownloadedDAO downloadedDAO = new DownloadedDAO("channelName", "tvShow", ".") {

			@Override
			public void addDownloadedFiles(final String... files) {
				downloaded = true;
			}

		};
		task = new DownloadTask(episode, provider, downloader, publisher, downloadedDAO);
		assertTrue(task.equals(task));
		assertEquals(task.hashCode(), task.hashCode());
	}

	@Test
	public final void testDownloadTaskSuccess() {
		init(false);
		task.addedTo("download", null);
		task.call();
		assertTrue(downloaded);
	}

	@Test(expected = TaskFailedException.class)
	public final void testDownloadTaskFailed() {
		init(true);
		task.addedTo("download", null);
		task.call();
		assertFalse(downloaded);
	}

	@Test
	public final void testDownloadRemovePreviousFile() throws IOException {
		final String filename = "episode1234567890123456789012345678901234567890123456789_episode123456789012345678901234567890123_channel_category_extension";
		final FileWriter fileWriter = new FileWriter(filename);
		fileWriter.write("test");
		fileWriter.close();
		init(false);
		task.addedTo("download", null);
		task.call();
		assertTrue(downloaded);
		assertTrue(!(new File(filename)).exists());
	}
}