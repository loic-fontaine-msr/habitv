package com.dabi.habitv.tray.view.window;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField downloapOutputTextField = buidDownloadOutputField();

	private JTextField checkDemonTimeTextField = buildDemonTimeTextField();

	private JTextField maxAttempsTextField = buildMaxAttempsTextField();

	public ConfigPanel() {
		super(new GridBagLayout());
		init();
	}

	private JTextField buildMaxAttempsTextField() {
		JTextField jTextField = new JTextField();
		jTextField.setColumns(5);
		jTextField.setToolTipText("Une fois ce nombre atteint le téléchargement sera marqué en erreur.");
		return jTextField;
	}

	private JTextField buildDemonTimeTextField() {
		JTextField jTextField = new JTextField();
		jTextField.setColumns(10);
		jTextField.setToolTipText("Période entre 2 recherches automatiques d'habiTv.");
		return jTextField;
	}

	private JTextField buidDownloadOutputField() {
		JTextField jTextField = new JTextField();
		jTextField.setColumns(30);
		jTextField.setToolTipText("Utiliser les patterns : " + "\n - #EPISODE_NAME# : nom de l'épisode"
				+ "\n - #PROVIDER_NAME# : nom du plugin" + "\n - #CATEGORY_NAME# : nom de la catégorie"
				+ "\n - #EXTENSION# extension du fichier" + "\n - #DATE# date de téléchargement" + "\n - #NUM# num�ro de l'épisode.");
		return jTextField;
	}

	private void init() {
		addField("Destination des téléchargements", this.downloapOutputTextField);
		addField("Période entre 2 recherches (sec) ", this.checkDemonTimeTextField);
		addField("Nombre de tentatives de téchargements", this.maxAttempsTextField);
	}

	private int row = 0;

	private void addField(String label, JTextField jTextField) {
		JLabel lab = new JLabel(label + " : ", JLabel.RIGHT);
		lab.setLabelFor(jTextField);
		// if (i < mnemonics.length)
		lab.setDisplayedMnemonic(30);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		c.ipady = 10; // make this component tall
		c.weightx = 199.0;
		c.gridx = 0;
		c.gridy = row;
		add(lab, c);
		// JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.ipady = 10; // make this component tall
		c.weightx = 199.0;
		c.gridx = 1;
		c.gridy = row;
		add(jTextField, c);
		// add(p);
		row++;
	}

}
