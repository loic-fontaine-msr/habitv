package com.dabi.habitv.tray.view.window;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.dabi.habitv.core.dao.GrabConfigDAO;

public class WindowRootView extends JFrame {
	private static final long serialVersionUID = 1L;
	private final DLPanel dlPanel;
	private final ADLPanel adlPanel;
	private final ConfigPanel configPanel;

	public WindowRootView(GrabConfigDAO grabConfigDAO) {
		super("habiTv");
		adlPanel = new ADLPanel(grabConfigDAO);
		dlPanel = new DLPanel();
		configPanel = new ConfigPanel();

		getContentPane().add(buildTabbedPanel(), BorderLayout.CENTER);
		setSize(600, 500);
		// pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	private JTabbedPane buildTabbedPanel() {
		JTabbedPane tabbedPane = new JTabbedPane();

		tabbedPane.addTab("Téléchargements", createImageIcon("dl.png"), dlPanel);

		tabbedPane.addTab("A télécharger", createImageIcon("adl.png"), adlPanel);

		tabbedPane.addTab("Configuration", createImageIcon("config.png"), configPanel);

		tabbedPane.setSelectedIndex(1);

		return tabbedPane;
	}

	protected static ImageIcon createImageIcon(String path) {
		Image image = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(path));
		if (image != null) {
			return new ImageIcon(image);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

}
