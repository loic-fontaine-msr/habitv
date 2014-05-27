package com.dabi.habitv.tray.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.tray.controller.ViewController;
import com.dabi.habitv.tray.model.ActionProgress;
import com.dabi.habitv.tray.utils.LabelUtils;

public class DownloadBox extends BorderPane {

	private final ActionProgress actionProgress;
	private Pane statePanel;

	private ContextMenu contextMenu = new ContextMenu();
	private ViewController controller;
	private Node currentLabelWidget;
	private Label categoryLabel;
	private Label episodeLabel;

	public DownloadBox(ViewController viewController,
			ActionProgress actionProgress) {
		super();
		this.controller = viewController;
		this.actionProgress = actionProgress;

		EpisodeDTO episode = actionProgress.getEpisode();
		CategoryDTO category = episode.getCategory();

		categoryLabel = new Label(category.getPlugin().toUpperCase() + " - "
				+ category.getName());
		episodeLabel = new Label(episode.getName());
		HBox hBox = new HBox();
		hBox.setSpacing(10);
		hBox.getChildren().add(categoryLabel);
		hBox.getChildren().add(episodeLabel);
		
		setLeft(hBox);
		statePanel = new Pane();
		statePanel.getChildren().add(getStateWidget(null));
		setRight(statePanel);
		setPadding(new Insets(10, 10, 0, 10));
	}

	public void openMenu(MouseEvent event) {
		buildMenuItem();
		contextMenu.show(DownloadBox.this, event.getScreenX(),
				event.getScreenY());
	}

	void buildMenuItem() {
		contextMenu.getItems().clear();
		if (actionProgress.getState().isInProgress()
				&& actionProgress.getProcessHolder() != null) {
			MenuItem menuItemStop = new MenuItem("Arrêter");
			menuItemStop.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					actionProgress.getProcessHolder().stop();
				}
			});
			contextMenu.getItems().add(menuItemStop);
			MenuItem menuItemStopAndSetAsDL = new MenuItem(
					"Arrêter et marquer comme téléchargé");
			menuItemStopAndSetAsDL.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					actionProgress.getProcessHolder().stop();
					controller.setDownloaded(actionProgress.getEpisode());
				}
			});
			contextMenu.getItems().add(menuItemStopAndSetAsDL);
		}
		MenuItem menuItemOpenIndex = new MenuItem("Ouvrir l'index");
		menuItemOpenIndex.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				controller.openIndex(actionProgress.getEpisode().getCategory());
			}
		});
		contextMenu.getItems().add(menuItemOpenIndex);

		if (actionProgress.getState().hasFailed()) {
			MenuItem menuItemReStart = new MenuItem("Relancer");
			menuItemReStart.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					controller.restart(actionProgress.getEpisode(),
							actionProgress.getState().isExport());
				}
			});
			contextMenu.getItems().add(menuItemReStart);
		}
	}

	public void update() {
		Node oldWidget = statePanel.getChildren().get(0);
		Node newWidget = getStateWidget(oldWidget);
		if (!oldWidget.equals(newWidget)) {
			statePanel.getChildren().clear();
			statePanel.getChildren().add(newWidget);
		}
	}

	private Node getStateWidget(Node oldWidget) {
		Node widget;
		switch (actionProgress.getState()) {
		case DOWNLOAD_STARTING:
			widget = getProgressBarWidget(oldWidget);
			currentLabelWidget = null;
			break;
		default:
			widget = getLabelWidget(oldWidget);
			currentLabelWidget = widget;
			break;
		}
		return widget;
	}

	private Node getProgressBarWidget(Node oldWidget) {
		Node widget;
		Double progressDouble;
		String progress = actionProgress.getProgress();
		progressDouble = getProgressDouble(progress);

		ProgressIndicatorBar progressBar;
		if (oldWidget == null || !(oldWidget instanceof ProgressIndicatorBar)) {
			progressBar = new ProgressIndicatorBar();
		} else {
			progressBar = (ProgressIndicatorBar) oldWidget;
		}
		progressBar.setProgress(progressDouble);
		// if (progress != null) {
		// progressBar.setTooltip(new Tooltip(progress + "%"));
		// }
		widget = progressBar;
		return widget;
	}

	private Double getProgressDouble(String progress) {
		Double progressDouble;
		try {
			if (progress == null) {
				progressDouble = 0.0;
			} else {
				progressDouble = Double.parseDouble(progress) / 100;
			}
		} catch (NumberFormatException e) {
			progressDouble = 0.0;
		}
		return progressDouble;
	}

	private Node getLabelWidget(Node oldWidget) {
		Node widget;
		Label label;
		if (oldWidget == null || !(oldWidget instanceof Label)) {
			label = new Label();
			label.setMaxWidth(50);
		} else {
			label = (Label) oldWidget;
		}
		widget = label;
		label.setText(LabelUtils.buildStateLabel(actionProgress));
		return widget;
	}

	@Override
	public int hashCode() {
		return actionProgress.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DownloadBox other = (DownloadBox) obj;
		if (actionProgress == null) {
			if (other.actionProgress != null)
				return false;
		} else {
			return actionProgress.getEpisode().equals(
					other.actionProgress.getEpisode());
		}
		return true;
	}

	public void select() {
		setStyle("-fx-fill: white;-fx-background-color: #0096C9;");
		categoryLabel.setStyle("-fx-text-fill: white;");
		episodeLabel.setStyle("-fx-text-fill: white;");
		if (currentLabelWidget != null) {
			currentLabelWidget.setStyle("-fx-text-fill: white;");
		}
	}

	public void unSelect() {
		setStyle("-fx-fill: white;-fx-background-color: white;");
		categoryLabel.setStyle("");
		episodeLabel.setStyle("");
		if (currentLabelWidget != null) {
			currentLabelWidget.setStyle("");
		}
	};

}
