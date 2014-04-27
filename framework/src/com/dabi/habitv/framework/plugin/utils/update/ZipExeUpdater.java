package com.dabi.habitv.framework.plugin.utils.update;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dabi.habitv.framework.plugin.api.update.UpdatablePluginInterface;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.framework.plugin.utils.OSUtils;
import com.dabi.habitv.framework.plugin.utils.ZipUtils;
import com.dabi.habitv.framework.plugin.utils.update.FindArtifactUtils.ArtifactVersion;
import com.dabi.habitv.framework.plugin.utils.update.UpdatablePluginEvent.UpdatablePluginStateEnum;
import com.dabi.habitv.framework.pub.Publisher;

public class ZipExeUpdater extends Updater {

	private static final Logger LOG = Logger.getLogger(ZipExeUpdater.class);

	private final Publisher<UpdatablePluginEvent> updatePublisher;

	private final UpdatablePluginInterface updatablePlugin;

	private Map<String, String> parameters;

	public ZipExeUpdater(final UpdatablePluginInterface updatablePlugin,
			final String currentDir, final String groupId,
			final boolean autoriseSnapshot,
			final Publisher<UpdatablePluginEvent> updatePublisher,
			final Map<String, String> parameters) {
		super(currentDir, groupId, null, autoriseSnapshot);
		this.updatePublisher = updatePublisher;
		this.updatablePlugin = updatablePlugin;
		this.parameters = parameters;
	}

	@Override
	protected String getCurrentVersion(final File currentFile) {
		return updatablePlugin.getCurrentVersion(parameters);
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
	protected boolean performUpdate(final File current,
			final ArtifactVersion artifactVersion) {
		if (OSUtils.isWindows()) {
			return true;
		} else {
			LOG.warn("Plugin " + artifactVersion.getArtifactId()
					+ " should be updated to version"
					+ artifactVersion.getVersion());
			return false;
		}
	}

	@Override
	protected void updateFile(final String folderToUpdate, final File current, final File newVersion) {
		if (newVersion.exists()) {
			ZipUtils.unZipIt(newVersion, folderToUpdate);
			try {
				Files.delete(newVersion.toPath());
			} catch (final IOException e) {
				throw new TechnicalException(e);
			}
		}
	}

	@Override
	protected void onUpdate(final File current,
			final ArtifactVersion artifactNewVersion) {
		LOG.info("Update of file " + artifactNewVersion.getArtifactId()
				+ " version " + artifactNewVersion.getVersion());
		updatePublisher.addNews(new UpdatablePluginEvent(artifactNewVersion
				.getArtifactId(), artifactNewVersion.getVersion(),
				UpdatablePluginStateEnum.DOWNLOADING));
	}

	@Override
	protected void onUpdateDone(final File current,
			final ArtifactVersion artifactNewVersion) {
		LOG.info("Update of file " + artifactNewVersion.getArtifactId()
				+ " version " + artifactNewVersion.getVersion() + " done");
		updatePublisher.addNews(new UpdatablePluginEvent(artifactNewVersion
				.getArtifactId(), artifactNewVersion.getVersion(),
				UpdatablePluginStateEnum.DONE));

	}

	@Override
	protected void onUpdateError(final File current,
			final ArtifactVersion artifactNewVersion) {
		LOG.error("Error while updating file "
				+ artifactNewVersion.getArtifactId() + " version "
				+ artifactNewVersion.getVersion());
		updatePublisher.addNews(new UpdatablePluginEvent(artifactNewVersion
				.getArtifactId(), artifactNewVersion.getVersion(),
				UpdatablePluginStateEnum.ERROR));
	}

	@Override
	protected void onChecking(final String fileToUpdate) {
	}

	@Override
	protected boolean deleteFiles() {
		return false;
	}
}
