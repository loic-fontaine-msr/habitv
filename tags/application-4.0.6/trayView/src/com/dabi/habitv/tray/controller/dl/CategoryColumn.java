package com.dabi.habitv.tray.controller.dl;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import com.dabi.habitv.tray.model.ActionProgress;

public class CategoryColumn extends TableColumn<ActionProgress, String> {

	public CategoryColumn() {
		super("Catégorie");
		setPrefWidth(50);
		setCellValueFactory(new Callback<CellDataFeatures<ActionProgress, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(
					CellDataFeatures<ActionProgress, String> param) {
				return new SimpleStringProperty(param.getValue().getEpisode().getCategory().getName());
			}
		});
	}

}
