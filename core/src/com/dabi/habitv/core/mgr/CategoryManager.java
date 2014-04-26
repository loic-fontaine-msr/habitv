package com.dabi.habitv.core.mgr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.dabi.habitv.core.config.HabitTvConf;
import com.dabi.habitv.core.event.SearchCategoryEvent;
import com.dabi.habitv.core.event.SearchCategoryStateEnum;
import com.dabi.habitv.core.publisher.Publisher;
import com.dabi.habitv.core.task.SearchCategoryResult;
import com.dabi.habitv.core.task.SearchCategoryTask;
import com.dabi.habitv.core.task.TaskMgr;
import com.dabi.habitv.core.task.TaskMgrListener;
import com.dabi.habitv.core.task.TaskTypeEnum;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;

public class CategoryManager extends AbstractManager {

	private final TaskMgr<SearchCategoryTask, SearchCategoryResult> searchCategoryMgr;

	private final Publisher<SearchCategoryEvent> searchCategoryPublisher;

	private static final Logger LOG = Logger.getLogger(CategoryManager.class);

	CategoryManager(final Collection<PluginProviderInterface> pluginProviderList, final Map<String, Integer> taskName2PoolSize) {
		super(pluginProviderList);
		// task mgrs
		searchCategoryMgr = new TaskMgr<SearchCategoryTask, SearchCategoryResult>(TaskTypeEnum.category.getPoolSize(taskName2PoolSize),
				buildCategoryTaskMgrListener(), null);
		// publisher
		searchCategoryPublisher = new Publisher<>();
	}

	Map<String, Set<CategoryDTO>> findCategory() {
		final Map<String, Set<CategoryDTO>> channel2Categories = new HashMap<>();
		final List<SearchCategoryTask> taskList = new ArrayList<>();
		// search is parallelized, the final result will be build with the
		// future result
		for (final PluginProviderInterface provider : getPluginProviderList()) {
			final SearchCategoryTask searchCategoryTask = new SearchCategoryTask(provider.getName(), provider, searchCategoryPublisher);
			searchCategoryMgr.addTask(searchCategoryTask);
			taskList.add(searchCategoryTask);
		}
		SearchCategoryResult searchCategoryResult;
		for (final SearchCategoryTask searchTask : taskList) {
			try {
				searchCategoryResult = searchTask.getResult();
				channel2Categories.put(searchCategoryResult.getChannel(), searchCategoryResult.getCategoryList());
			} catch (final TechnicalException e) {
				searchCategoryPublisher.addNews(new SearchCategoryEvent(SearchCategoryStateEnum.ERROR, HabitTvConf.GRABCONFIG_XML_FILE));
				// throw new TechnicalException(e);
				// if one plugin failed keep generating the grabconfig file
				LOG.error("one plugin failed, keep generating the grabconfig file", e);
			}
		}
		return channel2Categories;
	}

	private TaskMgrListener buildCategoryTaskMgrListener() {
		return new TaskMgrListener() {

			@Override
			public void onFailed(final Throwable throwable) {
				searchCategoryPublisher.addNews(new SearchCategoryEvent(SearchCategoryStateEnum.ERROR, HabitTvConf.GRABCONFIG_XML_FILE));
			}

			@Override
			public void onAllTreatmentDone() {
				searchCategoryPublisher.addNews(new SearchCategoryEvent(SearchCategoryStateEnum.DONE, HabitTvConf.GRABCONFIG_XML_FILE));
			}
		};
	}

	public Publisher<SearchCategoryEvent> getSearchCategoryPublisher() {
		return searchCategoryPublisher;
	}

	void forceEnd() {
		searchCategoryMgr.shutdownNow();
	}

}
