package com.dabi.habitv.tray.controller.todl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.tpl.TemplateFilled;
import com.dabi.habitv.framework.plugin.tpl.TemplateParam;
import com.dabi.habitv.framework.plugin.tpl.TemplateUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

class CategoryForm extends HBox {

	private List<TextField> textFieldList = new ArrayList<>();
	private Double advisedWidth;
	private Double advisedHeight;

	public CategoryForm(CategoryDTO category) {
		super(3);
		if (category.getId().contains("!!!")) {
			initTemplateV3(category);
		} else {
			initTemplateV2(category);
		}
	}

	private void initTemplateV2(CategoryDTO category) {
		getChildren().add(new Label(category.getId().split("!!")[1]));
		TextField textField = new TextField();
		textField.setId("ID");
		textFieldList.add(textField);
		getChildren().add(textField);
	}

	private void initTemplateV3(CategoryDTO category) {
		TemplateFilled templateFilled = TemplateUtils.parseTemplateFilled(category.getId());

		advisedHeight = 100d + templateFilled.getParams().size() * 60d;
		advisedWidth = 700d;
		GridPane grid = buildGrid();
		getChildren().add(new Label(templateFilled.getComment()));
		getChildren().add(grid);

		int i = 0;
		for (TemplateParam templateParam : templateFilled.getParams()) {
			grid.add(buildLabel(templateParam.getId(), templateParam.getName()), 0, i);
			grid.add(buildTextField(templateParam.getId(), templateParam.getDefaultValue()), 1, i);
			i++;
		}
	}

	private GridPane buildGrid() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		return grid;
	}

	private Label buildLabel(String id, String name) {
		Label label = new Label(name);
		label.setId(id);
		return label;
	}

	private TextField buildTextField(String id, String defaultValue) {
		TextField textField = new TextField();
		textField.setId(id);
		textField.setText(defaultValue);
		textFieldList.add(textField);
		return textField;
	}

	public List<TextField> getTextFieldList() {
		return textFieldList;
	}

	public Map<String, String> getValues() {
		Map<String, String> values = new HashMap<>();
		for (TextField textField : textFieldList) {
			values.put(textField.getId(), textField.getText());
		}
		return values;
	}

	public Double getAdvisedWidth() {
		return advisedWidth;
	}

	public Double getAdvisedHeight() {
		return advisedHeight;
	}

}