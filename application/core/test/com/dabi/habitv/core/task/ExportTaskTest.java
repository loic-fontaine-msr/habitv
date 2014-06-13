package com.dabi.habitv.core.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.api.plugin.api.PluginExporterInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.dto.ExportDTO;
import com.dabi.habitv.api.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.api.plugin.exception.ExportFailedException;
import com.dabi.habitv.api.plugin.holder.ProcessHolder;
import com.dabi.habitv.api.plugin.pub.Publisher;
import com.dabi.habitv.api.plugin.pub.Subscriber;
import com.dabi.habitv.core.event.EpisodeStateEnum;
import com.dabi.habitv.core.event.RetreiveEvent;

public class ExportTaskTest {

	private ExportTask task;

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
		final CategoryDTO category = new CategoryDTO("channel", "category",
				"identifier", "extension");
		final EpisodeDTO episode = new EpisodeDTO(category,
				"episode1234567890123456789012345678901234567890123456789",
				"videoUrl");
		final ExecutorFailedException executorFailedException = new ExecutorFailedException(
				"cmd", "fullOuput", "lastLine", null);
		final ExportFailedException exportFailedException = new ExportFailedException(
				executorFailedException);
		final PluginExporterInterface pluginExporter = new PluginExporterInterface() {

			@Override
			public String getName() {
				return "exporter";
			}

			@Override
			public ProcessHolder export(final String cmdProcessor,
					final String cmd) throws ExportFailedException {
				MockProcessHolder processHolder = new MockProcessHolder();

				assertEquals("episode1234567890123/channel/category/extension",
						cmd);

				processHolder.setProgression("0");

				try {
					Thread.sleep(10);
				} catch (final InterruptedException e) {
					fail(e.getMessage());
				}

				processHolder.setProgression("50");

				if (toFail) {
					throw exportFailedException;
				}

				try {
					Thread.sleep(10);
				} catch (final InterruptedException e) {
					fail(e.getMessage());
				}

				processHolder.setProgression("100");

				return ProcessHolder.EMPTY_PROCESS_HOLDER;
			}
		};

		final Publisher<RetreiveEvent> publisher = new Publisher<>();
		final Subscriber<RetreiveEvent> subscriber = new Subscriber<RetreiveEvent>() {

			private int i = 0;

			@Override
			public void update(final RetreiveEvent event) {
				assertNotNull(event.hashCode());
				assertEquals(event, event);
				switch (i) {
				case 0:
					assertEquals(new RetreiveEvent(episode,
							EpisodeStateEnum.TO_EXPORT), event);
					break;
				case 1:
					if (toFail) {
						assertEquals(new RetreiveEvent(episode,
								EpisodeStateEnum.EXPORT_FAILED,
								exportFailedException, "output"), event);
					} else {
						assertEquals(new RetreiveEvent(episode,
								EpisodeStateEnum.EXPORT_STARTING), event);
					}
					break;
				case 2:
					assertEquals(new RetreiveEvent(episode,
							EpisodeStateEnum.EXPORT_FAILED), event);
					break;
				default:
					fail("unexpected event" + event);
					break;
				}
				i++;
			}
		};
		publisher.attach(subscriber);
		final ExportDTO export = new ExportDTO("conditionReference",
				"conditionPattern", "name", "output", null,
				"#EPISODE_NAME§20#/#CHANNEL_NAME#/#TVSHOW_NAME#/#EXTENSION#",
				null);
		task = new ExportTask(episode, export, pluginExporter, publisher, 0);
		assertTrue(task.equals(task));
		assertEquals(task.hashCode(), task.hashCode());
	}

	@Test
	public final void testExportTaskSuccess() {
		init(false);
		task.adding();
		task.addedTo("export", null);
		task.call();
	}

	@Test(expected = TaskFailedException.class)
	public final void testExportTaskFailed() {
		init(true);
		task.adding();
		task.addedTo("export", null);
		task.call();
	}

}
