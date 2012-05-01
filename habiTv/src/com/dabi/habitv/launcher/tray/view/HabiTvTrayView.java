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
import java.util.Map;
import java.util.Map.Entry;

import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.launcher.tray.EpisodeChangedEvent;
import com.dabi.habitv.launcher.tray.HabiTvListener;
import com.dabi.habitv.launcher.tray.ProcessChangedEvent;
import com.dabi.habitv.launcher.tray.controller.TrayController;
import com.dabi.habitv.launcher.tray.model.EpisodeStateEnum;

public class HabiTvTrayView implements HabiTvListener {

	private final TrayController controller;

	private final TrayIcon trayIcon;
	
	private MenuItem startItem;
	
	private final Image fixImage;
	
	private final Image animatedImage;

	public HabiTvTrayView(final TrayController controller) {
		this.controller = controller;
		fixImage = Toolkit.getDefaultToolkit().getImage("src/fixe.gif");
		animatedImage = Toolkit.getDefaultToolkit().getImage("src/anim.gif");
		trayIcon = new TrayIcon(fixImage, "habiTv");
		try {
			init();
		} catch (AWTException e) {
			throw new TechnicalException(e);
		}
	}

	public void init() throws AWTException {

		if (SystemTray.isSupported()) {

			SystemTray tray = SystemTray.getSystemTray();
			PopupMenu popupmenu = new PopupMenu();

			startItem = new MenuItem("Start");
			ActionListener actionListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					startItem.setEnabled(false);
					controller.start();
					startItem.setEnabled(true);
				}
			};
			startItem.addActionListener(actionListener);
			popupmenu.add(startItem);

			MenuItem item = new MenuItem("Quitter");
			actionListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					controller.stop();
				}
			};
			item.addActionListener(actionListener);
			popupmenu.add(item);

			trayIcon.setPopupMenu(popupmenu);

			MouseListener mouseListener = new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {
					// nothing
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// nothing
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// nothing
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// nothing
				}

				@Override
				public void mouseClicked(MouseEvent e) {
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

	private String progressionToText(Map<EpisodeDTO, Map<EpisodeStateEnum, String>> episodeName2ActionProgress) {
		StringBuilder str = null;
		for (Entry<EpisodeDTO, Map<EpisodeStateEnum, String>> episodeEntry : episodeName2ActionProgress.entrySet()) {
			if (str == null) {
				str = new StringBuilder();
			} else {
				str.append("\n");
			}
			str.append(episodeEntry.getKey().getCategory() + " " + episodeEntry.getKey().getName() + " ");
			for (Entry<EpisodeStateEnum, String> episodeActions : episodeEntry.getValue().entrySet()) {
				str.append(episodeActions.getKey().name() + " "
						+ ((episodeActions.getValue() != null && episodeActions.getValue().length() > 0) ? (episodeActions.getValue() + "%") : ""));
			}
		}
		return str.toString();
	}

	@Override
	public void processChanged(ProcessChangedEvent event) {
		switch (event.getState()) {
		case BUILD_INDEX:
			trayIcon.displayMessage("Building Index", "Build Index for " + event.getInfo(), TrayIcon.MessageType.INFO);
			break;
		case CHECKING_EPISODES:
			startItem.setEnabled(false);
			trayIcon.displayMessage("Checking", "Checking for episodes", TrayIcon.MessageType.INFO);
			trayIcon.setImage(animatedImage);
			break;
		case DONE:
			// trayIcon.displayMessage("Done", "Checking & Dowloading done",
			// TrayIcon.MessageType.INFO);
			startItem.setEnabled(true);
			trayIcon.setImage(fixImage);
			break;
		case ERROR:
			trayIcon.displayMessage("Error", "En error has occured", TrayIcon.MessageType.ERROR);
			break;
		case GETTING_CATEGORIES:
			trayIcon.displayMessage("Grabbing categories", "Grabbing categories", TrayIcon.MessageType.INFO);
			break;
		default:
			break;
		}
	}

	@Override
	public void episodeChanged(EpisodeChangedEvent event) {
		EpisodeStateEnum episodeState = event.getState();
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
