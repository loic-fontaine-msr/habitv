package com.dabi.habitv.framework.updater;

import org.apache.log4j.Logger;

public class UpdateManager {

	private static final Logger LOG = Logger.getLogger(UpdateManager.class);

	private final String site;

	private final String currentDir;

	public UpdateManager(final String site, final String currentDir) {
		this.site = site;
		this.currentDir = currentDir;
	}

	public void process(final String... toUpdateTab) {
		final Updater updater = new Updater(site, currentDir);
		for (final String fileToUpdate : toUpdateTab) {
			try {
				updater.update(fileToUpdate);
			} catch (final Exception e) {
				LOG.error("", e);
			}
		}
	}

}
