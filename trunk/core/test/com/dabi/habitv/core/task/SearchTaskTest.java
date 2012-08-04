package com.dabi.habitv.core.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.core.dao.DownloadedDAO;
import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchEvent;
import com.dabi.habitv.core.event.SearchStateEnum;
import com.dabi.habitv.core.publisher.Publisher;
import com.dabi.habitv.core.publisher.Subscriber;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.DownloaderDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.dto.ExportDTO;
import com.dabi.habitv.framework.plugin.api.dto.ExporterDTO;
import com.dabi.habitv.framework.plugin.api.exporter.PluginExporterInterface;
import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.exception.ExportFailedException;
import com.dabi.habitv.framework.plugin.exception.NoSuchDownloaderException;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.CmdProgressionListener;

public class SearchTaskTest {

	private SearchTask task;

	private boolean done;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		done = false;
	}

	@After
	public void tearDown() throws Exception {
	}

	public void init(final boolean toFail) {
		final CategoryDTO category1 = new CategoryDTO("channel", "category1", "identifier1", "extension");
		category1.getInclude().add("episode1.*");
		final CategoryDTO subCategory = new CategoryDTO("channel", "subcategory1", "subidentifier1", "subextension");
		category1.addSubCategory(subCategory);
		final CategoryDTO category2 = new CategoryDTO("channel", "category2", "identifier2", "extension2");
		final CategoryDTO category3 = new CategoryDTO("channel", "category3", "identifier3", "extension3");
		category3.getExclude().add("episodeExcluded");
		String url = "videoUrl";
		if (toFail) {
			url = "1";
		}
		final EpisodeDTO episodeRoot = new EpisodeDTO(category1, "episode1Root", url);
		final EpisodeDTO episode1 = new EpisodeDTO(subCategory, "episode1", url);
		final EpisodeDTO episodeNotIncluded = new EpisodeDTO(subCategory, "episodeNotIncluded", url);
		final EpisodeDTO episode2 = new EpisodeDTO(category2, "episode2", url);
		final EpisodeDTO episodeDl = new EpisodeDTO(category2, "episodeDl", url);
		final EpisodeDTO episodeExcluded = new EpisodeDTO(category2, "episodeExcluded", url);
		final EpisodeDTO episode3 = new EpisodeDTO(category3, "episode3", url);
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
				if (toFail) {
					throw new TechnicalException("error");
				}
				final Set<EpisodeDTO> episodeList = new HashSet<>();
				if (category.getName().equals("subcategory1")) {
					episodeList.add(episode1);
					episodeList.add(episodeNotIncluded);
				} else if (category.getName().equals("subcategory1")) {
					episodeList.add(episodeRoot);
				} else if (category.getName().equals("category3")) {
					episodeList.add(episode3);
				} else {
					episodeList.add(episode2);
					episodeList.add(episodeDl);
					episodeList.add(episodeExcluded);
				}
				return episodeList;
			}

			@Override
			public Set<CategoryDTO> findCategory() {
				return null;
			}

			@Override
			public void download(final String downloadOuput, final DownloaderDTO downloaders, final CmdProgressionListener cmdProgressionListener,
					final EpisodeDTO episode) throws DownloadFailedException, NoSuchDownloaderException {

			}
		};

		final DownloaderDTO downloader = new DownloaderDTO(null, null,
				"episode1234567890123456789012345678901234567890123456789/episode123456789012345678901234567890123/channel/category/extension", "indexDir");
		final Publisher<SearchEvent> searchPublisher = new Publisher<>();
		final Subscriber<SearchEvent> subscriber = new Subscriber<SearchEvent>() {

			private int i = 0;

			@Override
			public void update(final SearchEvent event) {
				switch (i) {
				case 0:
					assertEquals(new SearchEvent(provider.getName(), SearchStateEnum.CHECKING_EPISODES), event);
					break;
				case 1:
					if (toFail) {
						assertEquals(new SearchEvent(provider.getName(), SearchStateEnum.ERROR), event);
						done = true;
					} else {
						assertEquals(new SearchEvent(provider.getName(), SearchStateEnum.DONE), event);
						done = true;
					}
				case 2:
					assertEquals(new SearchEvent(provider.getName(), SearchStateEnum.BUILD_INDEX), event);
					break;
				default:
					fail("unexpected event" + event);
					break;
				}
				i++;
			}
		};
		searchPublisher.attach(subscriber);
		final Map<String, PluginExporterInterface> exporterName2exporter = new HashMap<>();
		final PluginExporterInterface pluginExporter = new PluginExporterInterface() {

			@Override
			public void setClassLoader(final ClassLoader classLoader) {

			}

			@Override
			public String getName() {
				return "exporter";
			}

			@Override
			public void export(final String cmd, final CmdProgressionListener listener) throws ExportFailedException {
			}
		};
		exporterName2exporter.put("exporter", pluginExporter);
		final List<ExportDTO> exporterList = new ArrayList<>();
		final List<ExportDTO> exporterSubList = new ArrayList<>();
		final ExportDTO subExporter = new ExportDTO("#EPISODE_NAME#", "episode", "exporter", "subexport1Out", "subcmd 1", null);
		exporterSubList.add(subExporter);
		final ExportDTO export1 = new ExportDTO("#EPISODE_NAME#", "episode", "export1", "export1Out", "cmd 1", exporterSubList);
		exporterList.add(export1);
		final ExportDTO export2 = new ExportDTO("#EPISODE_NAME#", "episode2", "export2", "export2Out", "cmd 2", null);
		exporterList.add(export2);
		final ExporterDTO exporter = new ExporterDTO(exporterName2exporter, exporterList);
		final Publisher<RetreiveEvent> retreivePublisher = new Publisher<>();
		final TaskAdder taskAdder = new TaskAdder() {

			private int i = 0;

			@Override
			public Future<Object> addRetreiveTask(final RetreiveTask retreiveTask) {
				switch (i) {
				case 0:
					assertEquals(episode1, retreiveTask.getEpisode());
					break;
				case 1:
					assertEquals(episode2, retreiveTask.getEpisode());
					done = true;
					break;
				default:
					fail("unexpected task" + retreiveTask);
					break;
				}
				i++;
				return null;
			}

			@Override
			public Future<Object> addExportTask(final ExportTask exportTask, final String category) {
				return null;
			}

			@Override
			public Future<Object> addDownloadTask(final DownloadTask downloadTask, final String channel) {
				return null;
			}

		};
		final Set<CategoryDTO> categories = new HashSet<>();
		categories.add(category1);
		categories.add(category2);
		categories.add(category3);
		task = new SearchTask(provider, categories, taskAdder, searchPublisher, retreivePublisher, downloader, exporter) {

			@Override
			protected DownloadedDAO buildDownloadDAO(final String categoryName) {
				assertNotNull(super.buildDownloadDAO(categoryName));

				final DownloadedDAO dao = buildDLDAO(categoryName);

				return dao;
			}

		};
	}

	private DownloadedDAO buildDLDAO(final String categoryName) {
		final DownloadedDAO dao = new DownloadedDAO("channel", categoryName, ".") {

			@Override
			public Set<String> findDownloadedFiles() {
				final Set<String> dlFiles = new HashSet<>();
				dlFiles.add("episodeDl");
				return dlFiles;
			}

			@Override
			public void addDownloadedFiles(final String... files) {
				assertEquals("episode3", files[0]);
			}

			@Override
			public boolean isIndexCreated() {
				boolean ret = true;
				if (getCategory().equals("category3")) {
					ret = false;
				}
				return ret;
			}

		};
		return dao;
	}

	@Test
	public final void testSearchCategoryTaskSuccess() {
		init(false);
		task.addedTo("retreive");
		task.call();
		assertTrue(done);
	}

	@Test(expected = TechnicalException.class)
	public final void testSearchCategoryTaskFailed() {
		init(true);
		task.addedTo("retreive");
		task.call();
		assertTrue(done);
	}
}
