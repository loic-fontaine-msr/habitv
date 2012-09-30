package ted.ui.addshowdialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ted.BrowserLauncher;
import ted.Lang;
import ted.TedConfig;
import ted.TedSerie;
import ted.TedSystemInfo;
import ted.datastructures.SimpleTedSerie;
import ted.datastructures.StandardStructure;
import ted.interfaces.EpisodeChooserListener;
import ted.ui.TableRenderer;
import ted.ui.editshowdialog.EditShowDialog;
import ted.view.TedMainDialog;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class AddShowDialog extends JDialog implements ActionListener, EpisodeChooserListener, KeyListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1006862655927988046L;
	private JTable showsTable;
	private JButton cancelButton;
	private JButton okButton;
	private JScrollPane showsScrollPane;
	private ShowsTableModel showsTableModel;
	private TedSerie selectedSerie;
	private TedMainDialog tedMain;
	private JTextPane showInfoPane;
	private JCheckBox downloadInHD;
	private JTextField jSearchField;
	private JLabel showNameLabel;
	private JLabel selectShowLabel;
	private JLabel selectEpisodeLabel;
	private JButton jHelpButton;
	private JScrollPane showInfoScrollPane;
	private JButton buttonAddEmptyShow;
	private Vector<SimpleTedSerie> allShows;
	private SimpleTedSerie selectedShow;
	private ShowInfoThread showInfoThread;
	private EpisodeParserThread episodeParserThread;

	private EpisodeChooserPanel episodeChooserPanel = new EpisodeChooserPanel(this);
	private SubscribeOptionsPanel subscribeOptionsPanel = new SubscribeOptionsPanel(this);

	public AddShowDialog() {
		this.initGUI();
	}

	public AddShowDialog(TedMainDialog main) {
		this.setModal(true);
		this.tedMain = main;
		this.initGUI();
	}

	private void initGUI() {
		try {
			// Set the name of the dialog
			this.setTitle(Lang.getString("TedAddShowDialog.Title"));

			this.episodeChooserPanel.setActivityStatus(false);
			FormLayout thisLayout = new FormLayout("max(p;5dlu), 68dlu:grow, max(p;68dlu), 10dlu, 250px, max(p;100px), 5dlu, 150px, max(p;5dlu)",
					"max(p;5dlu), max(p;15dlu), 5dlu, 50dlu:grow, 5dlu, max(p;15dlu), 5dlu, bottom:130dlu, max(p;15dlu), 5dlu, max(p;15dlu), 5dlu, max(p;15dlu), max(p;5dlu)");
			getContentPane().setLayout(thisLayout);

			episodeChooserPanel.setVisible(false);
			subscribeOptionsPanel.setVisible(true);

			showsTableModel = new ShowsTableModel();
			showsTable = new JTable();
			// getContentPane().add(showsTable, new
			// CellConstraints("4, 3, 1, 1, default, default"));
			getShowsScrollPane().setViewportView(showsTable);
			getContentPane().add(getShowsScrollPane(), new CellConstraints("2, 4, 2, 6, fill, fill"));
			getContentPane().add(episodeChooserPanel, new CellConstraints("5, 4, 4, 1, fill, fill"));
			getContentPane().add(subscribeOptionsPanel, new CellConstraints("5, 8, 4, 1, fill, fill"));
			getContentPane().add(getOkButton(), new CellConstraints("8, 13, 1, 1, default, default"));
			getContentPane().add(getCancelButton(), new CellConstraints("6, 13, 1, 1, default, default"));
			getContentPane().add(getShowInfoScrollPane(), new CellConstraints("5, 4, 4, 1, fill, fill"));
			getContentPane().add(getJHelpButton(), new CellConstraints("2, 13, 1, 1, left, default"));
			getContentPane().add(getSelectShowLabel(), new CellConstraints("2, 2, 2, 1, left, fill"));
			getContentPane().add(getSelectEpisodeLabel(), new CellConstraints("5, 6, 2, 1, left, bottom"));
			getContentPane().add(getShowNameLabel(), new CellConstraints("5, 2, 4, 1, left, fill"));
			getContentPane().add(getButtonAddEmptyShow(), new CellConstraints("2, 11, 2, 1, left, default"));
			getContentPane().add(getJSearchField(), new CellConstraints("3, 2, 1, 1, default, fill"));
			getContentPane().add(getDownloadInHD(), new CellConstraints("5, 9, 4, 1, left, center"));
			showsTable.setModel(showsTableModel);

			allShows = this.readShowNames();
			showsTableModel.setSeries(allShows);

			showsTable.setAutoCreateColumnsFromModel(true);
			showsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
			showsTable.setEditingRow(0);
			showsTable.setFont(new java.awt.Font("Dialog", 0, 15));
			showsTable.setRowHeight(showsTable.getRowHeight() + 10);
			TableRenderer tr = new TableRenderer();
			showsTable.setDefaultRenderer(Object.class, tr);

			// disable horizontal lines in table
			showsTable.setShowHorizontalLines(false);
			showsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			showsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent arg0) {
					showsTableSelectionChanged();

				}
			});

			this.getSelectEpisodeLabel().setVisible(false);
			this.getDownloadInHD().setVisible(false);

			// This preference has been saved for the user's convenience.
			getDownloadInHD().setSelected(TedConfig.getInstance().isHDDownloadPreference());

			// Get the screen size
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Dimension screenSize = toolkit.getScreenSize();

			this.setSize((int) (screenSize.width * 0.75), (int) (screenSize.height * 0.90));

			// Calculate the frame location
			int x = (screenSize.width - this.getWidth()) / 2;
			int y = (screenSize.height - this.getHeight()) / 2;

			// Set the new frame location
			this.setLocation(x, y);
			this.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read the shownames from the xml file
	 * 
	 * @return Vector with names
	 */
	private Vector<SimpleTedSerie> readShowNames() {
		Vector<SimpleTedSerie> names = new Vector<SimpleTedSerie>();
		//names = TedShowsConfig.getInstance().getNames();
		//TODO retreive serie

		return names;
	}

	private JScrollPane getShowsScrollPane() {
		if (showsScrollPane == null) {
			showsScrollPane = new JScrollPane();

		}
		return showsScrollPane;
	}

	/**
	 * Called whenever the selection of a show is changed in the dialog
	 */
	private void showsTableSelectionChanged() {
		// disable ok button
		this.okButton.setEnabled(false);

		// get the selected show
		int selectedRow = showsTable.getSelectedRow();

		if (selectedRow >= 0) {
			// get the simple info of the show
			SimpleTedSerie selectedShow = this.showsTableModel.getSerieAt(selectedRow);

			if (this.selectedShow == null || !(this.selectedShow.getName().equals(selectedShow.getName()))) {
				this.getSelectEpisodeLabel().setVisible(true);
				this.getDownloadInHD().setVisible(true);

				this.selectedShow = selectedShow;

				this.showNameLabel.setText(selectedShow.getName());

				this.episodeChooserPanel.setVisible(false);

				TedSerie selectedSerie = new TedSerie();//TODO get selected serie info .getInstance().getSerie(selectedShow.getName());

				// create a new infoPane to (correctly) show the information
				showInfoPane = null;
				showInfoScrollPane.setViewportView(this.getShowInfoPane());

				// kill running threads

				if (episodeParserThread != null && episodeParserThread.isAlive()) {
					episodeParserThread.done();
				}
				if (showInfoThread != null && showInfoThread.isAlive()) {
					showInfoThread.done();
				}

				// retrieve the show info and the episodes from the web
				showInfoThread = new ShowInfoThread(this.getShowInfoPane(), selectedSerie);

				episodeParserThread = new EpisodeParserThread(this.episodeChooserPanel, selectedSerie, this.subscribeOptionsPanel);

				showInfoThread.start();
				episodeParserThread.start();

				// set the selected show
				this.setSelectedSerie(selectedSerie);
			}
		}
	}

	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText(Lang.getString("TedGeneral.ButtonAdd"));
			okButton.setActionCommand("OK");
			okButton.addActionListener(this);
			this.getRootPane().setDefaultButton(okButton);
			this.okButton.setEnabled(false);
		}
		return okButton;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText(Lang.getString("TedGeneral.ButtonCancel"));
			cancelButton.setActionCommand("Cancel");
			cancelButton.addActionListener(this);
		}
		return cancelButton;
	}

	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();

		if (command.equals("OK")) {
			this.addShow();
		} else if (command.equals("Cancel")) {
			this.close();
		} else if (command.equals("Help")) {
			try {
				// open the help page of ted
				BrowserLauncher.openURL("http://www.ted.nu/wiki/index.php/Add_show"); //$NON-NLS-1$
			} catch (Exception err) {

			}
		} else if (command.equals("addempty")) {
			// create an edit show dialog with an empty show and hide add show
			// dialog
			TedSerie temp = new TedSerie();
			// assume user wants to add the show he searched for
			temp.setName(getJSearchField().getText());
			this.close();
			new EditShowDialog(tedMain, temp, true);
		} else if (command.equals("search")) {
			this.searchShows(getJSearchField().getText());
		} else if (command.equals("switch")) {
			episodeChooserPanel.setVisible(!episodeChooserPanel.isVisible());
		} else if (command.equals("")) {
			// possible a reset on the search box
			// check (even though this is a little ugly, i dont know a better
			// way to do this)
			if (event.getSource().toString().contains("cancel")) {
				this.getJSearchField().setText("");
				this.searchShows(getJSearchField().getText());
			}
		}
	}

	/**
	 * Add the selected show with the selected season/episode to teds show list
	 */
	private void addShow() {
		// add show
		if (selectedSerie != null) {
			StandardStructure selectedEpisode = this.subscribeOptionsPanel.getSelectedEpisode();
			selectedSerie.setCurrentEpisode(selectedEpisode);
			selectedSerie.updateShowStatus();

			// Handle HD episodes.
			boolean downloadSerieInHD = downloadInHD.isSelected();
			selectedSerie.setDownloadInHD(downloadSerieInHD);
			if (downloadSerieInHD) {
				selectedSerie.setMinSize(selectedSerie.getMinSize() * 2);
				selectedSerie.setMaxSize(selectedSerie.getMaxSize() * 2);
			}

			// add the serie
			tedMain.addSerie(selectedSerie);

			this.close();
		}
	}

	private void close() {
		// Save this preference for the next time.
		TedConfig.getInstance().setHDDownloadPreference(this.getDownloadInHD().isSelected());
		// TODO save pref

		this.showsTableModel.removeSeries();
		this.episodeChooserPanel.clear();
		// close the dialog
		this.setVisible(false);
		this.dispose();
	}

	public void setSelectedSerie(TedSerie selectedSerie2) {
		this.selectedSerie = selectedSerie2;
	}

	private JScrollPane getShowInfoScrollPane() {
		if (showInfoScrollPane == null) {
			showInfoScrollPane = new JScrollPane();
			showInfoScrollPane.setViewportView(getShowInfoPane());
			showInfoScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		}
		return showInfoScrollPane;
	}

	private JTextPane getShowInfoPane() {
		if (showInfoPane == null) {
			showInfoPane = new JTextPane();
			showInfoPane.setContentType("text/html");
			showInfoPane.setEditable(false);

			String startHTML = "<html><font face=\"Arial, Helvetica, sans-serif\">";
			String endHTML = "</font></html>";
			showInfoPane.setText(startHTML + Lang.getString("TedAddShowDialog.ShowInfo.PickAShow") + endHTML);

			showInfoPane.setPreferredSize(new java.awt.Dimension(475, 128));

			// Set up the JEditorPane to handle clicks on hyperlinks
			showInfoPane.addHyperlinkListener(new HyperlinkListener() {
				public void hyperlinkUpdate(HyperlinkEvent e) {
					// Handle clicks; ignore mouseovers and other link-related
					// events
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						// Get the HREF of the link and display it.
						try {
							BrowserLauncher.openURL(e.getDescription());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
						}
					}
				}
			});

		}
		return showInfoPane;
	}

	private JButton getJHelpButton() {
		if (jHelpButton == null) {
			jHelpButton = new JButton();
			jHelpButton.setActionCommand("Help");
			if (!TedSystemInfo.osIsMacLeopardOrBetter()) {
				jHelpButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/help.png")));
			}
			jHelpButton.setBounds(11, 380, 28, 28);
			jHelpButton.addActionListener(this);
			jHelpButton.putClientProperty("JButton.buttonType", "help");
			jHelpButton.setToolTipText(Lang.getString("TedGeneral.ButtonHelpToolTip"));
		}
		return jHelpButton;
	}

	private JLabel getSelectShowLabel() {
		if (selectShowLabel == null) {
			selectShowLabel = new JLabel();
			selectShowLabel.setText(Lang.getString("TedAddShowDialog.LabelSelectShow"));
		}
		return selectShowLabel;
	}

	private JLabel getSelectEpisodeLabel() {
		if (selectEpisodeLabel == null) {
			selectEpisodeLabel = new JLabel();
			selectEpisodeLabel.setText(Lang.getString("TedAddShowDialog.LabelSelectEpisode"));
		}
		return selectEpisodeLabel;
	}

	private JLabel getShowNameLabel() {
		if (showNameLabel == null) {
			showNameLabel = new JLabel();
			showNameLabel.setFont(new java.awt.Font("Dialog", 1, 25));

		}
		return showNameLabel;
	}

	private JButton getButtonAddEmptyShow() {
		if (buttonAddEmptyShow == null) {
			buttonAddEmptyShow = new JButton();
			buttonAddEmptyShow.setText(Lang.getString("TedAddShowDialog.ButtonAddCustomShow"));
			buttonAddEmptyShow.addActionListener(this);
			buttonAddEmptyShow.setActionCommand("addempty");
		}
		return buttonAddEmptyShow;
	}

	public void episodeSelectionChanged() {
		StandardStructure selectedStructure = episodeChooserPanel.getSelectedStructure();
		this.subscribeOptionsPanel.setCustomEpisode(selectedStructure);
	}

	/**
	 * @see ted.interfaces.EpisodeChooserListener#doubleClickOnEpisodeList()
	 */
	public void doubleClickOnEpisodeList() {
		// add show
		this.addShow();
	}

	private JTextField getJSearchField() {
		if (jSearchField == null) {
			jSearchField = new SearchTextField();
			jSearchField.addKeyListener(this);
			jSearchField.putClientProperty("JTextField.Search.CancelAction", this);
		}
		return jSearchField;
	}

	private void searchShows(String searchString) {
		// Only search if we've entered a search term
		if (!searchString.equals("<SEARCH>")) {
			Vector<SimpleTedSerie> tempShows = new Vector<SimpleTedSerie>();

			// If we've entered a search term filter the list, otherwise
			// display all shows
			if (!searchString.equals("")) {
				// Do the filtering
				for (int show = 0; show < allShows.size(); ++show) {
					SimpleTedSerie serie = allShows.get(show);

					if (serie.getName().toLowerCase().contains(searchString.toLowerCase())) {
						tempShows.add(serie);
					}
				}
				// Update the table
				showsTableModel.setSeries(tempShows);

				if (tempShows.size() == 1) {
					// if only a single show is left: select it
					ListSelectionModel selectionModel = showsTable.getSelectionModel();
					selectionModel.setSelectionInterval(0, 0);

				}
			} else {
				showsTableModel.setSeries(allShows);
			}

			// Let the table know that there's new information
			showsTableModel.fireTableDataChanged();
		}
	}

	public void keyPressed(KeyEvent arg0) {
	}

	public void keyReleased(KeyEvent arg0) {
		searchShows(jSearchField.getText());
	}

	public void keyTyped(KeyEvent arg0) {
	}

	public void subscribeOptionChanged() {
		// called when episode selection is changed.
		// check if episode and show selected
		if (selectedSerie != null && this.subscribeOptionsPanel.getSelectedEpisode() != null) {
			// enable add button
			this.okButton.setEnabled(true);
		} else {
			this.okButton.setEnabled(false);
		}
	}

	public void setEpisodeChooserVisible(boolean b) {
		this.episodeChooserPanel.setVisible(b);

	}

	private JCheckBox getDownloadInHD() {
		if (downloadInHD == null) {
			downloadInHD = new JCheckBox();
			downloadInHD.setText(Lang.getString("TedAddShowDialog.DownloadInHd"));
		}
		return downloadInHD;
	}
}
