package com.dabi.habitv.tray.view.fx;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.core.event.EpisodeStateEnum;
import com.dabi.habitv.tray.model.ActionProgress;

public class DownloadPanelController implements Initializable {

	@FXML
	private VBox downloadingBox;

	@FXML
	private TreeView<CategoryDTO> toDLTree;

	@FXML
	private TextField downloadOuput;

	@FXML
	private TextField nbrMaxAttempts;

	@FXML
	private TextField daemonCheckTimeSec;

	@FXML
	private CheckBox autoUpdate;

	public DownloadPanelController() {
	}

	private void addDownload(ActionProgress actionProgress) {
		downloadingBox.getChildren().add(buildDownloadBox(actionProgress));
	}

	private Node buildDownloadBox(ActionProgress actionProgress) {
		EpisodeDTO episode = actionProgress.getEpisode();
		CategoryDTO category = episode.getCategory();
		BorderPane downloadBox = new BorderPane();
		downloadBox
				.setLeft(new HBox(10, new Label(category.getChannel()),
						new Label(category.getName()), new Label(episode
								.getFullName())));
		downloadBox.setRight(getStateWidget(actionProgress));
		downloadBox.setPadding(new Insets(10, 10, 0, 10));
		return downloadBox;
	}

	private Node getStateWidget(ActionProgress actionProgress) {
		Node widget;
		switch (actionProgress.getState()) {
		case DOWNLOADING:
			widget = new ProgressBar(Double.parseDouble(actionProgress
					.getProgress()));
			break;
		default:
			widget = new Label(actionProgress.getState().name() + " : "
					+ actionProgress.getInfo());
			break;
		}
		return widget;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	public void init() {
		downloadingBox.getChildren().clear();
		addDownload(new ActionProgress(EpisodeStateEnum.TO_DOWNLOAD, "0.7",
				"info", new EpisodeDTO(new CategoryDTO("chan", "name",
						"identifier", "extension"), "name", "id")));
		addDownload(new ActionProgress(EpisodeStateEnum.DOWNLOADING, "0.2",
				"info", new EpisodeDTO(new CategoryDTO("channel tf1", "name",
						"identifier", "extension"), "name", "id")));
		addDownload(new ActionProgress(EpisodeStateEnum.DOWNLOADING, "0.7",
				"info", new EpisodeDTO(new CategoryDTO("channel", "name",
						"identifier", "extension"), "name", "id")));
		addDownload(new ActionProgress(EpisodeStateEnum.READY, "70", "info",
				new EpisodeDTO(new CategoryDTO("channel", "name", "identifier",
						"extension"), "name", "id")));

		TreeItem<CategoryDTO> root = buildTreeItem();
		toDLTree.setRoot(root);
		toDLTree.setCellFactory(CheckBoxTreeCell.<CategoryDTO>forTreeView());

		TreeItem<CategoryDTO> buildTreeItem = buildTreeItem();
		buildTreeItem.getChildren().add(buildTreeItem());
		buildTreeItem.getChildren().add(buildTreeItem());
		buildTreeItem.getChildren().add(buildTreeItem());

		toDLTree.getRoot().getChildren().add(buildTreeItem);

		nbrMaxAttempts.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				System.out.println(((TextField) event.getSource()).getText());
			}
		});

	}

	private TreeItem<CategoryDTO> buildTreeItem() {
		return new TreeItem<CategoryDTO>(new CategoryDTO("channel", "name",
				"identifier", "extension"));
	}
}
