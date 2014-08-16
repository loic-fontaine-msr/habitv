package com.dabi.habitv.tray.controller.todl;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;

class CategoryForm extends HBox {

	private TextField textField = new TextField();

	public CategoryForm(CategoryDTO category) {
		super(3);
		getChildren().add(new Label(category.getId().split("!!")[1]));
		getChildren().add(textField);
	}

	public TextField getTextField() {
		return textField;
	}
	
}