package com.dabi.habitv.tray.view.window;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.dabi.habitv.tray.view.window.tree.CategoryTreeNode;
import com.dabi.habitv.tray.view.window.tree.ChannelTreeNode;
import com.dabi.habitv.tray.view.window.tree.CheckBoxTreeNodeEditor;
import com.dabi.habitv.tray.view.window.tree.CheckBoxTreeNodeRenderer;
import com.dabi.habitv.tray.view.window.tree.RootTreeNode;

public class EditableTree extends JPanel {

	private static final long serialVersionUID = 1L;

	private RootTreeNode m_rootNode = new RootTreeNode("Plugins");

	private DefaultTreeModel m_model = new DefaultTreeModel(m_rootNode);

	private JTree m_tree = new JTree(m_model);

	// private JButton m_addButton = new JButton("Add Node");

	// private JButton m_delButton = new JButton("Delete Node");

	private JButton m_searchButton = new JButton("Rechercher une catégorie");

	private JTextField m_searchText;

	public EditableTree() {
		setLayout(new BorderLayout());

		m_tree.setEditable(true);
		m_tree.setSelectionRow(0);
		m_tree.setCellRenderer(new CheckBoxTreeNodeRenderer());
		m_tree.setCellEditor(new CheckBoxTreeNodeEditor(m_tree));

		JScrollPane scrollPane = new JScrollPane(m_tree);
		add(scrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();

		// m_addButton.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// CheckBoxTreeNode selNode = (CheckBoxTreeNode) m_tree
		// .getLastSelectedPathComponent();
		// if (selNode == null) {
		// return;
		// }
		// CheckBoxTreeNode newNode = new CheckBoxTreeNode(
		// "New Node");
		// m_model.insertNodeInto(newNode, selNode,
		// selNode.getChildCount());
		//
		// TreeNode[] nodes = m_model.getPathToRoot(newNode);
		// TreePath path = new TreePath(nodes);
		// m_tree.scrollPathToVisible(path);
		// m_tree.setSelectionPath(path);
		// m_tree.startEditingAtPath(path);
		// }
		// });
		// panel.add(m_addButton);
		//
		// m_delButton.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// CheckBoxTreeNode selNode = (CheckBoxTreeNode) m_tree
		// .getLastSelectedPathComponent();
		// removeNode(selNode);
		// }
		// });
		// panel.add(m_delButton);

		JPanel searchPanel = new JPanel();
		searchPanel.setBorder(BorderFactory.createEtchedBorder());

		m_searchText = new JTextField(10);
		searchPanel.add(m_searchText);

		m_searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CategoryTreeNode node = searchNode(m_searchText.getText());
				if (node != null) {
					TreeNode[] nodes = m_model.getPathToRoot(node);
					TreePath path = new TreePath(nodes);
					m_tree.scrollPathToVisible(path);
					m_tree.setSelectionPath(path);
				} else {
					System.out.println("Node with string " + m_searchText.getText() + " not found");
				}
			}
		});
		searchPanel.add(m_searchButton);

		panel.add(searchPanel);
		add(panel, BorderLayout.SOUTH);
		setVisible(true);

	}

	public CategoryTreeNode searchNode(String nodeStr) {
		CategoryTreeNode node = null;
		@SuppressWarnings("rawtypes")
		Enumeration e = m_rootNode.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			Object nextElement = e.nextElement();
			if (nextElement instanceof CategoryTreeNode) {
				node = (CategoryTreeNode) nextElement;
				if (nodeStr.equals(node.getUserObject().toString())) {
					return node;
				}
			}
		}
		return null;
	}

	public void removeNode(CategoryTreeNode selNode) {
		if (selNode == null) {
			return;
		}
		MutableTreeNode parent = (MutableTreeNode) (selNode.getParent());
		if (parent == null) {
			return;
		}
		MutableTreeNode toBeSelNode = getSibling(selNode);
		if (toBeSelNode == null) {
			toBeSelNode = parent;
		}
		TreeNode[] nodes = m_model.getPathToRoot(toBeSelNode);
		TreePath path = new TreePath(nodes);
		m_tree.scrollPathToVisible(path);
		m_tree.setSelectionPath(path);
		m_model.removeNodeFromParent(selNode);
	}

	private MutableTreeNode getSibling(CategoryTreeNode selNode) {
		MutableTreeNode sibling = (MutableTreeNode) selNode.getPreviousSibling();
		if (sibling == null) {
			sibling = (MutableTreeNode) selNode.getNextSibling();
		}
		return sibling;
	}

	public void clear() {
		m_rootNode.removeAllChildren();
	}

	public void addNode(ChannelTreeNode channelTreeNode) {
		m_rootNode.add(channelTreeNode);
	}

	public TreeCellEditor getCellEditor() {
		return m_tree.getCellEditor();
	}

	public void openTree() {
		m_tree.expandRow(0);
	}

	public JTree getM_tree() {
		return m_tree;
	}
	
	
}
