package com.dabi.habitv.tray.view.fx;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.CheckBoxTreeItem.TreeModificationEvent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;

public class ToDownloadController extends BaseController{

	private Button refreshCategoryButton;

	private Button cleanCategoryButton;

	private TreeView<CategoryDTO> toDLTree;

	public ToDownloadController(Button refreshCategoryButton,
			Button cleanCategoryButton, TreeView<CategoryDTO> toDLTree) {
		super();
		this.refreshCategoryButton = refreshCategoryButton;
		this.cleanCategoryButton = cleanCategoryButton;
		this.toDLTree = toDLTree;
	}

	public void init() {
		TreeItem<CategoryDTO> root = buildTreeItem();
		toDLTree.setRoot(root);
		toDLTree.setCellFactory(CheckBoxTreeCell.<CategoryDTO> forTreeView());

		TreeItem<CategoryDTO> buildTreeItem = buildTreeItem();
		buildTreeItem.getChildren().add(buildTreeItem());
		buildTreeItem.getChildren().add(buildTreeItem());
		buildTreeItem.getChildren().add(buildTreeItem());

		toDLTree.getRoot().getChildren().add(buildTreeItem);
	}
	
	private TreeItem<CategoryDTO> buildTreeItem() {
		CheckBoxTreeItem<CategoryDTO> checkBoxTreeItem = new CheckBoxTreeItem<CategoryDTO>(
				new CategoryDTO("channel", "name", "identifier", "extension"));
		checkBoxTreeItem.addEventHandler(
				CheckBoxTreeItem.<String> checkBoxSelectionChangedEvent(),
				new EventHandler<TreeModificationEvent<String>>() {
					public void handle(TreeModificationEvent<String> event) {
						if (event.wasSelectionChanged()) {
							planTaskIfNot(new Runnable() {
								
								@Override
								public void run() {
									System.out.println("change tree");
								}
							});
						}
					}

				});
		return checkBoxTreeItem;
	}
}
