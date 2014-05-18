package com.dabi.habitv.tray.view.fx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.tray.controller.ViewController;
import com.dabi.habitv.tray.model.HabitTvViewManager;
import com.dabi.habitv.tray.view.HabiTvTrayView;

public class WindowController {

	/*
	 * DL
	 */

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
	private Button refreshCategoryButton;

	@FXML
	private Button cleanCategoryButton;

	@FXML
	private TreeView<CategoryDTO> toDLTree;

	/*
	 * CONFIG
	 */

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

	public void init(final HabitTvViewManager manager) {
		final ViewController controller = new ViewController(manager);
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