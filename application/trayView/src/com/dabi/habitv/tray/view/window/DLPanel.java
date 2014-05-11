package com.dabi.habitv.tray.view.window;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

public class DLPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public DLPanel() {
		init();
	}

	private void init() {
		setLayout(new GridBagLayout());

		JProgressBar progressBar = new JProgressBar();
		progressBar.setValue(75);
		progressBar.setStringPainted(true);
		Border border = BorderFactory.createTitledBorder("Reading...");
		progressBar.setBorder(border);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		c.ipady = 10; // make this component tall
		c.weightx = 199.0;
		c.gridx = 0;
		c.gridy = 1;

		add(progressBar, c);

		progressBar = new JProgressBar();
		progressBar.setValue(40);
		progressBar.setStringPainted(true);
		border = BorderFactory.createTitledBorder("Reading...");
		progressBar.setBorder(border);

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		c.ipady = 10; // make this component tall
		c.weightx = 199.0;
		c.gridx = 0;
		c.gridy = 2;
		add(progressBar, c);
	}

}
