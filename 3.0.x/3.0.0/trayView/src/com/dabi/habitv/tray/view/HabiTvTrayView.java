package com.dabi.habitv.tray.view;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchCategoryEvent;
import com.dabi.habitv.core.event.SearchEvent;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.tray.controller.TrayController;
import com.dabi.habitv.tray.model.ActionProgress;
import com.dabi.habitv.tray.subscriber.CoreSubscriber;

public final class HabiTvTrayView implements CoreSubscriber {

	private final TrayController controller;

	private final TrayIcon trayIcon;

	private final Image fixImage;

	private final Image animatedImage;

	private boolean retreiveInProgress = false;

	private boolean checkInProgress = false;

	public HabiTvTrayView(final TrayController controller) {
		this.controller = controller;
		fixImage = getImage("fixe.gif");
		animatedImage = getImage("anim.gif");
		trayIcon = new TrayIcon(fixImage, "habiTv");
		try {
			init();
		} catch (final AWTException e) {
			throw new TechnicalException(e);
		}
	}

	private Image getImage(final String image) {
		return Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(image));
	}

	public void init() throws AWTException {

		if (SystemTray.isSupported()) {

			final SystemTray tray = SystemTray.getSystemTray();
			trayIcon.setPopupMenu(new TrayMenu(controller));

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
		for (final ActionProgress actionProgress : episodeName2ActionProgress) {
			if (str == null) {
				str = new StringBuilder();
			} else {
				str.append("\n");
			}
			str.append(actionProgress.getEpisode().getCategory() + " " + actionProgress.getEpisode().getName() + " ");
			String progression = actionProgress.getProgress();
			if (progression != null && progression.length() > 0) {
				progression = progression + "%";
			} else {
				progression = "";
			}
			String info = actionProgress.getInfo();
			if (info == null) {
				info = "";
			}
			str.append(actionProgress.getState().name() + " " + info + " " + progression);
		}
		return str.toString();
	}

	@Override
	public void update(final SearchEvent event) {
		switch (event.getState()) {
		case ALL_RETREIVE_DONE:
			retreiveInProgress = false;
			changeAnimation();
			break;
		case ALL_SEARCH_DONE:
			checkInProgress = false;
			changeAnimation();
			break;
		case BUILD_INDEX:
			trayIcon.displayMessage("Building Index", "Build Index for " + event.getChannel() + " " + event.getCategory(), TrayIcon.MessageType.INFO);
			break;
		case CHECKING_EPISODES:
			// trayIcon.displayMessage("Checking", "Checking for episodes",
			// TrayIcon.MessageType.INFO);
			checkInProgress = true;
			changeAnimation();
			break;
		case DONE:

			break;
		case ERROR:
			trayIcon.displayMessage("Error", "En error has occured : " + event.getException().getMessage(), TrayIcon.MessageType.ERROR);
			break;
		case IDLE:

			break;
		default:
			break;
		}
	}

	private void changeAnimation() {
		if (retreiveInProgress || checkInProgress) {
			trayIcon.setImage(animatedImage);
		} else {
			trayIcon.setImage(fixImage);
		}
	}

	@Override
	public void update(final RetreiveEvent event) {
		switch (event.getState()) {
		case BUILD_INDEX:

			break;
		case DOWNLOAD_FAILED:
			trayIcon.displayMessage("Warning", "Episode failed to download : " + event.getEpisode().getCategory() + " " + event.getEpisode().getName(),
					TrayIcon.MessageType.WARNING);
			break;
		case DOWNLOADED:

			break;
		case DOWNLOADING:

			break;
		case EXPORT_FAILED:
			trayIcon.displayMessage("Warning", "Export failed : " + event.getEpisode().getCategory() + " " + event.getEpisode().getName() + " "
					+ event.getException().getMessage(), TrayIcon.MessageType.WARNING);
			break;
		case EXPORTING:

			break;
		case FAILED:
			trayIcon.displayMessage("Error", "En error has occured : " + event.getException().getMessage() + " on episode " + event.getEpisode(),
					TrayIcon.MessageType.WARNING);
			break;
		case READY:
			trayIcon.displayMessage("Episode Ready", "The episode is ready : " + event.getEpisode().getCategory() + " " + event.getEpisode().getName(),
					TrayIcon.MessageType.INFO);
			break;
		case TO_DOWNLOAD:
			retreiveInProgress = true;
			trayIcon.displayMessage("New Download", "Episode to download : " + event.getEpisode().getCategory() + " " + event.getEpisode().getName(),
					TrayIcon.MessageType.INFO);
			break;
		case TO_EXPORT:

			break;
		default:
			break;
		}
	}

	@Override
	public void update(final SearchCategoryEvent event) {
		switch (event.getState()) {
		case BUILDING_CATEGORIES:
			trayIcon.displayMessage("Grabbing categories", "Grabbing categories for " + event.getChannel(), TrayIcon.MessageType.INFO);
			trayIcon.setImage(animatedImage);
			break;
		case DONE:
			trayIcon.displayMessage("Grabbing categories", "Categories built in " + event.getInfo(), TrayIcon.MessageType.INFO);
			changeAnimation();
			break;
		case ERROR:

			break;
		case IDLE:

			break;
		default:
			break;
		}
	}
}
