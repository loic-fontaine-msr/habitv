package com.dabi.habitv.tray.view.fx;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException {
		primaryStage.getIcons().add(
				new Image(ClassLoader.getSystemResource("fixe.gif").openStream()));
		primaryStage.setTitle("habiTv");
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
				"habitv.fxml"));
		WindowController controller = new WindowController();
		fxmlLoader.setController(controller);

		try {
			VBox root = (VBox) fxmlLoader.load();
			Scene scene = new Scene(root, 400, 400);
			primaryStage.setScene(scene);
			primaryStage.show();
			controller.init();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
