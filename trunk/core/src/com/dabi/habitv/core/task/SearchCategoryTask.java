package com.dabi.habitv.core.task;

import com.dabi.habitv.core.event.SearchCategoryEvent;
import com.dabi.habitv.core.event.SearchCategoryStateEnum;
import com.dabi.habitv.core.publisher.Publisher;
import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;

public final class SearchCategoryTask extends AbstractTask<SearchCategoryResult> {

	private final String channel;

	private final PluginProviderInterface provider;

	private final Publisher<SearchCategoryEvent> searchCategoryPublisher;

	public SearchCategoryTask(final String channel, final PluginProviderInterface provider, final Publisher<SearchCategoryEvent> searchCategoryPublisher) {
		this.channel = channel;
		this.provider = provider;
		this.searchCategoryPublisher = searchCategoryPublisher;
	}

	@Override
	protected void added() {
		LOG.error("Waiting for Grabbing categories for " + channel);
		searchCategoryPublisher.addNews(new SearchCategoryEvent(channel, SearchCategoryStateEnum.CHANNEL_CATEGORIES_TO_BUILD));
	}

	@Override
	protected void failed(final Throwable e) {
		LOG.error("Grabbing categories for " + channel + " failed", e);
		searchCategoryPublisher.addNews(new SearchCategoryEvent(channel, SearchCategoryStateEnum.ERROR));
	}

	@Override
	protected void ended() {
		LOG.info("Grabbing categories for " + channel + " done");
		searchCategoryPublisher.addNews(new SearchCategoryEvent(channel, SearchCategoryStateEnum.CATEGORIES_BUILT));
	}

	@Override
	protected void started() {
		LOG.info("Grabbing categories for " + channel + "...");
		searchCategoryPublisher.addNews(new SearchCategoryEvent(channel, SearchCategoryStateEnum.BUILDING_CATEGORIES));
	}

	@Override
	protected SearchCategoryResult doCall() {
		return new SearchCategoryResult(channel, provider.findCategory());
	}

	@Override
	public String toString() {
		return "SearchingCategory" + channel;
	}

}
