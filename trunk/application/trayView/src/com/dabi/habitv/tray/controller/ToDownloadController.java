package com.dabi.habitv.tray.controller;

import java.util.Arrays;
import java.util.Collection;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.GroupBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.StackPaneBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.web.WebView;
import javafx.scene.web.WebViewBuilder;
import javafx.stage.Popup;
import javafx.stage.PopupBuilder;
import javafx.util.Callback;
import javafx.util.StringConverter;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
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
		MenuItem ouvrirIndex = new MenuItem("");
		ouvrirIndex.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				createWebViewPopup();
				System.out.println(toDLTree2.getSelectionModel()
						.getSelectedItem().getValue().getName());
			}
		});
		// toDLTree2.setContextMenu(new ContextMenu(ouvrirIndex));

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
							buildContextMenu(newValue.getValue());
							fillEpisodeList(newValue.getValue());
						}
					}

				});
	}

	private void buildContextMenu(final CategoryDTO category) {
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

		if (category.getFatherCategory() != null) {
			MenuItem indexMenu = new MenuItem("Ouvrir l'index");
			indexMenu.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					getController().openIndex(category);
				}
			});
			contextMenu.getItems().add(indexMenu);
		}

		toDLTree.setContextMenu(contextMenu);
	}

	private void formulaireAjout(CategoryDTO subCategory) {
		// TODO Auto-generated method stub

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
			if (!category.isTemplate()) {
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

	private void createWebViewPopup() {
		WebView webView;
		webView = WebViewBuilder.create().prefWidth(920).prefHeight(560)
				.build();

		Button okButton;
		Popup webViewPopup = PopupBuilder
				.create()
				.content(
						StackPaneBuilder
								.create()
								.children(
										RectangleBuilder.create().width(930)
												.height(620).arcWidth(20)
												.arcHeight(20)
												.fill(Color.WHITE)
												.stroke(Color.GREY)
												.strokeWidth(2).build(),
										BorderPaneBuilder
												.create()
												.center(GroupBuilder.create()
														.children(webView)
														.build())
												.bottom(HBoxBuilder
														.create()
														.id("popupButtonBar")
														.alignment(Pos.CENTER)
														.spacing(10)
														.children(
																ButtonBuilder
																		.create()
																		.id("backButton")
																		.onAction(
																				new EventHandler<ActionEvent>() {
																					@Override
																					public void handle(
																							ActionEvent e) {
																					}
																				})
																		.build(),
																okButton = ButtonBuilder
																		.create()
																		.text("OK")
																		.onAction(
																				new EventHandler<ActionEvent>() {
																					@Override
																					public void handle(
																							ActionEvent e) {
																					}
																				})
																		.build())
														.build()).build())
								.build()).build();

		BorderPane.setAlignment(okButton, Pos.CENTER);
		BorderPane.setMargin(okButton, new Insets(10, 0, 10, 0));

		webViewPopup.show(getStage(),
				(getStage().getWidth() - webViewPopup.getWidth()) / 2
						+ getStage().getX(),
				(getStage().getHeight() - webViewPopup.getHeight()) / 2
						+ getStage().getY());
	}
}
