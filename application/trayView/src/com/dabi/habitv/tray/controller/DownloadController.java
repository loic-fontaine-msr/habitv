package com.dabi.habitv.tray.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.api.plugin.pub.UpdatablePluginEvent;
import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchCategoryEvent;
import com.dabi.habitv.core.event.SearchEvent;
import com.dabi.habitv.core.event.UpdatePluginEvent;
import com.dabi.habitv.tray.model.ActionProgress;
import com.dabi.habitv.tray.subscriber.CoreSubscriber;
import com.dabi.habitv.tray.view.DownloadBox;

public class DownloadController extends BaseController implements
		CoreSubscriber {

	private Button searchButton;

	private Button clearButton;

	private Button retryExportButton;

	private Button clearExportButton;

	private VBox downloadingBox;

	private Button downloadDirButton;

	private Button indexButton;

	private Button errorBUtton;

	private Map<String, DownloadBox> epId2DLBox = new HashMap<>();

	private ProgressIndicator mainProgress;

	public DownloadController(ProgressIndicator mainProgress,
			Button searchButton, Button clearButton, Button retryExportButton,
			Button clearExportButton, VBox downloadingBox,
			Button downloadDirButton, Button indexButton, Button errorBUtton) {
		super();
		this.mainProgress = mainProgress;
		this.searchButton = searchButton;
		this.clearButton = clearButton;
		this.retryExportButton = retryExportButton;
		this.clearExportButton = clearExportButton;
		this.downloadingBox = downloadingBox;
		this.downloadDirButton = downloadDirButton;
		this.indexButton = indexButton;
		this.errorBUtton = errorBUtton;
		runCheckProgressThread();
	}

	public void init() {
		downloadingBox.getChildren().clear();
		addTooltip();
		updateDownloadPanel();
		addDownloadActions();
		addExportActions();
		addFilesAndFoldersActions();
	}

	private void runCheckProgressThread() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								updateDownloadPanel();
							}
						});
					} catch (final InterruptedException e) {
						throw new TechnicalException(e);
					}
				}
			}
		}).start();
	}

	private void addTooltip() {
		this.searchButton.setTooltip(new Tooltip(
				"Rechercher des épisodes à télécharger."));
		this.clearButton.setTooltip(new Tooltip(
				"Vider la liste des téléchargements terminés."));
		this.retryExportButton.setTooltip(new Tooltip(
				"Retenter les exports précédemment échoués."));
		this.clearExportButton.setTooltip(new Tooltip(
				"Vider les exports précédemment échoués."));
		this.downloadDirButton.setTooltip(new Tooltip(
				"Ouvrir le répertoire de téléchargement."));
		this.indexButton
				.setTooltip(new Tooltip(
						"Ouvrir le répertoire contenant les index des épisodes marqués comme déjà téléchargés."));
		this.errorBUtton
				.setTooltip(new Tooltip(
						"Ouvrir le fichier contenant les téléchargements notés en erreur qui ne seront pas retentés."));
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
				new Thread(new Runnable() {

					@Override
					public void run() {
						getController().start();
					}

				}).run();
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
			final DownloadBox newDownloadBox = new DownloadBox(getController(),
					actionProgress);
			downloadBox = newDownloadBox;
			newDownloadBox.setPadding(new Insets(2));
			downloadingBox.getChildren().add(downloadBox);
			epId2DLBox.put(actionProgress.getEpisode().getId(), downloadBox);

			downloadBox.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					newDownloadBox.openMenu(event);
					newDownloadBox.select();
					for (DownloadBox otherDownloadBox : epId2DLBox.values()) {
						if (!otherDownloadBox.equals(newDownloadBox)) {
							otherDownloadBox.unSelect();
						}
					}
				}

			});
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

	@Override
	public void update(UpdatePluginEvent event) {

	}

	@Override
	public void update(UpdatablePluginEvent event) {

	}

	private int searchCount;

	private int searchSize;

	@Override
	public void update(final SearchEvent event) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				switch (event.getState()) {
				case STARTING:
					searchButton.setDisable(true);
					searchCount = 0;
					searchSize = Integer.parseInt(event.getInfo());
					mainProgress.setProgress((double) searchCount / searchSize);
					break;
				case DONE:
					searchCount++;
					mainProgress.setProgress((double) searchCount / searchSize);
					break;
				case ALL_SEARCH_DONE:
					searchButton.setDisable(false);
					mainProgress.setProgress(1);
					break;
				default:
					break;
				}
			}

		});
	}

	@Override
	public void update(SearchCategoryEvent event) {

	}
}
