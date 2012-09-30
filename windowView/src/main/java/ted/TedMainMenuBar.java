package ted;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import ted.view.TedMainDialog;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
/**
 * @author roel
 * Main menu bar for Ted
 */
public class TedMainMenuBar extends JMenuBar
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8573600372750247532L;
	// menu items
	private JMenuItem helpMenuItem;
	private JMenu menuHelp;
	private JMenuItem deleteMenuItem;
	private JMenuItem editMenuItem;
	private JSeparator jSeparator1;
	private JMenuItem preferencesMenuItem;
	private JMenuItem logMenuItem;
	private JMenu menuEdit;
	private JMenuItem exitMenuItem;
	private JMenuItem newFileMenuItem;
	private JMenu menuFile;
	private JMenuItem exportMenuItem;
	private JMenuItem importMenuItem;
	private JMenuItem menuParse;
	private JMenuItem menuEnable;
	private JMenuItem menuDisable;
	private JMenuItem versionItem;
	private JMenuItem webItem;
	private JMenuItem synchronizeMenuItem;
	private JMenuItem RSSItem;
	private JMenuItem aboutItem;
	private JMenuItem translateItem;
	private JMenuItem languageItem;
	private JMenu extraMenu;
	
	private JMenu subUpdateMenu;
	private JCheckBoxMenuItem sortDescendingRadioItem;
	private JCheckBoxMenuItem sortAscendingRadioItem;
	private JSeparator jSeparator3;
	private JCheckBoxMenuItem sortOnStatusItem;
	private JCheckBoxMenuItem sortOnNameItem;
	private JMenu sortMenuItem;
	private JSeparator jSeparator2;
	private JSeparator jSeparator6;
	private JMenu subLangMenu;
	
	private TedMainDialog tMain;
	private JMenuItem menuDisableAutoSchedule;
	private JMenuItem menuEnableAutoSchedule;
	private JMenuItem editAllShowsMenuItem;
	private JSeparator jSeparator7;
	
	/**
	 * Main menubar for ted
	 * @param main MainDialog that this bar is added to
	 */
	public TedMainMenuBar(TedMainDialog main)
	{
		this.tMain = main;
		
		this.initMenu();
	}
	
	public void initMenu()
	{
//		 File Menu
		menuFile = new JMenu();
		this.add(menuFile);
		
		{
			newFileMenuItem = new JMenuItem();
			exportMenuItem	= new JMenuItem();
			importMenuItem  = new JMenuItem();
			
			// add show
			menuFile.add(newFileMenuItem);
			
			newFileMenuItem.addActionListener(tMain);
			newFileMenuItem.setActionCommand("New"); //$NON-NLS-1$
			
			// separate
			jSeparator1 = new JSeparator();
			menuFile.add(jSeparator1);
			
			menuFile.add(importMenuItem);
			importMenuItem.setActionCommand("Import");
			importMenuItem.addActionListener(tMain);
			
			menuFile.add(exportMenuItem);			
			exportMenuItem.setActionCommand("Export"); //$NON-NLS-1$
			exportMenuItem.addActionListener(tMain);
						
			if (!TedSystemInfo.osIsMac())
			{
				// seperate
				jSeparator1 = new JSeparator();
				menuFile.add(jSeparator1);
				// exit
				exitMenuItem = new JMenuItem();
				menuFile.add(exitMenuItem);
				
				exitMenuItem.setActionCommand("Exit");
				exitMenuItem.addActionListener(tMain);
			}
		}
		// Edit Menu
		menuEdit = new JMenu();
		this.add(menuEdit);
		
		{
			// edit all shows
			editAllShowsMenuItem = new JMenuItem();
			menuEdit.add(editAllShowsMenuItem);
			
			editAllShowsMenuItem.setActionCommand("editAllShows");
			editAllShowsMenuItem.addActionListener(tMain);
			
			jSeparator7 = new JSeparator();
			menuEdit.add(jSeparator7);
			
			// edit..
			editMenuItem = new JMenuItem();
			menuEdit.add(editMenuItem);
			
			editMenuItem.setActionCommand("Edit"); //$NON-NLS-1$
			editMenuItem.addActionListener(tMain);
			
			// delete
			deleteMenuItem = new JMenuItem();
			menuEdit.add(deleteMenuItem);
			
			deleteMenuItem.setActionCommand("Delete");
			deleteMenuItem.addActionListener(tMain);
			
			menuParse = new JMenuItem(); //$NON-NLS-1$
			menuParse.addActionListener(tMain);
			menuParse.setActionCommand("parse selected"); //$NON-NLS-1$
			menuEdit.add(menuParse);
			
			jSeparator6 = new JSeparator();
			menuEdit.add(jSeparator6);
			
			menuEnable = new JMenuItem (); //$NON-NLS-1$
			menuEnable.addActionListener(tMain);
			menuEnable.setActionCommand("setstatusenabled"); //$NON-NLS-1$
			menuEdit.add(menuEnable);
			
			menuDisable = new JMenuItem (); //$NON-NLS-1$
			menuDisable.addActionListener(tMain);
			menuDisable.setActionCommand("setstatusdisabled"); //$NON-NLS-1$
			menuEdit.add(menuDisable);
			
			menuEnableAutoSchedule = new JMenuItem (); //$NON-NLS-1$
			menuEnableAutoSchedule.addActionListener(tMain);
			menuEnableAutoSchedule.setActionCommand("setautoscheduleenabled"); //$NON-NLS-1$
			menuEdit.add(menuEnableAutoSchedule);
			
			menuDisableAutoSchedule = new JMenuItem (); //$NON-NLS-1$
			menuDisableAutoSchedule.addActionListener(tMain);
			menuDisableAutoSchedule.setActionCommand("setautoscheduledisabled"); //$NON-NLS-1$
			menuEdit.add(menuDisableAutoSchedule);
			
			
		}
		{
		}
		
		// Extra menu
		extraMenu = new JMenu();
		this.add(extraMenu);
		{
			
			sortMenuItem = new JMenu();
			extraMenu.add(sortMenuItem);
			sortMenuItem.setText("Sort shows");
			{
				sortOnStatusItem = new JCheckBoxMenuItem();
				sortMenuItem.add(sortOnStatusItem);
				sortOnStatusItem.setText("Sort on status and airdate");
				sortOnStatusItem.addActionListener(tMain);
				sortOnStatusItem.setActionCommand("sort_status");
			}
			{
				sortOnNameItem = new JCheckBoxMenuItem();
				sortMenuItem.add(sortOnNameItem);
				sortOnNameItem.setText("Sort on name");
				sortOnNameItem.addActionListener(tMain);
				sortOnNameItem.setActionCommand("sort_name");
			}
			{
				jSeparator3 = new JSeparator();
				sortMenuItem.add(jSeparator3);
			}
			{
				sortAscendingRadioItem = new JCheckBoxMenuItem();
				sortMenuItem.add(sortAscendingRadioItem);
				sortAscendingRadioItem.setText("Sort ascending");
				sortAscendingRadioItem.addActionListener(tMain);
				sortAscendingRadioItem.setActionCommand("sort_ascending");
			}
			{
				sortDescendingRadioItem = new JCheckBoxMenuItem();
				sortMenuItem.add(sortDescendingRadioItem);
				sortDescendingRadioItem.setText("Sort descending");
				sortDescendingRadioItem.addActionListener(tMain);
				sortDescendingRadioItem.setActionCommand("sort_descending");
				
				jSeparator2 = new JSeparator();
				extraMenu.add(jSeparator2);
			}
		}
		// show log	
		logMenuItem = new JMenuItem();
		extraMenu.add(logMenuItem);
		logMenuItem.setText(Lang.getString("TedMainMenuBar.Edit.ShowLog")); //$NON-NLS-1$
		logMenuItem.setActionCommand("Log"); //$NON-NLS-1$
		logMenuItem.addActionListener(tMain);
		
		if (!TedSystemInfo.osIsMac())
		{
			jSeparator1 = new JSeparator();
			extraMenu.add(jSeparator1);
			// preferences
			preferencesMenuItem = new JMenuItem();
			extraMenu.add(preferencesMenuItem);
			preferencesMenuItem.setText(Lang.getString("TedMainMenuBar.Edit.Preferences")); //$NON-NLS-1$
			preferencesMenuItem.setActionCommand("Preferences...");
			preferencesMenuItem.addActionListener(tMain);
		}
		
		// Help Menu
		menuHelp = new JMenu();
		this.add(menuHelp);
		{
			helpMenuItem = new JMenuItem();
			// help
			menuHelp.add(helpMenuItem);
			helpMenuItem.addActionListener(tMain);
			helpMenuItem.setActionCommand("help"); //$NON-NLS-1$
			menuHelp.add(helpMenuItem);
			// Seperator
			jSeparator1 = new JSeparator();
			menuHelp.add(jSeparator1);
			// update submenus
			subUpdateMenu = new JMenu();
			subLangMenu = new JMenu();
			menuHelp.add(subUpdateMenu);
			menuHelp.add(subLangMenu);
			// Check for updated
			versionItem = new JMenuItem();
			subUpdateMenu.add(versionItem);
			versionItem.addActionListener(tMain);
			versionItem.setActionCommand("checkupdates"); //$NON-NLS-1$
			// Check RSS updates
			RSSItem = new JMenuItem();
			subUpdateMenu.add(RSSItem);
			RSSItem.addActionListener(tMain);
			RSSItem.setActionCommand("checkRSS"); //$NON-NLS-1$
			// Synchronize show.xml with current shows
			synchronizeMenuItem = new JMenuItem();
			subUpdateMenu.add(synchronizeMenuItem);
			synchronizeMenuItem.addActionListener(tMain);
			synchronizeMenuItem.setActionCommand("synchronize"); //$NON-NLS-1$
			// Translate ted
			translateItem = new JMenuItem();
			subLangMenu.add(translateItem);
			translateItem.addActionListener(tMain);
			translateItem.setActionCommand("translate");
			// Get latest language version
			languageItem = new JMenuItem();
			subLangMenu.add(languageItem);
			languageItem.addActionListener(tMain);
			languageItem.setActionCommand("language");
			// seperate
			jSeparator1 = new JSeparator();
			menuHelp.add(jSeparator1);
			// open ted website
			webItem = new JMenuItem();
			webItem.setActionCommand("opensite"); //$NON-NLS-1$
			webItem.addActionListener(tMain);
			menuHelp.add(webItem);
			if (!TedSystemInfo.osIsMac())
			{
				// sperate
				jSeparator1 = new JSeparator();
				menuHelp.add(jSeparator1);
				// about ted
				aboutItem = new JMenuItem();
				menuHelp.add(aboutItem);
				aboutItem.setText(Lang.getString("TedMainMenuBar.Help.AboutTed")); //$NON-NLS-1$
				aboutItem.setActionCommand("About ted"); //$NON-NLS-1$
				aboutItem.addActionListener(tMain);
			}
		}
		
		this.updateText();
		this.updateSortMenu();
	}
	
	public void updateText()
	{
		menuFile.setText(Lang.getString("TedMainMenuBar.File")); //$NON-NLS-1$
		newFileMenuItem.setText(Lang.getString("TedMainMenuBar.File.NewShow")); //$NON-NLS-1$
		exportMenuItem.setText(Lang.getString("TedMainMenuBar.File.ExportShows")); //$NON-NLS-1$
		importMenuItem.setText(Lang.getString("TedMainMenuBar.File.ImportShows")); //$NON-NLS-1$
		if (!TedSystemInfo.osIsMac())
		{
			exitMenuItem.setText(Lang.getString("TedMainMenuBar.File.Exit")); //$NON-NLS-1$
			preferencesMenuItem.setText(Lang.getString("TedMainMenuBar.Edit.Preferences")); //$NON-NLS-1$
			aboutItem.setText(Lang.getString("TedMainMenuBar.Help.AboutTed")); //$NON-NLS-1$
		}
		
		// edit menu
		menuEdit.setText(Lang.getString("TedMainMenuBar.Edit")); //$NON-NLS-1$
		editMenuItem.setText(Lang.getString("TedMainMenuBar.Edit.Edit")); //$NON-NLS-1$
		deleteMenuItem.setText(Lang.getString("TedMainMenuBar.Edit.Delete")); //$NON-NLS-1$
		menuParse.setText( Lang.getString("TedTablePopupMenu.CheckShow") ); //$NON-NLS-1$
		
		sortMenuItem.setText(Lang.getString("TedMainMenuBar.Edit.Sort"));	
		sortOnStatusItem.setText(Lang.getString("TedMainMenuBar.Edit.Sort.OnStatus"));
		sortOnNameItem.setText(Lang.getString("TedMainMenuBar.Edit.Sort.OnName"));
		sortAscendingRadioItem.setText(Lang.getString("TedMainMenuBar.Edit.Sort.Ascending"));
		sortDescendingRadioItem.setText(Lang.getString("TedMainMenuBar.Edit.Sort.Descending"));
		
		logMenuItem.setText(Lang.getString("TedMainMenuBar.Edit.ShowLog")); //$NON-NLS-1$
		
		// help menu
		menuHelp.setText(Lang.getString("TedMainMenuBar.Help")); //$NON-NLS-1$
		helpMenuItem.setText(Lang.getString("TedMainMenuBar.Help.Help")); //$NON-NLS-1$
	
		versionItem.setText(Lang.getString("TedMainMenuBar.Help.CheckForUpdates")); //$NON-NLS-1$
	
		RSSItem.setText(Lang.getString("TedMainMenuBar.Help.CheckShowDefinitions")); //$NON-NLS-1$
	
		synchronizeMenuItem.setText(Lang.getString("TedMainMenuBar.Help.SynchronizeShows")); //$NON-NLS-1$
	
		translateItem.setText(Lang.getString("TedMainMenuBar.Help.Translate"));
		
		webItem.setText(Lang.getString("TedMainMenuBar.Help.TedOnline")); //$NON-NLS-1$	
	
		subUpdateMenu.setText(Lang.getString("TedMainMenuBar.Help.SubSoftware"));
		
		subLangMenu.setText(Lang.getString("TedMainMenuBar.Help.SubTranslate"));
		
		languageItem.setText(Lang.getString("TedMainMenuBar.Help.LanguageUpdate"));
		
		extraMenu.setText(Lang.getString("TedMainMenuBar.Extra"));
		menuEnable.setText(Lang.getString("TedMainMenuBar.Edit.EnableShow"));
		menuDisable.setText(Lang.getString("TedMainMenuBar.Edit.DisableShow"));
		menuEnableAutoSchedule.setText(Lang.getString("TedMainMenuBar.Edit.EnableAutoSchedule"));
		menuDisableAutoSchedule.setText(Lang.getString("TedMainMenuBar.Edit.DisableAutoSchedule"));
		this.editAllShowsMenuItem.setText(Lang.getString("TedMainMenuBar.Edit.editAllShowsMenuItem"));
	}
	
	/**
	 * Disable all menuitems that can't be used while parsing
	 */
	public void setParsing()
	{
		this.deleteMenuItem.setEnabled(false);
	}
	/**
	 * Enable all menuitems that can be used while idleing.
	 */
	public void setIdle()
	{
		this.deleteMenuItem.setEnabled(true);
	}

	
	/**
	 * Enable all menuitems that can be used while something is selected.
	 * @param b 
	 */
	public void setSomethingSelected(boolean b)
	{
		this.deleteMenuItem.setEnabled(b);
		this.editMenuItem.setEnabled(b);
		this.menuEnable.setEnabled(b);
		this.menuDisable.setEnabled(b);
		this.menuParse.setEnabled(b);
		this.menuDisableAutoSchedule.setEnabled(b);
		this.menuEnableAutoSchedule.setEnabled(b);
	}
	
	public void updateSortMenu()
	{
		// update sort type menu
		boolean isSortOnName = (TedConfig.getInstance().getSortType() == TedConfig.SORT_NAME);
		boolean isSortOnStatus = (TedConfig.getInstance().getSortType() == TedConfig.SORT_STATUS);
		
		this.sortOnNameItem.setSelected(isSortOnName);
		this.sortOnStatusItem.setSelected(isSortOnStatus);
		
		// update sort direction menu
		boolean isSortAscending = (TedConfig.getInstance().getSortDirection() == TedConfig.SORT_ASCENDING);
		boolean isSortDescending = (TedConfig.getInstance().getSortDirection() == TedConfig.SORT_DESCENDING);
		
		this.sortAscendingRadioItem.setSelected(isSortAscending);
		this.sortDescendingRadioItem.setSelected(isSortDescending);
		
	}

	public void checkDisabled(boolean disabled, boolean showBoth) 
	{
		this.menuDisable.setVisible(!disabled || showBoth);
		this.menuEnable.setVisible(disabled || showBoth);
	}

	public void checkAutoSchedule(boolean enabled, boolean showBoth)
	{	
		// use auto schedule?
		boolean useAutoSchedule = TedConfig.getInstance().isUseAutoSchedule();
		
		this.menuDisableAutoSchedule.setVisible((enabled || showBoth) && useAutoSchedule);
		this.menuEnableAutoSchedule.setVisible((!enabled || showBoth) && useAutoSchedule);
	}
}
