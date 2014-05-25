package com.dabi.habitv.tray;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import com.dabi.habitv.api.plugin.exception.TechnicalException;
import com.dabi.habitv.tray.PopinController.ButtonHandler;

public class Popin extends Application {

	private PopinController controller = new PopinController();

	public Popin() {
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Platform.setImplicitExit(false);
		primaryStage.getIcons().add(
				new Image(ClassLoader.getSystemResource("fixe.gif")
						.openStream()));		
		try {
			// primaryStage.setTitle("habiTv");
			FXMLLoader fxmlLoader = new FXMLLoader(
					HabitvViewMain.class.getResource("popin.fxml"));
			fxmlLoader.setController(controller);

			GridPane root = (GridPane) fxmlLoader.load();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			controller.init(primaryStage);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public void show(final String title, final String message) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				try {
					start(new Stage());
					controller.show(title, message);
				} catch (Exception e) {
					throw new TechnicalException(e);
				}
			}
		});
	}

	public Popin show(final String title, final Node node) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				try {
					start(new Stage());
					controller.show(title, node);
				} catch (Exception e) {
					throw new TechnicalException(e);
				}
			}
		});
		return this;
	}

	public Popin setOkButtonHandler(ButtonHandler okButtonHandler) {
		controller.setOkButtonHandler(okButtonHandler);
		return this;
	}

	public Popin setCancelButtonHandler(ButtonHandler cancelButtonHandler) {
		controller.setCancelButtonHandler(cancelButtonHandler);
		return this;
	}

}
