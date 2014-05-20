package com.dabi.habitv.tray.controller;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.pub.UpdatablePluginEvent;
import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchCategoryEvent;
import com.dabi.habitv.core.event.SearchEvent;
import com.dabi.habitv.core.event.UpdatePluginEvent;
import com.dabi.habitv.tray.subscriber.CoreSubscriber;

public class ToDownloadController extends BaseController implements
		CoreSubscriber {

	private Button refreshCategoryButton;

	private Button cleanCategoryButton;

	private TreeView<CategoryDTO> toDLTree;

	private Map<String, Set<CategoryDTO>> channels;

	private TextFlow indicationTextFlow;

	private ProgressIndicator searchCategoryProgress;

	public ToDownloadController(ProgressIndicator searchCategoryProgress,
			Button refreshCategoryButton, Button cleanCategoryButton,
			TreeView<CategoryDTO> toDLTree, TextFlow indicationTextFlow) {
		super();
		this.refreshCategoryButton = refreshCategoryButton;
		this.cleanCategoryButton = cleanCategoryButton;
		this.toDLTree = toDLTree;
		this.indicationTextFlow = indicationTextFlow;
		this.searchCategoryProgress = searchCategoryProgress;
		final TreeView<CategoryDTO> toDLTree2 = toDLTree;
		toDLTree.addEventHandler(KeyEvent.KEY_PRESSED,
				new EventHandler<KeyEvent>() {

					@Override
					public void handle(KeyEvent event) {
						if (event.getCode() == KeyCode.SPACE) {
							TreeItem<CategoryDTO> selectedItem = toDLTree2
									.getSelectionModel().getSelectedItem();
							if (selectedItem instanceof CheckBoxTreeItem) {
								CheckBoxTreeItem<CategoryDTO> checkBoxTreeItem = (CheckBoxTreeItem<CategoryDTO>) selectedItem;
								checkBoxTreeItem.setSelected(!checkBoxTreeItem
										.isSelected());
							}
						}
					}
				});
	}

	@Override
	protected void init() {
		loadTree();
		addButtonsActions();
		addTooltips();
	}

	private void addTooltips() {
		refreshCategoryButton.setTooltip(new Tooltip(
				"Rafraichir l'arbre des catégories."));
		cleanCategoryButton.setTooltip(new Tooltip(
				"Enlever les catégories périmées."));
		indicationTextFlow
				.getChildren()
				.add(new Text(
						"Selectionner les catégories à surveiller pour le téléchargement automatique."));
	}

	private void addButtonsActions() {
		refreshCategoryButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						getController().getManager().updateGrabConfig();
					}
				}).start();
			}
		});
		cleanCategoryButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				getController().getManager().cleanCategories();
				loadTree();
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
			setIndeterminate((CheckBoxTreeItem<CategoryDTO>) treeItem
					.getParent());
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

	@Override
	public void update(UpdatePluginEvent event) {
	}

	@Override
	public void update(UpdatablePluginEvent event) {
	}

	@Override
	public void update(SearchEvent event) {
	}

	@Override
	public void update(RetreiveEvent event) {
	}

	private int searchCount;
	private int searchSize;

	@Override
	public void update(final SearchCategoryEvent event) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				switch (event.getState()) {
				case STARTING:
					refreshCategoryButton.setDisable(true);
					searchCount = 0;
					searchSize = Integer.parseInt(event.getInfo());
					searchCategoryProgress.setProgress((double) searchCount
							/ searchSize);
					break;
				case CATEGORIES_BUILT:
					searchCount++;
					searchCategoryProgress.setProgress((double) searchCount
							/ searchSize);
					break;
				case DONE:
					refreshCategoryButton.setDisable(false);
					searchCategoryProgress.setProgress(1);
					loadTree();
					break;
				default:
					break;
				}
			}

		});
	}
}
