package com.dabi.habitv.tray.controller;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.api.plugin.pub.UpdatablePluginEvent;
import com.dabi.habitv.core.event.RetreiveEvent;
import com.dabi.habitv.core.event.SearchCategoryEvent;
import com.dabi.habitv.core.event.SearchEvent;
import com.dabi.habitv.core.event.UpdatePluginEvent;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.tray.Popin;
import com.dabi.habitv.tray.PopinController.ButtonHandler;
import com.dabi.habitv.tray.subscriber.CoreSubscriber;

public class ToDownloadController extends BaseController implements
		CoreSubscriber {

	private Button refreshCategoryButton;

	private Button cleanCategoryButton;

	private TreeView<CategoryDTO> toDLTree;

	private Map<String, CategoryDTO> plugins;

	private Label indicationText;

	private ProgressIndicator searchCategoryProgress;

	private ListView<EpisodeDTO> episodeListView;

	public ToDownloadController(ProgressIndicator searchCategoryProgress,
			Button refreshCategoryButton, Button cleanCategoryButton,
			TreeView<CategoryDTO> toDLTree, Label indicationTextFlow,
			ListView<EpisodeDTO> episodeListView) {
		super();
		this.refreshCategoryButton = refreshCategoryButton;
		this.cleanCategoryButton = cleanCategoryButton;
		this.toDLTree = toDLTree;
		this.indicationText = indicationTextFlow;
		this.searchCategoryProgress = searchCategoryProgress;
		this.episodeListView = episodeListView;
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

		toDLTree.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<TreeItem<CategoryDTO>>() {

					@Override
					public void changed(
							ObservableValue<? extends TreeItem<CategoryDTO>> arg0,
							TreeItem<CategoryDTO> oldValue,
							TreeItem<CategoryDTO> newValue) {
						if (newValue != null) {
							buildContextMenu(newValue);
							fillEpisodeList(newValue.getValue());
						}
					}

				});
	}

	private void buildContextMenu(TreeItem<CategoryDTO> treeItem) {
		final CategoryDTO category = treeItem.getValue();
		ContextMenu contextMenu = new ContextMenu();
		if (category.hasTemplates()) {
			Menu ajoutMenu = new Menu("Ajouter une catégorie");
			for (final CategoryDTO subCategory : category.getSubCategories()) {
				if (subCategory.isTemplate()) {
					MenuItem menuItem = new MenuItem(subCategory.getName());
					menuItem.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							formulaireAjout(subCategory);
						}

					});
					ajoutMenu.getItems().add(menuItem);
				}
			}
			contextMenu.getItems().add(ajoutMenu);
		}

		if (treeItem.getParent() != null) {
			MenuItem indexMenu = new MenuItem("Ouvrir l'index");
			indexMenu.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					getController().openIndex(category);
				}
			});
			contextMenu.getItems().add(indexMenu);

			MenuItem supprimerMenu = new MenuItem("Supprimer");
			supprimerMenu.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					category.setDeleted(true);
					planTaskIfNot(new Runnable() {

						@Override
						public void run() {
							saveTree();
						}
					});
					toDLTree.getSelectionModel()
							.getSelectedItem()
							.getParent()
							.getChildren()
							.remove(toDLTree.getSelectionModel()
									.getSelectedItem());
				}
			});
			contextMenu.getItems().add(supprimerMenu);
		}

		toDLTree.setContextMenu(contextMenu);
	}

	private void formulaireAjout(final CategoryDTO templateCategory) {
		final CategoryForm categoryForm = new CategoryForm(templateCategory);
		new Popin().show("Ajout d'une catégorie " + templateCategory.getName(),
				categoryForm).setOkButtonHandler(new ButtonHandler() {

			@Override
			public void onAction() {
				templateCategory.getFatherCategory().addSubCategory(
						buildCategoryFromTemplate(templateCategory,
								categoryForm.textField.getText()));
				saveTree();
				loadTree();
			}

		});
	}

	private CategoryDTO buildCategoryFromTemplate(CategoryDTO templateCategory,
			String text) {
		String id = templateCategory.getId().split("!!")[0].replace("§ID§",
				text);
		return new CategoryDTO(templateCategory.getPlugin(), findNameById(id),
				id, FrameworkConf.MP4);
	}

	private String findNameById(String id) {
		String name;
		if (id.startsWith(FrameworkConf.HTTP_PREFIX)) {
			name = RetrieverUtils.getTitleByUrl(id);
		} else {
			File file = new File(id);
			if (file.exists()) {
				name = file.getName();
			} else {
				name = id;
			}
		}
		return name;
	}

	private static class CategoryForm extends HBox {

		private TextField textField = new TextField();
		
		public CategoryForm(CategoryDTO category) {
			super(3);	
			getChildren().add(new Label(category.getId().split("!!")[1]));
			getChildren().add(textField);
		}
	}

	private void fillEpisodeList(final CategoryDTO category) {
		ObservableList<EpisodeDTO> obsEp = FXCollections.observableArrayList();
		obsEp.addAll(Arrays.asList(new EpisodeDTO(null, "Chargement...", "")));

		episodeListView.setItems(obsEp);
		new Thread(new Runnable() {

			@Override
			public void run() {
				final Collection<EpisodeDTO> episodes = getController()
						.findEpisodeByCategory(category);
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						initListView(episodes);
					}
				});
			}

		}).start();

	}

	private void initListView(final Collection<EpisodeDTO> episodes) {
		ObservableList<EpisodeDTO> obsEp = FXCollections.observableArrayList();
		obsEp.addAll(episodes);

		episodeListView.setItems(obsEp);
		StringConverter<EpisodeDTO> stringConverter = new StringConverter<EpisodeDTO>() {
			@Override
			public String toString(EpisodeDTO episode) {
				return episode.getName();
			}

			@Override
			public EpisodeDTO fromString(String string) {
				for (EpisodeDTO episode : episodes) {
					if (string.equals(episode.getName())) {
						return episode;
					}
				}
				return null;
			}
		};
		Callback<ListView<EpisodeDTO>, ListCell<EpisodeDTO>> forListView = TextFieldListCell
				.forListView(stringConverter);

		episodeListView.setCellFactory(forListView);

		episodeListView.setContextMenu(buildEpisodeContextMenu());
	}

	private ContextMenu buildEpisodeContextMenu() {
		MenuItem telecharger = new MenuItem("Télécharger");
		telecharger.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				getController().downloadEpisode(
						episodeListView.getSelectionModel().getSelectedItem());
			}
		});
		ContextMenu contextMenu = new ContextMenu(telecharger);
		return contextMenu;
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
		indicationText
				.setText("Selectionner les catégories à surveiller pour le téléchargement automatique \n et cliquer sur les épisodes à droite pour le téléchargement manuel.");
	}

	private void addButtonsActions() {
		refreshCategoryButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						getController().getManager().updateGrabConfig();
						Platform.runLater(new Runnable() {
							
							@Override
							public void run() {
								loadTree();
							}
						});
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
		plugins = getController().loadCategories();
		for (CategoryDTO plugin : plugins.values()) {
			PluginTreeItem channelTreeItem = new PluginTreeItem(plugin);
			root.getChildren().add(channelTreeItem);
			addCategoriesToTree(channelTreeItem, plugin.getSubCategories());
		}

	}

	private void addCategoriesToTree(CheckBoxTreeItem<CategoryDTO> treeItem,
			Collection<CategoryDTO> categories) {
		for (CategoryDTO category : categories) {
			if (!category.isTemplate() && !category.isDeleted()) {
				CategoryTreeItem categoryTreeItem = setSelected(treeItem,
						category);
				treeItem.getChildren().add(categoryTreeItem);
				if (category.getSubCategories() != null
						&& !category.getSubCategories().isEmpty()) {
					addCategoriesToTree(categoryTreeItem,
							category.getSubCategories());
				}
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

	private static class PluginTreeItem extends CheckBoxTreeItem<CategoryDTO> {

		public PluginTreeItem(CategoryDTO plugin) {
			super(plugin);
		}

	}

	private class CategoryTreeItem extends CheckBoxTreeItem<CategoryDTO> {

		public CategoryTreeItem(final CategoryDTO category) {
			super(category);
			setIndependent(true);
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
		if (plugins != null) {
			getController().getManager().saveGrabConfig(plugins);
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
