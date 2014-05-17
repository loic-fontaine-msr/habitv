package com.dabi.habitv.tray.view.fx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import com.dabi.habitv.api.plugin.pub.Subscriber;
import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.tray.model.ActionProgress;

public class DownloadController extends BaseController implements
		Subscriber<RetreiveEvent> {

	private Button searchButton;

	private Button clearButton;

	private Button retryExportButton;

	private Button clearExportButton;

	private VBox downloadingBox;

	private Button downloadDirButton;

	private Button indexButton;

	private Button errorBUtton;

	private Map<String, DownloadBox> epId2DLBox = new HashMap<>();

	public DownloadController(Button searchButton, Button clearButton,
			Button retryExportButton, Button clearExportButton,
			VBox downloadingBox, Button downloadDirButton, Button indexButton,
			Button errorBUtton) {
		super();
		this.searchButton = searchButton;
		this.clearButton = clearButton;
		this.retryExportButton = retryExportButton;
		this.clearExportButton = clearExportButton;
		this.downloadingBox = downloadingBox;
		this.downloadDirButton = downloadDirButton;
		this.indexButton = indexButton;
		this.errorBUtton = errorBUtton;
	}

	public void init() {
		downloadingBox.getChildren().clear();
		updateDownloadPanel();
		addDownloadActions();
		addExportActions();
		addFilesAndFoldersActions();
	}

	private void addFilesAndFoldersActions() {
		downloadDirButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				getController().openDownloadDir();
			}
		});

		indexButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				getController().openIndexDir();
			}
		});

		errorBUtton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				getController().openErrorFile();
			}
		});
	}

	private void addExportActions() {
		retryExportButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				getController().reDoExport();
			}
		});
		retryExportButton.setVisible(false);

		clearExportButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				getController().clearExport();
			}
		});
		clearExportButton.setVisible(false);
	}

	private void addDownloadActions() {
		searchButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				getController().start();
			}
		});

		clearButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				getController().clear();
				updateDownloadPanel();
			}
		});
	}

	private void updateDownloadPanel() {
		Set<String> existingEpId = new HashSet<>();
		for (ActionProgress actionProgress : getModel().getProgressionModel()
				.getEpisodeName2ActionProgress()) {
			buildOrUpdateDownloadBox(actionProgress);
			existingEpId.add(actionProgress.getEpisode().getId());
		}

		Iterator<Entry<String, DownloadBox>> it = epId2DLBox.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<String, DownloadBox> downloadBox = it.next();
			if (!existingEpId.contains(downloadBox.getKey())) {
				it.remove();
				downloadingBox.getChildren().remove(downloadBox.getValue());
			}
		}
	}

	private void buildOrUpdateDownloadBox(ActionProgress actionProgress) {
		DownloadBox downloadBox = epId2DLBox.get(actionProgress.getEpisode()
				.getId());
		if (downloadBox == null) {
			downloadBox = new DownloadBox(actionProgress);
			downloadingBox.getChildren().add(downloadBox);
			epId2DLBox.put(actionProgress.getEpisode().getId(), downloadBox);
		} else {
			downloadBox.update();
		}
	}

	@Override
	public void update(final RetreiveEvent event) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				updateDownloadPanel();
			}

		});
	}
}
