package com.dabi.habitv.core.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.api.plugin.pub.Publisher;
import com.dabi.habitv.core.event.UpdatePluginEvent;
import com.dabi.habitv.core.event.UpdatePluginStateEnum;
import com.dabi.habitv.framework.plugin.utils.update.FindArtifactUtils.ArtifactVersion;
import com.dabi.habitv.framework.plugin.utils.update.Updater;

public class JarUpdater extends Updater {

	private static final String JAR = "jar";

	private static final Logger LOG = Logger.getLogger(JarUpdater.class);

	private final Publisher<UpdatePluginEvent> updatePublisher;

	public JarUpdater(final String currentDir, final String groupId, final String coreVersion, final boolean autoriseSnapshot,
			final Publisher<UpdatePluginEvent> updatePublisher) {
		super(currentDir, groupId, coreVersion, autoriseSnapshot);
		this.updatePublisher = updatePublisher;
	}

	@Override
	protected String getCurrentVersion(final File currentFile) {
		try (JarInputStream jarStream = new JarInputStream(new FileInputStream(currentFile));) {
			final Manifest mf = jarStream.getManifest();
			return (String) mf.getMainAttributes().get(Attributes.Name.IMPLEMENTATION_VERSION);
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
	}

	@Override
	protected String getLocalExtension() {
		return JAR;
	}

	@Override
	protected String getServerExtension() {
		return JAR;
	}

	@Override
	protected boolean performUpdate(final File current, final ArtifactVersion artifactVersion) {
		return true;
	}

	@Override
	protected void onUpdate(final File current, final ArtifactVersion artifactNewVersion) {
		LOG.info("Update of plugin " + artifactNewVersion.getArtifactId() + " version " + artifactNewVersion.getVersion());
		updatePublisher.addNews(new UpdatePluginEvent(artifactNewVersion.getArtifactId(), artifactNewVersion.getVersion(), UpdatePluginStateEnum.DOWNLOADING));
	}

	@Override
	protected void onUpdateDone(final File current, final ArtifactVersion artifactNewVersion) {
		LOG.info("Update of plugin " + artifactNewVersion.getArtifactId() + " version " + artifactNewVersion.getVersion() + " done");
		updatePublisher.addNews(new UpdatePluginEvent(artifactNewVersion.getArtifactId(), artifactNewVersion.getVersion(), UpdatePluginStateEnum.DONE));

	}

	@Override
	protected void onUpdateError(final File current, final ArtifactVersion artifactNewVersion) {
		LOG.error("Error while updating plugin " + artifactNewVersion.getArtifactId() + " version " + artifactNewVersion.getVersion());
		updatePublisher.addNews(new UpdatePluginEvent(artifactNewVersion.getArtifactId(), artifactNewVersion.getVersion(), UpdatePluginStateEnum.ERROR));
	}

	@Override
	protected void onChecking(final String fileToUpdate) {
		updatePublisher.addNews(new UpdatePluginEvent(fileToUpdate, null, UpdatePluginStateEnum.CHECKING));
	}

	@Override
	protected boolean deleteFiles() {
		return true;
	}

}
