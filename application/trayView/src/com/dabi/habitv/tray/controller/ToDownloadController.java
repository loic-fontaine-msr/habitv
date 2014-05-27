package com.dabi.habitv.tray.controller;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

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

	private TextField episodeFilter;
	private Collection<EpisodeDTO> currentEpisodes;

	private TextField categoryFilter;

	private Set<String> downloadedEpisodes;

	public ToDownloadController(ProgressIndicator searchCategoryProgress,
			Button refreshCategoryButton, Button cleanCategoryButton,
			TreeView<CategoryDTO> toDLTree, Label indicationTextFlow,
			ListView<EpisodeDTO> episodeListView, TextField episodeFilter,
			TextField categoryFilter) {
		super();
		this.refreshCategoryButton = refreshCategoryButton;
		this.cleanCategoryButton = cleanCategoryButton;
		this.toDLTree = toDLTree;
		this.indicationText = indicationTextFlow;
		this.searchCategoryProgress = searchCategoryProgress;
		this.episodeListView = episodeListView;
		this.episodeFilter = episodeFilter;
		this.categoryFilter = categoryFilter;
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

		// toDLTree.setOnMouseClicked(new EventHandler<MouseEvent>()
		// {
		// @Override
		// public void handle(MouseEvent mouseEvent)
		// {
		// if(mouseEvent.getClickCount() == 2)
		// {
		// TreeItem<CategoryDTO> item =
		// toDLTree2.getSelectionModel().getSelectedItem();
		//
		// if (item instanceof CheckBoxTreeItem) {
		// CategoryDTO category = ((CheckBoxTreeItem<CategoryDTO>)
		// item).getValue();
		// }
		// }
		// }
		// });

		toDLTree.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<TreeItem<CategoryDTO>>() {

					@Override
					public void changed(
							ObservableValue<? extends TreeItem<CategoryDTO>> arg0,
							TreeItem<CategoryDTO> oldValue,
							TreeItem<CategoryDTO> newValue) {
						if (newValue != null) {
							buildContextMenu(newValue);
							CategoryDTO category = newValue.getValue();
							if (category.isDownloadable()) {
								fillEpisodeList(category);
							} else {
								emptyEpisodeList();
							}
						}
					}

				});
	}

	private void emptyEpisodeList() {
		ObservableList<EpisodeDTO> obsEp = FXCollections.observableArrayList();
		episodeListView.setItems(obsEp);
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
		downloadedEpisodes = getController().getManager()
				.findDownloadedEpisodes(category);

		new Thread(new Runnable() {

			@Override
			public void run() {
				currentEpisodes = getController().findEpisodeByCategory(
						category);
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						initListView(currentEpisodes);
					}
				});
			}

		}).start();

	}

	private void initListView(final Collection<EpisodeDTO> episodes) {
		ObservableList<EpisodeDTO> obsEp = FXCollections.observableArrayList();
		obsEp.addAll(episodes);

		episodeListView.setItems(obsEp);

		episodeListView
				.setCellFactory(new Callback<ListView<EpisodeDTO>, ListCell<EpisodeDTO>>() {
					@Override
					public ListCell<EpisodeDTO> call(ListView<EpisodeDTO> param) {
						return new ListCell<EpisodeDTO>() {

							@Override
							protected void updateItem(EpisodeDTO episode,
									boolean empty) {
								super.updateItem(episode, empty);

								if (!empty) {
									setText(episode.getName());

									if (downloadedEpisodes.contains(episode.getName())){
										setTextFill(Color.GRAY);
									} else {
										setTextFill(Color.BLACK);
									}
								} else {
									setText(null);
								}
							}
						};
					}
				});

		// episodeListView.setCellFactory(forListView);

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
		initFilters();
	}

	private void initFilters() {
		episodeFilter.setOnKeyTyped(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				filterEpisodeListView(episodeFilter.getText());
			}
		});

		categoryFilter.setOnKeyTyped(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				filterTree(categoryFilter.getText());
			}

		});
	}

	private void filterTree(String text) {
		categoriesToHide.clear();
		String textUpper = text.toUpperCase();
		// filterNodes(toDLTree.getRoot().getChildren(), textUpper);
		filterCategories(plugins, textUpper);
		loadTree(plugins);
		expandAll(toDLTree.getRoot().getChildren());
	}

	private void expandAll(Collection<TreeItem<CategoryDTO>> observableList) {
		for (TreeItem<CategoryDTO> treeItem : observableList) {
			treeItem.setExpanded(true);
			expandAll(treeItem.getChildren());
		}
	}

	private void filterCategories(Map<String, CategoryDTO> plugins,
			String textUpper) {
		for (Entry<String, CategoryDTO> pluginEntry : plugins.entrySet()) {
			if (!categoryPassFilter(pluginEntry.getValue(), textUpper)) {
				categoriesToHide.add(pluginEntry.getValue());
			}
		}

	}

	// private boolean filterNodes(Collection<TreeItem<CategoryDTO>> children,
	// String textUpper) {
	// boolean hasNodeToShow = false;
	// for (TreeItem<CategoryDTO> treeItem : children) {
	// boolean passFilter = treeItem.getValue().getName()
	// .contains(textUpper);
	// boolean hasChildrenToShow = filterNodes(treeItem.getChildren(),
	// textUpper);
	// boolean show = passFilter || hasChildrenToShow;
	//
	// treeItem.setExpanded(show);
	//
	// hasNodeToShow = show || hasNodeToShow;
	// }
	//
	// return hasNodeToShow;
	// }
	private final Set<CategoryDTO> categoriesToHide = new HashSet<>();

	private boolean categoryPassFilter(CategoryDTO category, String textUpper) {

		boolean passFilter = category.getName().toUpperCase()
				.contains(textUpper);
		boolean hasChildrenToShow = categoriesPassFilter(
				category.getSubCategories(), textUpper);
		boolean show = passFilter || hasChildrenToShow;

		if (!show) {
			categoriesToHide.add(category);
		}

		return show;
	}

	private boolean categoriesPassFilter(Set<CategoryDTO> subCategories,
			String textUpper) {
		boolean catToShow = false;
		if (subCategories != null) {
			for (CategoryDTO categoryDTO : subCategories) {
				catToShow = categoryPassFilter(categoryDTO, textUpper)
						|| catToShow;
			}
		}
		return catToShow;
	}

	private void filterEpisodeListView(String text) {
		if (text != null && !text.isEmpty()) {
			initListView(filterEpisodeList(text));
		}
	}

	private Collection<EpisodeDTO> filterEpisodeList(String text) {
		Collection<EpisodeDTO> filteredList = new LinkedList<>();
		String textUpper = text.toUpperCase();
		for (EpisodeDTO episodeDTO : currentEpisodes) {
			String episodeName = episodeDTO.getName();
			if (episodeName.toUpperCase().contains(textUpper)) {
				filteredList.add(episodeDTO);
			}
		}

		return filteredList;
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
		plugins = getController().loadCategories();
		loadTree(plugins);
	}

	private void loadTree(Map<String, CategoryDTO> pluginsToDisplay) {
		TreeItem<CategoryDTO> root = new RootTreeItem();
		toDLTree.setRoot(root);
		toDLTree.setShowRoot(false);
		toDLTree.setCellFactory(CheckBoxTreeCell.<CategoryDTO> forTreeView());
		for (CategoryDTO plugin : pluginsToDisplay.values()) {
			if (!categoriesToHide.contains(plugin)) {
				PluginTreeItem channelTreeItem = new PluginTreeItem(plugin);
				root.getChildren().add(channelTreeItem);
				addCategoriesToTree(channelTreeItem, plugin.getSubCategories());
			}
		}
	}

	private void addCategoriesToTree(CheckBoxTreeItem<CategoryDTO> treeItem,
			Collection<CategoryDTO> categories) {
		for (CategoryDTO category : categories) {
			if (!category.isTemplate() && !category.isDeleted()
					&& !categoriesToHide.contains(category)) {
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
			setIndependent(category.isDownloadable());
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
