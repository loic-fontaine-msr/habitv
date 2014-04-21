package ted.ui.configdialog;

/****************************************************
 * IMPORTS
 ****************************************************/
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ted.BrowserLauncher;

/**
 * TED: Torrent Episode Downloader (2005 - 2006)
 * 
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
public class ConfigDialogListener implements ActionListener 
{
	/****************************************************
	 * GLOBAL VARIABLES
	 ****************************************************/
	private ConfigDialog TedCD;

	/****************************************************
	 * CONSTRUCTOR
	 ****************************************************/
	/**
	 * Create a new ConfigListener
	 * @param dialog The dialog to listen to
	 */
	public ConfigDialogListener(ConfigDialog dialog) 
	{
		TedCD = dialog;
	}

	/****************************************************
	 * PUBLIC METHODS
	 ****************************************************/
	public void actionPerformed(ActionEvent e) 
	{
		String action = e.getActionCommand();
		
		if(action.equals("Save"))
		{
			TedCD.saveConfig();
		}
		else if(action.equals("Cancel"))
		{
			TedCD.setVisible(false);
			TedCD.dispose();
		}
		else if(action.equals("Help"))
		{
			String wikiUrl = "http://www.ted.nu/wiki/index.php/";
			
			if (TedCD.getCurrentTab().equals(TedCD.COMMANDGENERAL))
			{
				wikiUrl += "Config_General";
			}
			else if (TedCD.getCurrentTab().equals(TedCD.COMMANDLOOKNFEEL))
			{
				wikiUrl += "Config_Look_And_Feel";
			}
			else if (TedCD.getCurrentTab().equals(TedCD.COMMANDADVANCED))
			{
				wikiUrl += "Config_Advanced";
			}
			else if (TedCD.getCurrentTab().equals(TedCD.COMMANDUPDATES))
			{
				wikiUrl += "Config_Software_Updates";
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

}
