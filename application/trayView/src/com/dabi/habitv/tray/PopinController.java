package com.dabi.habitv.tray;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PopinController {

	@FXML
	private Label messageLabel;

	@FXML
	private Pane contentPane;

	@FXML
	private Label detailsLabel;

	@FXML
	private Button cancelButton;

	@FXML
	private Button okButton;

	public PopinController() {
	}

	public interface ButtonHandler {
		void onAction();
	}

	private ButtonHandler okButtonHandler;

	private ButtonHandler cancelButtonHandler;

	public void init(final Stage primaryStage) {
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				primaryStage.close();
				if (cancelButtonHandler != null) {
					cancelButtonHandler.onAction();
				}
			}
		});
		okButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				primaryStage.close();
				if (okButtonHandler != null) {
					okButtonHandler.onAction();
				}
			}
		});
	}

	public void show(String title, String message) {
		this.messageLabel.setText(title);
		this.detailsLabel.setText(message);
		cancelButton.setVisible(false);
	}

	public void show(String title, Node node) {
		this.messageLabel.setText(title);
		contentPane.getChildren().clear();
		contentPane.getChildren().add(node);
	}

	public void setOkButtonHandler(ButtonHandler okButtonHandler) {
		this.okButtonHandler = okButtonHandler;
	}

	public void setCancelButtonHandler(ButtonHandler cancelButtonHandler) {
		this.cancelButtonHandler = cancelButtonHandler;
	}

}
