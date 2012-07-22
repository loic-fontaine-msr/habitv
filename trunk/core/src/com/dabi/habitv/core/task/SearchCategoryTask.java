package com.dabi.habitv.core.task;

import com.dabi.habitv.framework.plugin.api.provider.PluginProviderInterface;

public final class SearchCategoryTask extends AbstractTask<SearchCategoryResult> {

	private final String channel;

	private final PluginProviderInterface provider;

	public SearchCategoryTask(final String channel, final PluginProviderInterface provider) {
		this.channel = channel;
		this.provider = provider;
	}

	@Override
	protected void added() {
		LOG.error("Waiting for Grabing categories for " + channel);
	}

	@Override
	protected void failed(final Exception e) {
		LOG.error("Grabing categories for " + channel + " failed");
	}

	@Override
	protected void ended() {
		LOG.info("Grabing categories for " + channel + " done");
	}

	@Override
	protected void started() {
		LOG.info("Grabing categories for " + channel + "...");
	}

	@Override
	protected SearchCategoryResult doCall() {
		return new SearchCategoryResult(channel, provider.findCategory());
	}

}
