package com.dabi.habitv.process.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.dabi.habitv.config.HabitTvConf;
import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;
import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.process.mgr.TaskMgr;
import com.dabi.habitv.process.mgr.TaskMgrListener;
import com.dabi.habitv.process.publisher.Publisher;
import com.dabi.habitv.process.publisher.SearchCategoryEvent;
import com.dabi.habitv.process.publisher.SearchCategoryStateEnum;

public class CategoryManager extends AbstractManager {

	private final TaskMgr<SearchCategoryTask, SearchCategoryResult> searchCategoryMgr;

	private final Publisher<SearchCategoryEvent> searchCategoryPublisher;

	public CategoryManager(final Collection<PluginProviderInterface> pluginProviderList, final Map<String, Integer> taskName2PoolSize) {
		super(pluginProviderList);
		// task mgrs
		searchCategoryMgr = new TaskMgr<SearchCategoryTask, SearchCategoryResult>(taskName2PoolSize.get("category"), buildCategoryTaskMgrListener());
		// publisher
		searchCategoryPublisher = new Publisher<>();
	}

	public Map<String, Set<CategoryDTO>> findCategory() {
		final Map<String, Set<CategoryDTO>> channel2Categories = new HashMap<>();
		final List<Future<SearchCategoryResult>> futureList = new ArrayList<>();
		// search is parallelized, the final result will be build with the
		// future result
		for (final PluginProviderInterface provider : getPluginProviderList()) {
			futureList.add(searchCategoryMgr.addTask(new SearchCategoryTask(provider.getName(), provider)));
		}
		for (final Future<SearchCategoryResult> futureResult : futureList) {
			try {
				channel2Categories.put(futureResult.get().getChannel(), futureResult.get().getCategoryList());
			} catch (InterruptedException | ExecutionException e) {
				searchCategoryPublisher.addNews(new SearchCategoryEvent(SearchCategoryStateEnum.ERROR, HabitTvConf.GRABCONFIG_XML_FILE));
				throw new TechnicalException(e);
			}
		}
		searchCategoryPublisher.addNews(new SearchCategoryEvent(SearchCategoryStateEnum.DONE, HabitTvConf.GRABCONFIG_XML_FILE));
		return channel2Categories;
	}

	private TaskMgrListener buildCategoryTaskMgrListener() {
		return new TaskMgrListener() {

			@Override
			public void onFailed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAllTreatmentDone() {
				// TODO Auto-generated method stub

			}
		};
	}
}
