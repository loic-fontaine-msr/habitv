package ted;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.jdesktop.jdic.init.JdicManager;
import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;

public class TedTrayIcon implements ted.TrayIcon
{
	
	private SystemTray tray;
	private TrayIcon tedTray;
	private JdicManager jDicManager;
	private JMenuItem trayParse;
	private JMenuItem trayShow;
	private JMenuItem trayExit;
	
	public TedTrayIcon(TedMainDialog tedMain, ImageIcon icon) throws Exception
	{
		try
		{
			jDicManager = JdicManager.getManager();
			jDicManager.initShareNative();
			tray = SystemTray.getDefaultSystemTray();
			tedTray = new TrayIcon(icon, "ted"); //$NON-NLS-1$
			
			
			// initizlaize the trayicon
			tedTray.addActionListener(tedMain);
		    tray.addTrayIcon(tedTray);
		    // TODO: make tedtraymenu class for this
		    // add menu to trayicon   
		    JPopupMenu traymenu = new JPopupMenu();
		    trayParse = new JMenuItem();
		    traymenu.add(trayParse);
	
			trayParse.setActionCommand("Parse"); //$NON-NLS-1$
			trayParse.addActionListener(tedMain);
			// show ted
			trayShow = new JMenuItem();
		    traymenu.add(trayShow);
	
			trayShow.setActionCommand("PressAction"); //$NON-NLS-1$
			trayShow.addActionListener(tedMain);
			JSeparator jSeparator1 = new JSeparator();
			traymenu.add(jSeparator1);
		    
		    trayExit = new JMenuItem();
		    traymenu.add(trayExit);
			
			trayExit.setActionCommand("Exit"); //$NON-NLS-1$
			trayExit.addActionListener(tedMain);
			
			tedTray.setIconAutoSize(true);
			
			tedTray.setPopupMenu(traymenu);
			
			this.updateText();
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see ted.TrayIcon#setIcon(javax.swing.ImageIcon)
	 */
	public void setIcon(ImageIcon icon)
	{
		tedTray.setIcon(icon);		
	}

	/* (non-Javadoc)
	 * @see ted.TrayIcon#displayMessage(java.lang.String, java.lang.String, int)
	 */
	public void displayMessage(String header, String message, int i)
	{
		// TODO Auto-generated method stub
		tedTray.displayMessage(header, message, i);
		
	}
	
	/* (non-Javadoc)
	 * @see ted.TrayIcon#updateText()
	 */
	public void updateText()
	{
		trayParse.setText(Lang.getString("TedMainDialog.TrayMenuParse")); //$NON-NLS-1$
		trayShow.setText(Lang.getString("TedMainDialog.TrayMenuShowTed")); //$NON-NLS-1$
		trayExit.setText(Lang.getString("TedMainDialog.TrayMenuExit")); //$NON-NLS-1$
		
	}

}
