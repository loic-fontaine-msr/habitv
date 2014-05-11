package com.dabi.habitv.tray.view.window.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.dabi.habitv.grabconfig.entities.CategoryType;

public class CheckBoxTreeNodeRenderer implements TreeCellRenderer {
	private JCheckBox leafRenderer = new JCheckBox();

	private DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();

	Color selectionBorderColor, selectionForeground, selectionBackground, textForeground, textBackground;

	private CategoryType category;

	public CheckBoxTreeNodeRenderer() {
		Font fontValue;
		fontValue = UIManager.getFont("Tree.font");

		if (fontValue != null) {
			leafRenderer.setFont(fontValue);
		}
		Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
		leafRenderer.setFocusPainted((booleanValue != null) && (booleanValue.booleanValue()));

		selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
		selectionForeground = UIManager.getColor("Tree.selectionForeground");
		selectionBackground = UIManager.getColor("Tree.selectionBackground");
		textForeground = UIManager.getColor("Tree.textForeground");
		textBackground = UIManager.getColor("Tree.textBackground");
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		Component returnValue;
		if (leaf) {

			String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, false);
			leafRenderer.setText(stringValue);
			leafRenderer.setSelected(false);

			leafRenderer.setEnabled(tree.isEnabled());

			if (selected) {
				leafRenderer.setForeground(selectionForeground);
				leafRenderer.setBackground(selectionBackground);
			} else {
				leafRenderer.setForeground(textForeground);
				leafRenderer.setBackground(textBackground);
			}

			if ((value != null) && (value instanceof CategoryTreeNode)) {
				final CategoryTreeNode categoryTreeNode = (CategoryTreeNode) value;

				leafRenderer.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						categoryTreeNode.setSelected(((JCheckBox) e.getSource()).isSelected());
					}
				});

				leafRenderer.setText(categoryTreeNode.toString());
				leafRenderer.setSelected(categoryTreeNode.isSelected());
				category = (CategoryType) categoryTreeNode.getUserObject();
			}
			returnValue = leafRenderer;
		} else {
			returnValue = nonLeafRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		}
		return returnValue;
	}

	public CategoryType getCategory() {
		return category;
	}
	
}