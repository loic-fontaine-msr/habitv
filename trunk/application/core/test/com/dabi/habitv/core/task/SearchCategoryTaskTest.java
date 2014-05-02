package com.dabi.habitv.core.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.api.plugin.api.CmdProgressionListener;
import com.dabi.habitv.api.plugin.api.PluginProviderDownloaderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.DownloadParamDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.api.plugin.holder.DownloaderPluginHolder;
import com.dabi.habitv.api.plugin.pub.Publisher;
import com.dabi.habitv.api.plugin.pub.Subscriber;
import com.dabi.habitv.core.event.SearchCategoryEvent;
import com.dabi.habitv.core.event.SearchCategoryStateEnum;

public class SearchCategoryTaskTest {

	private SearchCategoryTask task;

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
				if (toFail) {
					throw new TechnicalException("string");
				}
				final Set<CategoryDTO> categoryDTOs = new HashSet<>();
				categoryDTOs.add(new CategoryDTO("channel", "name", "identifier", "extension"));
				return categoryDTOs;
			}

			@Override
			public void download(final DownloadParamDTO downloadParam, final DownloaderPluginHolder downloaders, final CmdProgressionListener listener)
					throws DownloadFailedException {

			}
		};

		final Publisher<SearchCategoryEvent> publisher = new Publisher<>();
		final Subscriber<SearchCategoryEvent> subscriber = new Subscriber<SearchCategoryEvent>() {

			private int i = 0;

			@Override
			public void update(final SearchCategoryEvent event) {
				switch (i) {
				case 0:
					assertEquals(new SearchCategoryEvent("channel", SearchCategoryStateEnum.CHANNEL_CATEGORIES_TO_BUILD), event);
					break;
				case 1:
					assertEquals(new SearchCategoryEvent("channel", SearchCategoryStateEnum.BUILDING_CATEGORIES), event);
					break;
				case 2:
					if (toFail) {
						assertEquals(new SearchCategoryEvent("channel", SearchCategoryStateEnum.ERROR), event);
					} else {
						assertEquals(new SearchCategoryEvent("channel", SearchCategoryStateEnum.DONE), event);
					}
					done = true;
					break;
				default:
					fail("unexpected event" + event);
					break;
				}
				i++;
			}
		};
		publisher.attach(subscriber);

		task = new SearchCategoryTask("channel", provider, publisher);
	}

	@Test
	public final void testSearchCategoryTaskSuccess() {
		init(false);
		task.addedTo("retreive", null);
		task.call();
		assertTrue(done);
	}

	@Test(expected = TaskFailedException.class)
	public final void testSearchCategoryTaskFailed() {
		init(true);
		task.addedTo("retreive", null);
		task.call();
		assertTrue(done);
	}

}
