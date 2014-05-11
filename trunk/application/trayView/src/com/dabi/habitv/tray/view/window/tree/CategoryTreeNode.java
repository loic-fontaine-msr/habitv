package com.dabi.habitv.tray.view.window.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.tree.DefaultMutableTreeNode;

import com.dabi.habitv.grabconfig.entities.CategoryType;

public class CategoryTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;

	public CategoryTreeNode(CategoryType category) {
		super(category);
	}

	public CategoryType getCategory() {
		return (CategoryType) getUserObject();
	}

	public boolean isSelected() {
		return getCategory().getDownload() != null && getCategory().getDownload();
	}

	public void setSelected(boolean newValue) {
		if (newValue != getCategory().getDownload()) {
			getCategory().setDownload(newValue);
		}
	}

	@Override
	public String toString() {
		return getCategory() == null ? "" : getCategory().getName();
	}

}