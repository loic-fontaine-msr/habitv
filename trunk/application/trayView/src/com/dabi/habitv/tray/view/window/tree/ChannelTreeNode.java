package com.dabi.habitv.tray.view.window.tree;

import javax.swing.tree.DefaultMutableTreeNode;

import com.dabi.habitv.grabconfig.entities.ChannelType;

public class ChannelTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;

	public ChannelTreeNode(ChannelType channel) {
		super(channel);
	}

	@Override
	public String toString() {
		return getChannel().getName();
	}

	private ChannelType getChannel() {
		return (ChannelType) getUserObject();
	}

	
	
}