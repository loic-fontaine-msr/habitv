package com.dabi.habitv.tray.controller.dl;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import com.dabi.habitv.tray.model.ActionProgress;

public class EpisodeColumn extends TableColumn<ActionProgress, String> {

	public EpisodeColumn() {
		super("Episode");
		setPrefWidth(550);
		setCellValueFactory(new Callback<CellDataFeatures<ActionProgress, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(
					CellDataFeatures<ActionProgress, String> param) {
				return new SimpleStringProperty(param.getValue().getEpisode()
						.getName());
			}
		});
	}

}
