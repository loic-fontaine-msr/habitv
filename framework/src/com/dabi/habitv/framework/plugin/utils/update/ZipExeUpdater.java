package com.dabi.habitv.framework.plugin.utils.update;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.log4j.Logger;

import com.dabi.habitv.framework.plugin.api.update.UpdatablePluginInterface;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.update.FindArtifactUtils.ArtifactVersion;
import com.dabi.habitv.framework.plugin.utils.update.UpdatablePluginEvent.UpdatablePluginStateEnum;
import com.dabi.habitv.framework.pub.Publisher;

public class ZipExeUpdater extends Updater {

	private static final Logger LOG = Logger.getLogger(ZipExeUpdater.class);

	private final Publisher<UpdatablePluginEvent> updatePublisher;

	private final UpdatablePluginInterface updatablePlugin;

	public ZipExeUpdater(final UpdatablePluginInterface updatablePlugin, final String currentDir, final String groupId, final boolean autoriseSnapshot,
			final Publisher<UpdatablePluginEvent> updatePublisher) {
		super(currentDir, groupId, null, autoriseSnapshot);
		this.updatePublisher = updatePublisher;
		this.updatablePlugin = updatablePlugin;
	}

	@Override
	protected String getCurrentVersion(final File currentFile) {
		return updatablePlugin.getCurrentVersion();
	}

	@Override
	protected String getLocalExtension() {
		return "exe";
	}

	@Override
	protected String getServerExtension() {
		return "zip";
	}

	@Override
	protected boolean performUpdate(final File current, final ArtifactVersion artifactVersion) {
		// FIXME if is windows
		return true;
	}

	@Override
	protected void updateFile(final File current, final File newVersion) {
		// FIXME unzip...
		if (newVersion.exists()) {
			if (current.exists()) {
				try {
					Files.delete(current.toPath());
				} catch (final IOException e) {
					throw new TechnicalException(e);
				}
			}
			newVersion.renameTo(current);
		}
	}

	@Override
	protected void onUpdate(final File current, final ArtifactVersion artifactNewVersion) {
		LOG.info("Update of file " + artifactNewVersion.getArtifactId() + " version " + artifactNewVersion.getVersion());
		updatePublisher.addNews(new UpdatablePluginEvent(artifactNewVersion.getArtifactId(), artifactNewVersion.getVersion(), UpdatablePluginStateEnum.DOWNLOADING));
	}

	@Override
	protected void onUpdateDone(final File current, final ArtifactVersion artifactNewVersion) {
		LOG.info("Update of file " + artifactNewVersion.getArtifactId() + " version " + artifactNewVersion.getVersion() + " done");
		updatePublisher.addNews(new UpdatablePluginEvent(artifactNewVersion.getArtifactId(), artifactNewVersion.getVersion(), UpdatablePluginStateEnum.DONE));

	}

	@Override
	protected void onUpdateError(final File current, final ArtifactVersion artifactNewVersion) {
		LOG.error("Error while updating file " + artifactNewVersion.getArtifactId() + " version " + artifactNewVersion.getVersion());
		updatePublisher.addNews(new UpdatablePluginEvent(artifactNewVersion.getArtifactId(), artifactNewVersion.getVersion(), UpdatablePluginStateEnum.ERROR));
	}

	@Override
	protected void onChecking(final String fileToUpdate) {
	}

	@Override
	protected boolean deleteFiles() {
		d		return false;
	}
}
