package com.dabi.habitv.launcher.tray.view;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.launcher.tray.EpisodeChangedEvent;
import com.dabi.habitv.launcher.tray.HabiTvListener;
import com.dabi.habitv.launcher.tray.ProcessChangedEvent;
import com.dabi.habitv.launcher.tray.controller.TrayController;
import com.dabi.habitv.launcher.tray.model.ActionProgress;
import com.dabi.habitv.launcher.tray.model.EpisodeStateEnum;

public final class HabiTvTrayView implements HabiTvListener {

	private final TrayController controller;

	private final TrayIcon trayIcon;

	private MenuItem startItem;

	private final Image fixImage;

	private final Image animatedImage;

	public HabiTvTrayView(final TrayController controller) {
		this.controller = controller;
		fixImage = getImage("fixe.gif");
		animatedImage = getImage("anim.gif");
		trayIcon = new TrayIcon(fixImage, "habiTv");
		try {
			init();
		} catch (AWTException e) {
			throw new TechnicalException(e);
		}
	}

	private Image getImage(String image) {
		return Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(image));
	}

	public void init() throws AWTException {

		if (SystemTray.isSupported()) {

			final SystemTray tray = SystemTray.getSystemTray();
			final PopupMenu popupmenu = new PopupMenu();

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
			popupmenu.add(startItem);

			final MenuItem item = new MenuItem("Quitter");
			actionListener = new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent actionEvent) {
					controller.stop();
				}
			};
			item.addActionListener(actionListener);
			popupmenu.add(item);

			trayIcon.setPopupMenu(popupmenu);

			final MouseListener mouseListener = new MouseListener() {

				@Override
				public void mouseReleased(final MouseEvent mouseEvent) {
					// nothing
				}

				@Override
				public void mousePressed(final MouseEvent mouseEvent) {
					// nothing
				}

				@Override
				public void mouseExited(final MouseEvent mouseEvent) {
					// nothing
				}

				@Override
				public void mouseEntered(final MouseEvent mouseEvent) {
					// nothing
				}

				@Override
				public void mouseClicked(final MouseEvent mouseEvent) {
					if (!controller.getModel().getProgressionModel().getEpisodeName2ActionProgress().isEmpty()) {
						trayIcon.displayMessage("Processing", progressionToText(controller.getModel().getProgressionModel().getEpisodeName2ActionProgress()),
								TrayIcon.MessageType.INFO);
					}
				}
			};
			trayIcon.addMouseListener(mouseListener);
			tray.add(trayIcon);
		}
	}

	private String progressionToText(final Collection<ActionProgress> episodeName2ActionProgress) {
		StringBuilder str = null;
		for (ActionProgress actionProgress : episodeName2ActionProgress) {
			if (str == null) {
				str = new StringBuilder();
			} else {
				str.append("\n");
			}
			str.append(actionProgress.getEpisode().getCategory() + " " + actionProgress.getEpisode().getName() + " ");
			String progression = actionProgress.getProgress();
			if (progression != null && progression.length() > 0) {
				progression = progression + "%";
			}
			str.append(actionProgress.getState().name() + " " + actionProgress.getInfo() + " " + progression);
		}
		return str.toString();
	}

	@Override
	public void processChanged(final ProcessChangedEvent event) {
		switch (event.getState()) {
		case BUILD_INDEX:
			trayIcon.displayMessage("Building Index", "Build Index for " + event.getInfo(), TrayIcon.MessageType.INFO);
			break;
		case CHECKING_EPISODES:
			// startItem.setEnabled(false);
			trayIcon.displayMessage("Checking", "Checking for episodes", TrayIcon.MessageType.INFO);
			trayIcon.setImage(animatedImage);
			break;
		case DONE:
			// startItem.setEnabled(true);
			trayIcon.setImage(fixImage);
			break;
		case ERROR:
			trayIcon.displayMessage("Error", "En error has occured", TrayIcon.MessageType.ERROR);
			break;
		case BUILDING_CATEGORIES:
			trayIcon.displayMessage("Grabbing categories", "Grabbing categories for " + event.getInfo(), TrayIcon.MessageType.INFO);
			trayIcon.setImage(animatedImage);
			break;
		case CATEGORIES_BUILD:
			trayIcon.displayMessage("Grabbing categories", "Categories built in " + event.getInfo(), TrayIcon.MessageType.INFO);
			trayIcon.setImage(fixImage);
			break;
		default:
			break;
		}
	}

	@Override
	public void episodeChanged(final EpisodeChangedEvent event) {
		final EpisodeStateEnum episodeState = event.getState();
		switch (episodeState) {
		case TO_DOWNLOAD:
			trayIcon.displayMessage("New Download", "Episode to download : " + event.getEpisode().getCategory() + " " + event.getEpisode().getName(),
					TrayIcon.MessageType.INFO);
			break;
		case DOWNLOAD_FAILED:
			trayIcon.displayMessage("Warning", "Episode failed to download : " + event.getEpisode().getCategory() + " " + event.getEpisode().getName(),
					TrayIcon.MessageType.WARNING);
			break;
		case EXPORT_FAILED:
			trayIcon.displayMessage("Warning", "Export failed to download : " + event.getEpisode().getCategory() + " " + event.getEpisode().getName(),
					TrayIcon.MessageType.WARNING);
			break;
		case READY:
			trayIcon.displayMessage("Episode Ready", "The episode is ready : " + event.getEpisode().getCategory() + " " + event.getEpisode().getName(),
					TrayIcon.MessageType.INFO);
			break;
		default:
			break;
		}
	}
}
