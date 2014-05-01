package com.dabi.habitv.core.updater;

import org.apache.log4j.Logger;

import com.dabi.habitv.api.plugin.pub.Publisher;
import com.dabi.habitv.core.event.UpdatePluginEvent;
import com.dabi.habitv.core.event.UpdatePluginStateEnum;
import com.dabi.habitv.framework.FWKProperties;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.utils.OSUtils;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.framework.plugin.utils.update.Updater;

public class UpdateManager {

	private static final Logger LOG = Logger.getLogger(UpdateManager.class);

	private final String site;

	private final String currentDir;

	private final String groupId;

	private final String coreVersion;

	private final boolean autoriseSnapshot;

	private final Publisher<UpdatePluginEvent> updatePublisher = new Publisher<>();

	private UpdateManager(final String site, final String currentDir, final String groupId, final String coreVersion, final boolean autoriseSnapshot) {
		this.site = site;
		this.currentDir = currentDir;
		this.groupId = groupId;
		this.coreVersion = coreVersion;
		this.autoriseSnapshot = autoriseSnapshot;
	}

	public UpdateManager(final boolean autoriseSnapshot) {
		this(FrameworkConf.UPDATE_URL, OSUtils.getCurrentDir(), FrameworkConf.GROUP_ID, FWKProperties.getVersion(), autoriseSnapshot);
	}

	public void process() {
		LOG.info("Checking plugin updates...");
		updatePublisher.addNews(new UpdatePluginEvent(UpdatePluginStateEnum.STARTING_ALL));
		process("provider", "downloader", "exporter");
		updatePublisher.addNews(new UpdatePluginEvent(UpdatePluginStateEnum.ALL_DONE));
		LOG.info("Update done");
	}

	private void process(final String... toUpdateTab) {
		final Updater updater = new JarUpdater(currentDir, groupId, coreVersion, autoriseSnapshot, updatePublisher);
		for (final String folderToUpdate : toUpdateTab) {
			try {
				updater.update(folderToUpdate, RetrieverUtils.getUrlContent(site + "/" + folderToUpdate + ".txt", null).split("\\r\\n"));
			} catch (final Exception e) {
				LOG.error("", e);
			}
		}
	}

	public Publisher<UpdatePluginEvent> getUpdatePublisher() {
		return updatePublisher;
	}

}
