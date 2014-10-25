package com.dabi.habitv.tray.controller.todl;

import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;

class CategoryTreeItem extends CompatibleCheckBoxTreeItem<CategoryDTO> {

	public interface SelectionChangeHandler {
		void onSelectionChange(CategoryTreeItem categoryTreeItem);
	}

	private SelectionChangeHandler selectionChangeHandler;

	public CategoryTreeItem(final CategoryDTO category) {
		super(category);
		setVIndependant(true);
		setVSelected(category.isSelected());
		addEventHandler(
				CategoryTreeItem.<String> checkBoxSelectionChangedEvent(),
				new EventHandler<TreeModificationEvent<String>>() {

					public void handle(TreeModificationEvent<String> event) {
						boolean isSelected = isVSelected();
						if (category.isSelected() != isSelected) {
							category.setSelected(isSelected);
							setVSelected(isSelected);
							if (selectionChangeHandler != null) {
								selectionChangeHandler
										.onSelectionChange(CategoryTreeItem.this);
							}
						}
					}

				});
	}

	public void setSelectionChangeHandler(
			SelectionChangeHandler selectionChangeHandler) {
		this.selectionChangeHandler = selectionChangeHandler;
	}

	Boolean hasSelectedOrIndeterminateChild() {
		for (TreeItem<CategoryDTO> child : getChildren()) {
			CategoryTreeItem categoryTreeItem = (CategoryTreeItem) child;
			if (categoryTreeItem.isVSelected()
					|| categoryTreeItem.isVIndeterminate()) {
				return true;
			}

		}
		return false;
	}

}