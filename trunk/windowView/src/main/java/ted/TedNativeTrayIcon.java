package ted;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

import javax.swing.ImageIcon;

import ted.view.TedMainDialog;


public class TedNativeTrayIcon implements ted.TrayIcon
{

	private SystemTray tray;
	private TrayIcon tedTray;
	private MenuItem trayParse;
	private MenuItem trayShow;
	private MenuItem trayExit;

	public TedNativeTrayIcon(TedMainDialog tedMain, ImageIcon imageIcon) throws Exception
	{
		try
		{
			tray = SystemTray.getSystemTray();
			tedTray = new TrayIcon(imageIcon.getImage(), "ted"); //$NON-NLS-1$


			// initizlaize the trayicon
			tedTray.addActionListener(tedMain);
			tray.add(tedTray);
			// TODO: make tedtraymenu class for this
			// add menu to trayicon
			PopupMenu traymenu = new PopupMenu();
			trayParse = new MenuItem();
			traymenu.add(trayParse);

			trayParse.setActionCommand("Parse"); //$NON-NLS-1$
			trayParse.addActionListener(tedMain);
			// show ted
			trayShow = new MenuItem();
			traymenu.add(trayShow);

			trayShow.setActionCommand("PressAction"); //$NON-NLS-1$
			trayShow.addActionListener(tedMain);
			traymenu.addSeparator();

			trayExit = new MenuItem();
			traymenu.add(trayExit);

			trayExit.setActionCommand("Exit"); //$NON-NLS-1$
			trayExit.addActionListener(tedMain);

			tedTray.setImageAutoSize(true);

			tedTray.setPopupMenu(
					traymenu);

			this.updateText();
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	/**
	 * Set the icon of the tray
	 * @param icon
	 */
	public void setIcon(ImageIcon icon)
	{
		tedTray.setImage(icon.getImage());
	}

	/**
	 * @param header Header of balloon
	 * @param message Contents of balloon
	 * @param i msecs delay
	 */
	public void displayMessage(String header, String message, int i)
	{
		// TODO: no delay?
		tedTray.displayMessage(header, message, MessageType.INFO);

	}

	public void updateText()
	{
		trayParse.setLabel(Lang.getString("TedMainDialog.TrayMenuParse")); //$NON-NLS-1$
		trayShow.setLabel(Lang.getString("TedMainDialog.TrayMenuShowTed")); //$NON-NLS-1$
		trayExit.setLabel(Lang.getString("TedMainDialog.TrayMenuExit")); //$NON-NLS-1$

	}

}
