package com.dabi.habitv.tray.controller.dl;

import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

class ProgressIndicatorBar extends StackPane {
	final private ProgressBar bar = new ProgressBar();
	final private Text text = new Text();

	final private static int DEFAULT_LABEL_PADDING = 5;

	ProgressIndicatorBar() {
		bar.setMaxWidth(Double.MAX_VALUE); // allows the progress bar to expand
											// to fill available horizontal
											// space.
		getChildren().setAll(bar, text);
	}

	// synchronizes the progress indicated with the work done.
	public void setProgress(Double progress) {
		if (progress == null) {
			text.setText("");
			bar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
		} else {
			text.setText(Math.ceil(progress * 100) + "%");
			bar.setProgress(progress);
		}

		bar.setMinHeight(text.getBoundsInLocal().getHeight()
				+ DEFAULT_LABEL_PADDING * 2);
		bar.setMinWidth(text.getBoundsInLocal().getWidth()
				+ DEFAULT_LABEL_PADDING * 2);
	}

}