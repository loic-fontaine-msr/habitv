package com.dabi.habitv.tray.controller.todl;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;

public class IncludeExcludeReCall extends HBox {

	//private Label label = new Label();

	private Button deleteButton = new Button();

	private EventHandler<ActionEvent> deleteHandler;

	public IncludeExcludeReCall(final CategoryDTO category,
			final String pattern, final boolean include) {
		setSpacing(5);
		//getChildren().add(label);
		getChildren().add(deleteButton);

		deleteButton.setText((include ? "Inclusion" : "Exclusion") + " : " + pattern);
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (include) {
					category.getInclude().remove(pattern);
				} else {
					category.getExclude().remove(pattern);
				}

				if (deleteHandler != null) {
					deleteHandler.handle(event);
				}
			}
		});
	}

	public void setOnAction(EventHandler<ActionEvent> deleteHandler) {
		this.deleteHandler = deleteHandler;
	}
}
