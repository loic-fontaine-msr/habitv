package com.dabi.habitv.process.task;

import com.dabi.habitv.framework.plugin.api.PluginProviderInterface;

public final class SearchCategoryTask extends AbstractTask<SearchCategoryResult> {

	private final String channel;

	private final PluginProviderInterface provider;

	public SearchCategoryTask(final String channel, final PluginProviderInterface provider) {
		this.channel = channel;
		this.provider = provider;
	}

	@Override
	protected void added() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void failed() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void ended() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void started() {
		// TODO Auto-generated method stub

	}

	@Override
	protected SearchCategoryResult doCall() {
		return new SearchCategoryResult(channel, provider.findCategory());
	}

}
