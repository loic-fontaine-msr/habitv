package com.dabi.habitv.tray.view.fx;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
					"habitv.fxml"));
			//fxmlLoader.setRoot(this);
			DownloadPanelController controller = new DownloadPanelController();
			fxmlLoader.setController(controller);

			try {
				VBox root = (VBox) fxmlLoader.load();
//				URL url = getClass().getResource("habitv.fxml");
				Scene scene = new Scene(root, 400, 400);
				// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setScene(scene);
				primaryStage.show();
				controller.init();
			} catch (IOException exception) {
				throw new RuntimeException(exception);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
