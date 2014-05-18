package com.dabi.habitv.tray.view.fx;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;

public class ToDownloadController extends BaseController {

	private Button refreshCategoryButton;

	private Button cleanCategoryButton;

	private TreeView<CategoryDTO> toDLTree;

	private Map<String, Set<CategoryDTO>> channels;

	public ToDownloadController(Button refreshCategoryButton,
			Button cleanCategoryButton, TreeView<CategoryDTO> toDLTree) {
		super();
		this.refreshCategoryButton = refreshCategoryButton;
		this.cleanCategoryButton = cleanCategoryButton;
		this.toDLTree = toDLTree;
	}

	@Override
	protected void init() {
		loadTree();
		addButtonsActions();
	}

	private void addButtonsActions() {
		refreshCategoryButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				getController().getManager().updateGrabConfig();
				loadTree();
			}
		});
		cleanCategoryButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				getController().getManager().cleanCategories();
			}
		});
	}

	private void loadTree() {
		TreeItem<CategoryDTO> root = new RootTreeItem();
		toDLTree.setRoot(root);
		toDLTree.setShowRoot(false);
		toDLTree.setCellFactory(CheckBoxTreeCell.<CategoryDTO> forTreeView());
		channels = getController().loadCategories();
		for (Entry<String, Set<CategoryDTO>> channel : channels.entrySet()) {
			ChannelTreeItem channelTreeItem = new ChannelTreeItem(
					channel.getKey());
			root.getChildren().add(channelTreeItem);
			addCategoriesToTree(channelTreeItem, channel.getValue());
		}
	}

	private void addCategoriesToTree(CheckBoxTreeItem<CategoryDTO> treeItem,
			Collection<CategoryDTO> categories) {
		for (CategoryDTO category : categories) {
			CategoryTreeItem categoryTreeItem = setSelected(treeItem, category);
			treeItem.getChildren().add(categoryTreeItem);
			if (category.getSubCategories() != null
					&& !category.getSubCategories().isEmpty()) {
				addCategoriesToTree(categoryTreeItem,
						category.getSubCategories());
			}
		}
	}

	private CategoryTreeItem setSelected(
			CheckBoxTreeItem<CategoryDTO> treeItem, CategoryDTO category) {
		CategoryTreeItem categoryTreeItem = new CategoryTreeItem(category);
		categoryTreeItem.setSelected(category.isSelected());
		if (category.isSelected()) {
			setIndeterminate(treeItem);
		}
		return categoryTreeItem;
	}

	private void setIndeterminate(CheckBoxTreeItem<CategoryDTO> treeItem) {
		treeItem.setIndeterminate(true);
		if (treeItem.getParent() != null) {
			setIndeterminate((CheckBoxTreeItem<CategoryDTO>) treeItem.getParent());
		}
	}

	private class RootTreeItem extends CheckBoxTreeItem<CategoryDTO> {

		public RootTreeItem() {
			super(new CategoryDTO(null, "Chaines", "root", null));
		}
	}

	private class ChannelTreeItem extends CheckBoxTreeItem<CategoryDTO> {

		public ChannelTreeItem(String channel) {
			super(new CategoryDTO(null, channel, channel, null));
		}
	}

	private class CategoryTreeItem extends CheckBoxTreeItem<CategoryDTO> {

		public CategoryTreeItem(final CategoryDTO category) {
			super(category);
			addEventHandler(
					CheckBoxTreeItem.<String> checkBoxSelectionChangedEvent(),
					new EventHandler<TreeModificationEvent<String>>() {
						public void handle(TreeModificationEvent<String> event) {
							if (event.wasSelectionChanged()) {
								category.setSelected(isSelected());
								planTaskIfNot(new Runnable() {

									@Override
									public void run() {
										saveTree();
									}
								});
							}
						}

					});
		}
	}

	private void saveTree() {
		if (channels != null) {
			getController().saveGrabconfig(channels);
		}
	}
}
