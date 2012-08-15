package com.dabi.habitv.tray.view;

import java.awt.Desktop;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import com.dabi.habitv.config.entities.Config;
import com.dabi.habitv.core.config.ConfigAccess;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.tray.controller.TrayController;

public final class TrayMenu extends PopupMenu {

	private static final long serialVersionUID = -1363830194310131496L;

	private final TrayController controller;

	private MenuItem startItem;

	public TrayMenu(final TrayController controller) {
		super();
		this.controller = controller;
		init();
	}

	private void init() {
		startItem = new MenuItem("Check");
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				startItem.setEnabled(false);
				controller.start();
				startItem.setEnabled(true);
			}
		};
		startItem.addActionListener(actionListener);
		this.add(startItem);

		final Menu folderSection = buildFolderMenuSection();
		this.add(folderSection);

		final Menu configSection = buildConfigMenuSection();
		this.add(configSection);

		MenuItem item = new MenuItem("Clear");
		actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				controller.clear();
			}
		};
		item.addActionListener(actionListener);
		this.add(item);

		item = new MenuItem("Quitter");
		actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				controller.stop();
			}
		};
		item.addActionListener(actionListener);
		this.add(item);

	}

	private Menu buildConfigMenuSection() {
		final Menu section = new Menu("Config");

		MenuItem item = new MenuItem("System");
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				openConfig();
			}
		};
		item.addActionListener(actionListener);
		section.add(item);

		item = new MenuItem("Grab config");
		actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				openGrabConfig();
			}
		};
		item.addActionListener(actionListener);
		section.add(item);

		return section;
	}

	private Menu buildFolderMenuSection() {
		final Menu section = new Menu("Folders");

		MenuItem item = new MenuItem("Index Folder");
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				openIndexFolder();
			}
		};
		item.addActionListener(actionListener);
		section.add(item);

		item = new MenuItem("Download Folder");
		actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				openDownloaderFolder();
			}
		};
		item.addActionListener(actionListener);
		section.add(item);

		return section;
	}

	public void openIndexFolder() {
		final Config config = controller.getModel().getConfig();
		open(config.getIndexDir());
	}

	static void open(final String toOpen) {
		try {
			final String canonicalPath = new File(toOpen).getCanonicalPath();
			if (toOpen == null) {
				throw new NullPointerException();
			}
			if (!Desktop.isDesktopSupported()) {
				return;
			}
			final Desktop desktop = Desktop.getDesktop();

			desktop.open(new File(canonicalPath));
		} catch (final IOException e) {
			throw new TechnicalException(e);
		}
	}

	public void openDownloaderFolder() {
		final Config config = controller.getModel().getConfig();
		open(config.getDownloadOuput().substring(0, config.getDownloadOuput().indexOf("#")));
	}

	public void openConfig() {
		open(ConfigAccess.CONF_FILE);
	}

	public void openGrabConfig() {
		open(ConfigAccess.GRAB_CONF_FILE);
	}
}
