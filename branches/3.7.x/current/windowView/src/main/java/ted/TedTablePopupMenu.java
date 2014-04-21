package ted;

/****************************************************
 * IMPORTS
 ****************************************************/

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import ted.view.TedMainDialog;

/**
 * TED: Torrent Episode Downloader (2005 - 2006)
 * 
 * This is popup menu that we show when a user clicks the right button on the table
 * in the mainwindow of ted
 * 
 * @author Roel
 * @author Joost
 *
 * ted License:
 * This file is part of ted. ted and all of it's parts are licensed
 * under GNU General Public License (GPL) version 2.0
 * 
 * for more details see: http://en.wikipedia.org/wiki/GNU_General_Public_License
 *
 */

public class TedTablePopupMenu extends JPopupMenu
{
	/****************************************************
	 * GLOBAL VARIABLES
	 ****************************************************/
	private static final long serialVersionUID = 6924907434718003055L;
	private TedMainDialog mainDialog;
	private JMenuItem menuDelete;
	private JMenuItem menuEdit;
	private JMenuItem buyDVD;
	private JMenuItem menuParse;
	private JMenuItem menuEnableShow;
	private JMenuItem menuDisableShow;
	private JMenuItem menuEnableAutoSchedule;
	private JMenuItem menuDisableAutoSchedule;

	/****************************************************
	 * CONSTRUCTORS
	 ****************************************************/
	/**
	 * Create a new table popup menu
	 * @param main Mainwindow where we want to add the popup menu
	 */
	public TedTablePopupMenu(TedMainDialog main)
	{
		this.mainDialog = main;
		// Create some menu items for the popup
		menuEdit = new JMenuItem(); //$NON-NLS-1$
		menuEdit.addActionListener(mainDialog);
		menuEdit.setActionCommand("Edit"); //$NON-NLS-1$
		
		menuDelete = new JMenuItem(); //$NON-NLS-1$
		menuDelete.addActionListener(mainDialog);
		menuDelete.setActionCommand("Delete"); //$NON-NLS-1$
		
		menuParse = new JMenuItem(); //$NON-NLS-1$
		menuParse.addActionListener(mainDialog);
		menuParse.setActionCommand("parse selected"); //$NON-NLS-1$
		
		
		menuEnableShow = new JMenuItem (); //$NON-NLS-1$
		menuEnableShow.addActionListener(mainDialog);
		menuEnableShow.setActionCommand("setstatusenabled"); //$NON-NLS-1$
		
		menuDisableShow = new JMenuItem (); //$NON-NLS-1$
		menuDisableShow.addActionListener(mainDialog);
		menuDisableShow.setActionCommand("setstatusdisabled"); //$NON-NLS-1$
		
		buyDVD = new JMenuItem ();
		buyDVD.addActionListener(mainDialog);
		buyDVD.setActionCommand("buyDVDselectedshow");
			
		menuEnableAutoSchedule = new JMenuItem (); //$NON-NLS-1$
		menuEnableAutoSchedule.addActionListener(mainDialog);
		menuEnableAutoSchedule.setActionCommand("setautoscheduleenabled"); //$NON-NLS-1$
		
		menuDisableAutoSchedule = new JMenuItem (); //$NON-NLS-1$
		menuDisableAutoSchedule.addActionListener(mainDialog);
		menuDisableAutoSchedule.setActionCommand("setautoscheduledisabled"); //$NON-NLS-1$
		
		JSeparator separator = new JSeparator();
		JSeparator separator2 = new JSeparator();
		
		this.add( menuEdit );
		this.add( menuDelete );
		this.add( menuParse );
		this.add(separator);
		this.add(menuEnableAutoSchedule);
		this.add(menuDisableAutoSchedule);
		this.add(menuEnableShow);
		this.add(menuDisableShow);
		this.add(separator2);
		this.add( buyDVD);
		
		this.updateText();
	}
	
	/**
	 * Update text of the status menu
	 */
	public void updateText()
	{
		menuEdit.setText(Lang.getString("TedMainMenuBar.Edit.Edit")); //$NON-NLS-1$
		menuDelete.setText( Lang.getString("TedMainMenuBar.Edit.Delete") ); //$NON-NLS-1$
		menuParse.setText( Lang.getString("TedTablePopupMenu.CheckShow") ); //$NON-NLS-1$
		buyDVD.setText(Lang.getString("TedTablePopupMenu.BuyDVD"));	
		menuEnableShow.setText(Lang.getString("TedMainMenuBar.Edit.EnableShow"));
		menuDisableShow.setText(Lang.getString("TedMainMenuBar.Edit.DisableShow"));
		menuEnableAutoSchedule.setText(Lang.getString("TedMainMenuBar.Edit.EnableAutoSchedule"));
		menuDisableAutoSchedule.setText(Lang.getString("TedMainMenuBar.Edit.DisableAutoSchedule"));
	}
	
	/**
	 * Disable all menuitems that can't be used while parsing
	 */
	public void setParsing()
	{
		this.menuDelete.setEnabled(false);
	}
	/**
	 * Enable all menuitems that can be used while idleing.
	 */
	public void setIdle()
	{
		this.menuDelete.setEnabled(true);
	}

	public void checkAutoSchedule(boolean enabled, boolean showBoth)
	{		
		// use auto schedule?
		boolean useAutoSchedule = TedConfig.getInstance().isUseAutoSchedule();
		
		this.menuDisableAutoSchedule.setVisible((enabled || showBoth) && useAutoSchedule);
		this.menuEnableAutoSchedule.setVisible((!enabled || showBoth) && useAutoSchedule);
	}

	public void checkDisabled(boolean disabled, boolean showBoth) 
	{
		this.menuDisableShow.setVisible(!disabled || showBoth);
		this.menuEnableShow.setVisible(disabled || showBoth);
	}
}
