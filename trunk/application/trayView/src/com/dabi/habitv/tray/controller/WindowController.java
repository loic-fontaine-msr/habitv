package com.dabi.habitv.tray.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.tray.model.HabitTvViewManager;
import com.dabi.habitv.tray.view.HabiTvTrayView;

public class WindowController {

	/*
	 * DL
	 */
	@FXML
	private Tab downloadTab;

	@FXML
	private Button searchButton;

	@FXML
	private Button clearButton;

	@FXML
	private Button retryExportButton;

	@FXML
	private Button clearExportButton;

	@FXML
	private VBox downloadingBox;

	@FXML
	private Button downloadDirButton;

	@FXML
	private Button indexButton;

	@FXML
	private Button errorBUtton;

	/*
	 * TO DL
	 */

	@FXML
	private Tab toDownloadTab;
	
	@FXML
	private Button refreshCategoryButton;

	@FXML
	private Button cleanCategoryButton;

	@FXML
	private TreeView<CategoryDTO> toDLTree;

	/*
	 * CONFIG
	 */

	@FXML
	private Tab configTab;
	
	@FXML
	private TextField downloadOuput;

	@FXML
	private TextField nbrMaxAttempts;

	@FXML
	private TextField daemonCheckTimeSec;

	@FXML
	private CheckBox autoUpdate;

	public WindowController() {
	}

	public void init(final HabitTvViewManager manager, Stage primaryStage) throws IOException {
		downloadTab.setGraphic(new ImageView(new Image((ClassLoader
				.getSystemResource("dl.png").openStream()))));
		toDownloadTab.setGraphic(new ImageView(new Image((ClassLoader
				.getSystemResource("adl.png").openStream()))));
		configTab.setGraphic(new ImageView(new Image((ClassLoader
				.getSystemResource("config.png").openStream()))));		

		final ViewController controller = new ViewController(manager, primaryStage);
		final HabiTvTrayView view = new HabiTvTrayView(controller);
		manager.attach(view);
		manager.attach(controller);

		DownloadController downloadController = new DownloadController(
				searchButton, clearButton, retryExportButton,
				clearExportButton, downloadingBox, downloadDirButton,
				indexButton, errorBUtton);
		manager.attachRetreiveSubscriber(downloadController);
		downloadController.init(controller, manager);

		new ToDownloadController(refreshCategoryButton, cleanCategoryButton,
				toDLTree).init(controller, manager);

		new ConfigController(downloadOuput, nbrMaxAttempts, daemonCheckTimeSec,
				autoUpdate).init(controller, manager);

		controller.startDownloadCheckDemon();
	}
}
