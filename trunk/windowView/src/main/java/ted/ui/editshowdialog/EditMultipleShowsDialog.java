package ted.ui.editshowdialog;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import ted.BrowserLauncher;
import ted.Lang;
import ted.TedSerie;
import ted.TedSystemInfo;
import ted.interfaces.PanelSwitcher;
import ted.view.TedMainDialog;

public class EditMultipleShowsDialog extends javax.swing.JDialog implements ActionListener, PanelSwitcher

{
	private int width = 500;
	private int height = 520;
	private int tabsHeight = 370;
	private TedMainDialog tedDialog;
	private TedSerie[] showsList;
	private JPanel jShowTabs;
	private JButton jHelpButton;
	private JButton jButton1;
	private JButton button_Save;
	private FilterPanel filterPanel;
	private String currentTab;
	
	/****************************************************
	 * CONSTRUCTORS
	 ****************************************************/
	/**
	 * Creates a new Edit Show Dialog
	 * @param frame TedMainDialog
	 * @param serie Serie where we have to make this dialog for
	 * @param newSerie Is it a new serie?
	 */
	public EditMultipleShowsDialog(TedMainDialog frame, TedSerie[] shows) 
	{
		this.setModal(true);
		this.setResizable(false);
		this.tedDialog = frame;
		this.showsList = shows;
		this.initGUI();
	}

	private void initGUI() 
	{
		try 
		{
			this.setSize(width, height);

		    //Get the screen size
		    Toolkit toolkit = Toolkit.getDefaultToolkit();
		    Dimension screenSize = toolkit.getScreenSize();

		    //Calculate the frame location
		    int x = (screenSize.width - this.getWidth()) / 2;
		    int y = (screenSize.height - this.getHeight()) / 2;

		    //Set the new frame location
		    this.setLocation(x, y);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		initMoreGui();
		
	}

	private void initMoreGui() 
	{
		// posistion of ok and cancel button for mac and linux
		int bottomButtonLocationY 	= this.height - 60;
		int bottomButtonOkX 		= this.width - 110;
		int bottomButtonCancelX 	= this.width - 220;
		int bottomButtonHelpX		= 11;
		
		// compute x and y location of buttons on windows
		if (TedSystemInfo.osIsWindows())
		{
			bottomButtonLocationY 	= this.height - 75;
			bottomButtonOkX 		= this.width - 225;
			bottomButtonCancelX 	= this.width - 120;		
		}
		
		this.getContentPane().setLayout(null);
		
		this.setTitle(Lang.getString("TedEpisodeDialog.WindowTitleEdit"));
		
		jShowTabs = new JPanel(new CardLayout());
		getContentPane().add(jShowTabs);
		jShowTabs.setBounds(0, 75, width, this.tabsHeight);
		
		EditShowToolBar toolBarPanel = new EditShowToolBar(this);
		getContentPane().add(toolBarPanel);
		toolBarPanel.setBounds(0, 0, width, 70);
		
		toolBarPanel.setBackground(Color.WHITE);

		jHelpButton = new JButton();
		getContentPane().add(jHelpButton);
		if (!TedSystemInfo.osIsMacLeopardOrBetter())
		{
			jHelpButton.setIcon(new ImageIcon(getClass()
					.getClassLoader().getResource("icons/help.png")));
		}
		jHelpButton.putClientProperty("JButton.buttonType", "help");
		jHelpButton.setActionCommand("Help");
		jHelpButton.setBounds(bottomButtonHelpX, bottomButtonLocationY, 28, 28);
		jHelpButton.addActionListener(this);
		jHelpButton.setToolTipText(Lang.getString("TedGeneral.ButtonHelpToolTip"));
		{
			jButton1 = new JButton();
			getContentPane().add(jButton1);
			jButton1.setActionCommand("cancel");
			jButton1.setText(Lang.getString("TedGeneral.ButtonCancel"));
			jButton1.setBounds(bottomButtonCancelX, bottomButtonLocationY, 100, 28);
			jButton1.addActionListener(this);
		}
		{
			button_Save = new JButton();
			getContentPane().add(button_Save);
			button_Save.setActionCommand("save");

				button_Save.setText(Lang.getString("TedGeneral.ButtonSave"));
			button_Save.setBounds(bottomButtonOkX, bottomButtonLocationY, 98, 28);
			button_Save.addActionListener(this);
			this.getRootPane().setDefaultButton(button_Save);
		}
		
	
		filterPanel = new FilterPanel();
		jShowTabs.add(EditShowDialog.FILTERCOMMAND, filterPanel);
		filterPanel.setValues(this.showsList);
		filterPanel.setSize(this.width, this.tabsHeight);
		
		this.currentTab = EditShowDialog.FILTERCOMMAND;
		
		this.setVisible(true);
		
	}

	public void actionPerformed(ActionEvent arg0) 
	{
		String action = arg0.getActionCommand();
		if (action.equals("save"))
		{
			// save the values from the different tabs in the serie
			if (this.saveShow(this.showsList))
			{
				// close the dialog		
				this.setVisible(false);
				this.dispose();
								
				// save the changed shows
				tedDialog.saveShows();
			}
		}
		else if (action.equals("cancel"))
		{
			// close the dialog
			this.setVisible(false);
			this.dispose();
		}
		else if (action.equals("Help"))
		{
			String wikiUrl = "http://www.ted.nu/wiki/index.php/";
						
			if (this.currentTab.equals(EditShowDialog.GENERALCOMMAND))
			{
				wikiUrl += "General";
			}
			else if (this.currentTab.equals(EditShowDialog.FEEDSCOMMAND))
			{
				wikiUrl += "Feeds";
			}
			else if (this.currentTab.equals(EditShowDialog.FILTERCOMMAND))
			{
				wikiUrl += "Filters";
			}
			else if (this.currentTab.equals(EditShowDialog.SCHEDULECOMMAND))
			{
				wikiUrl += "Schedulers";
			}
			// launch documentation website
			try 
			{
				BrowserLauncher.openURL(wikiUrl); //$NON-NLS-1$
			} 
			catch (Exception err)
			{
				
			}
		}		
	}

	private boolean saveShow(TedSerie[] showsList2) 
	{
		if (this.filterPanel.checkValues())
		{
			this.filterPanel.saveValues(showsList2);
			return true;
		}
		return false;
	}

	/**
	 * Show a specific panel
	 * @param command
	 */
	public void showPanel(String command)
	{
		CardLayout cl = (CardLayout)(jShowTabs.getLayout());
	    cl.show(jShowTabs, command);		
	    this.currentTab = command;
	}
}
