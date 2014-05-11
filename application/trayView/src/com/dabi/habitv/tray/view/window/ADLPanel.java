package com.dabi.habitv.tray.view.window;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.dabi.habitv.core.dao.GrabConfigDAO;
import com.dabi.habitv.grabconfig.entities.CategoryType;
import com.dabi.habitv.grabconfig.entities.ChannelType;
import com.dabi.habitv.grabconfig.entities.GrabConfig;
import com.dabi.habitv.tray.view.window.tree.CategoryTreeNode;
import com.dabi.habitv.tray.view.window.tree.ChannelTreeNode;

public class ADLPanel extends JPanel {

	private final GrabConfigDAO grabConfigDAO;

	private static final long serialVersionUID = 1L;

	private EditableTree editableTree;

	private GrabConfig grabConfig;

	public ADLPanel(GrabConfigDAO configDAO) {
		this.grabConfigDAO = configDAO;
		init();
		fillTree();
		editableTree.getM_tree().getModel().addTreeModelListener(new TreeModelListener() {

			@Override
			public void treeStructureChanged(TreeModelEvent e) {

			}

			@Override
			public void treeNodesRemoved(TreeModelEvent e) {

			}

			@Override
			public void treeNodesInserted(TreeModelEvent e) {

			}

			@Override
			public void treeNodesChanged(TreeModelEvent e) {
				grabConfigDAO.marshal(grabConfig);
			}
		});
		;
	}

	private void fillTree() {
		grabConfig = grabConfigDAO.unmarshal();
		editableTree.clear();
		for (ChannelType channel : grabConfig.getChannels().getChannel()) {
			ChannelTreeNode channelTreeNode = new ChannelTreeNode(channel);
			editableTree.addNode(channelTreeNode);
			DefaultMutableTreeNode treeNode = channelTreeNode;
			List<CategoryType> fillCategory = channel.getCategories().getCategory();
			fillCategory(treeNode, fillCategory);
		}
		editableTree.openTree();
	}

	private void fillCategory(DefaultMutableTreeNode treeNode, List<CategoryType> categories) {
		for (CategoryType category : categories) {
			CategoryTreeNode categoryNode = new CategoryTreeNode(category);
			treeNode.add(categoryNode);
			if (category.getSubcategories() != null) {
				fillCategory(categoryNode, category.getSubcategories().getCategory());
			}
		}
	}

	private void init() {

		editableTree = new EditableTree();
		// JScrollPane scrollPane = new JScrollPane(tree);
		JScrollPane scrollPane = new JScrollPane(editableTree);
		setLayout(new GridLayout(1, 1));
		add(scrollPane);
	}

}
