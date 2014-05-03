package com.dabi.habitv.core.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.api.PluginExporterInterface;
import com.dabi.habitv.api.plugin.api.PluginProviderDownloaderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.dto.ExportDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.exception.ExportFailedException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.holder.ExporterPluginHolder;
import com.dabi.habitv.api.plugin.pub.Publisher;
import com.dabi.habitv.api.plugin.pub.Subscriber;
import com.dabi.habitv.core.dao.DownloadedDAO;
import com.dabi.habitv.core.event.EpisodeStateEnum;
import com.dabi.habitv.core.event.RetreiveEvent;

public class RetreiveTaskTest {

	private RetrieveTask task;

	private boolean retreived;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		retreived = false;
	}

	@After
	public void tearDown() throws Exception {
	}

	public void init(final boolean toFail) {
		final CategoryDTO category = new CategoryDTO("channel", "category", "identifier", "extension");
		String url = "videoUrl";
		if (toFail) {
			url = "";
		}
		final EpisodeDTO episode = new EpisodeDTO(category, "episode1234567890123456789012345678901234567890123456789", url);
		final PluginProviderDownloaderInterface provider = new PluginProviderDownloaderInterface() {

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
			public void download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders, final CmdProgressionListener listener)
					throws DownloadFailedException {

			}

			@Override
			public DownloadableState canDownload(final String downloadInput) {
				return DownloadableState.IMPOSSIBLE;
			}
		};

		final DownloaderPluginHolder downloader = new DownloaderPluginHolder(null, null, null,
				"episode1234567890123456789012345678901234567890123456789/episode123456789012345678901234567890123/channel/category/extension", "indexDir",
				"bin");
		final Publisher<RetreiveEvent> publisher = new Publisher<>();
		final Subscriber<RetreiveEvent> subscriber = new Subscriber<RetreiveEvent>() {

			private int i = 0;

			@Override
			public void update(final RetreiveEvent event) {
				switch (i) {
				case 0:
					assertEquals(new RetreiveEvent(episode, EpisodeStateEnum.TO_DOWNLOAD), event);
					break;
				case 1:
					if (toFail) {
						assertEquals(episode, event.getEpisode());
						assertEquals(EpisodeStateEnum.FAILED, event.getState());
					} else {
						assertEquals(new RetreiveEvent(episode, EpisodeStateEnum.READY), event);
						retreived = true;
					}
					break;
				default:
					fail("unexpected event" + event);
					break;
				}
				i++;
			}
		};
		publisher.attach(subscriber);
		final DownloadedDAO downloadedDAO = new DownloadedDAO("channelName", "tvShow", ".");

		final Map<String, PluginExporterInterface> exporterName2exporter = new HashMap<>();
		final PluginExporterInterface pluginExporter = new PluginExporterInterface() {

			@Override
			public String getName() {
				return "exporter";
			}

			@Override
			public void export(final String cmdProcessor, final String cmd, final CmdProgressionListener listener) throws ExportFailedException {

			}
		};
		exporterName2exporter.put("exporter", pluginExporter);
		final List<ExportDTO> exporterList = new ArrayList<>();
		final List<ExportDTO> exporterSubList = new ArrayList<>();
		final ExportDTO subExporter = new ExportDTO("#EPISODE_NAME#", "episode", "exporter", "subexport1Out", null, "subcmd 1", null);
		exporterSubList.add(subExporter);
		final ExportDTO export1 = new ExportDTO("#EPISODE_NAME#", "episode", "export1", "export1Out", null, "cmd 1", exporterSubList);
		exporterList.add(export1);
		final ExportDTO export2 = new ExportDTO("#EPISODE_NAME#", "episode2", "export2", "export2Out", null, "cmd 2", null);
		exporterList.add(export2);
		final ExporterPluginHolder exporter = new ExporterPluginHolder(exporterName2exporter, exporterList);
		final TaskAdder taskAdder = new TaskAdder() {

			private int i = 0;

			@Override
			public TaskAdResult addRetreiveTask(final RetrieveTask retreiveTask) {
				return new TaskAdResult(TaskState.ADDED);
			}

			@Override
			public TaskAdResult addExportTask(final ExportTask exportTask, final String category) {
				switch (i) {
				case 0:
					assertEquals(new ExportTask(episode, export1, pluginExporter, publisher, 0), exportTask);
					assertNull(category);
					break;
				case 1:
					assertEquals(new ExportTask(episode, subExporter, pluginExporter, publisher, 0), exportTask);
					assertNull(category);
					break;
				default:
					fail("unexpected task" + exportTask);
					break;
				}
				i++;
				return new TaskAdResult(TaskState.ADDED);
			}

			@Override
			public TaskAdResult addDownloadTask(final DownloadTask downloadTask, final String channel) {
				assertEquals(new DownloadTask(episode, provider, downloader, publisher, downloadedDAO), downloadTask);
				return new TaskAdResult(TaskState.ADDED);
			}

		};
		task = new RetrieveTask(episode, publisher, taskAdder, exporter, provider, downloader, downloadedDAO);
	}

	@Test
	public final void testRetreiveTaskSuccess() {
		init(false);
		task.addedTo("retreive", null);
		task.call();
		assertTrue(retreived);
	}

	@Test(expected = TaskFailedException.class)
	public final void testRetreiveTaskFailed() {
		init(true);
		task.addedTo("retreive", null);
		task.call();
	}

}
