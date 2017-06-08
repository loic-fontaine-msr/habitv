package com.dabi.habitv.tray.controller.dl;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.tray.controller.ViewController;
import com.dabi.habitv.tray.model.ActionProgress;
import com.dabi.habitv.tray.utils.LabelUtils;

public class DownloadBox extends Pane {

	private EpisodeDTO episode;

	// private final ActionProgress actionProgress;

	public DownloadBox(ViewController viewController, ActionProgress actionProgress) {
		super();
		this.episode = actionProgress.getEpisode();
		// this.actionProgress = actionProgress;
		getChildren().add(getStateWidget(null, actionProgress));
	}

	public void update(ActionProgress actionProgress) {
		Node oldWidget = getChildren().get(0);
		Node newWidget = getStateWidget(oldWidget, actionProgress);
		if (!oldWidget.equals(newWidget)) {
			getChildren().clear();
			getChildren().add(newWidget);
		}
	}

	private Node getStateWidget(Node oldWidget, ActionProgress actionProgress) {
		Node widget;
		switch (actionProgress.getState()) {
		case DOWNLOAD_STARTING:
			widget = getProgressBarWidget(oldWidget, actionProgress);
			break;
		default:
			widget = getLabelWidget(oldWidget, actionProgress);
			break;
		}
		return widget;
	}

	private Node getProgressBarWidget(Node oldWidget, ActionProgress actionProgress) {
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

	private Node getLabelWidget(Node oldWidget, ActionProgress actionProgress) {
		Node widget;
		Label label;
		if (oldWidget == null || !(oldWidget instanceof Label)) {
			label = new Label();
		} else {
			label = (Label) oldWidget;
		}
		widget = label;
		label.setText(LabelUtils.buildStateLabel(actionProgress));
		return widget;
	}

	@Override
	public int hashCode() {
		return episode.hashCode();
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
		return episode.equals(other.episode);
	}

}
