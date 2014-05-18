package com.dabi.habitv.tray.view;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.dabi.habitv.tray.controller.ViewController;

public final class TrayMenu extends PopupMenu {

	private static final long serialVersionUID = -1363830194310131496L;

	private final ViewController controller;

	public TrayMenu(final ViewController controller) {
		super();
		this.controller = controller;
		init();
	}

	private void init() {
		
		MenuItem startItem = new MenuItem(Messages.getString("TrayMenu.1")); //$NON-NLS-1$
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				controller.openMainView();
			}
		};
		startItem.addActionListener(actionListener);
		this.add(startItem);		
		
		startItem = new MenuItem(Messages.getString("TrayMenu.0")); //$NON-NLS-1$
		actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				controller.start();
			}
		};
		startItem.addActionListener(actionListener);
		this.add(startItem);

//		final Menu exportSection = buildExportMenuSection();
//		this.add(exportSection);

		final Menu folderSection = buildFolderMenuSection();
		this.add(folderSection);

//		final Menu configSection = buildConfigMenuSection();
//		this.add(configSection);

		MenuItem item;

		item = new MenuItem(Messages.getString("TrayMenu.2")); //$NON-NLS-1$
		actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				controller.stop();
			}
		};
		item.addActionListener(actionListener);
		this.add(item);

	}

	private Menu buildExportMenuSection() {
		final Menu section = new Menu(Messages.getString("TrayMenu.3")); //$NON-NLS-1$

		final MenuItem redoExport = new MenuItem(Messages.getString("TrayMenu.4")); //$NON-NLS-1$
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				redoExport.setEnabled(false);
				controller.reDoExport();
				redoExport.setEnabled(true);
			}
		};
		redoExport.addActionListener(actionListener);
		section.add(redoExport);

		final MenuItem clearExport = new MenuItem(Messages.getString("TrayMenu.5")); //$NON-NLS-1$
		actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				controller.clearExport();
			}
		};
		clearExport.addActionListener(actionListener);
		section.add(clearExport);
		return section;
	}

	private Menu buildConfigMenuSection() {
		final Menu section = new Menu(Messages.getString("TrayMenu.6")); //$NON-NLS-1$

		MenuItem item = new MenuItem(Messages.getString("TrayMenu.7")); //$NON-NLS-1$
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				controller.openConfig();
			}
		};
		item.addActionListener(actionListener);
		section.add(item);

		item = new MenuItem(Messages.getString("TrayMenu.8")); //$NON-NLS-1$
		actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				controller.openGrabConfig();
			}
		};
		item.addActionListener(actionListener);
		section.add(item);

		item = new MenuItem(Messages.getString("TrayMenu.9")); //$NON-NLS-1$
		actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				controller.updateGrabConfig();
			}
		};
		item.addActionListener(actionListener);
		section.add(item);

		item = new MenuItem(Messages.getString("TrayMenu.13")); //$NON-NLS-1$
		actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				controller.update();
			}

		};
		item.addActionListener(actionListener);
		section.add(item);

		return section;
	}

	private Menu buildFolderMenuSection() {
		final Menu section = new Menu(Messages.getString("TrayMenu.10")); //$NON-NLS-1$

		MenuItem item = new MenuItem(Messages.getString("TrayMenu.11")); //$NON-NLS-1$
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				controller.openIndexDir();
			}
		};
		item.addActionListener(actionListener);
		section.add(item);

		item = new MenuItem(Messages.getString("TrayMenu.12")); //$NON-NLS-1$
		actionListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				controller.openDownloadDir();
			}
		};
		item.addActionListener(actionListener);
		section.add(item);

		return section;
	}

}
