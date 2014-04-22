package com.dabi.habitv.updater;

import org.apache.log4j.Logger;

import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public class UpdateManager {

	private static final Logger LOG = Logger.getLogger(UpdateManager.class);

	private final String site;

	private final String currentDir;

	private final String groupId;

	private final String coreVersion;

	private final boolean autoriseSnapshot;

	public UpdateManager(final String site, final String currentDir, final String groupId, final String coreVersion, final boolean autoriseSnapshot) {
		this.site = site;
		this.currentDir = currentDir;
		this.groupId = groupId;
		this.coreVersion = coreVersion;
		this.autoriseSnapshot = autoriseSnapshot;
	}

	public void process(final String... toUpdateTab) {
		final Updater updater = new Updater(currentDir, groupId, coreVersion, autoriseSnapshot);
		for (final String folderToUpdate : toUpdateTab) {
			try {
				updater.update(folderToUpdate, RetrieverUtils.getUrlContent(site + "/" + folderToUpdate + ".txt", null).split("\\r\\n"));
			} catch (final Exception e) {
				LOG.error("", e);
			}
		}
	}

}
