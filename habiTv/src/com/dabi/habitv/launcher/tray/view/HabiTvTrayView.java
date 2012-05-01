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

import com.dabi.habitv.framework.plugin.utils.ProcessingThread;
import com.dabi.habitv.launcher.tray.EpisodeChangedEvent;
import com.dabi.habitv.launcher.tray.HabiTvListener;
import com.dabi.habitv.launcher.tray.ProcessChangedEvent;
import com.dabi.habitv.launcher.tray.controller.TrayController;
import com.dabi.habitv.launcher.tray.model.EpisodeStateEnum;

public class HabiTvTrayView implements HabiTvListener {

	private final TrayController controller;

	private final TrayIcon trayIcon;

	public HabiTvTrayView(final TrayController controller) {
		this.controller = controller;
		Image image = Toolkit.getDefaultToolkit().getImage("test/test.gif");
		trayIcon = new TrayIcon(image, "habiTv");
		try {
			init();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void init() throws AWTException {

		if (SystemTray.isSupported()) {

			SystemTray tray = SystemTray.getSystemTray();
			PopupMenu popupmenu = new PopupMenu();
			MenuItem item = new MenuItem("Quitter");
			ActionListener exitActionListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ProcessingThread.killAllProcessing();
					System.exit(0);
				}
			};
			item.addActionListener(exitActionListener);
			popupmenu.add(item);

			item = new MenuItem("Start");
			exitActionListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					controller.start();
				}
			};
			item.addActionListener(exitActionListener);
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

	private String progressionToText(Map<String, Map<EpisodeStateEnum, String>> episodeName2ActionProgress) {
		StringBuilder str = new StringBuilder();
		for (Entry<String, Map<EpisodeStateEnum, String>> episodeEntry : episodeName2ActionProgress.entrySet()) {
			str.append(episodeEntry.getKey() + " ");
			for (Entry<EpisodeStateEnum, String> episodeActions : episodeEntry.getValue().entrySet()) {
				str.append(episodeActions.getKey().name() + " " + episodeActions.getValue() + "%");
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
			trayIcon.displayMessage("Checking", "Checking for episodes", TrayIcon.MessageType.INFO);
			break;
		case DONE:
			trayIcon.displayMessage("Done", "Checking & Dowloading done", TrayIcon.MessageType.INFO);
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
		case DOWNLOAD_FAILED:
			trayIcon.displayMessage("Warning", "Episode failed to download : " + event.getEpisode().getName(), TrayIcon.MessageType.WARNING);
			break;
		case EXPORT_FAILED:
			trayIcon.displayMessage("Warning", "Export failed to download : " + event.getEpisode().getName(), TrayIcon.MessageType.WARNING);
			break;
		case READY:
			trayIcon.displayMessage("Episode Ready", "The episode is ready : " + event.getEpisode().getName(), TrayIcon.MessageType.INFO);
			break;
		default:
			break;
		}
	}
}
