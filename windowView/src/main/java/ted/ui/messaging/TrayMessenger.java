package ted.ui.messaging;

import ted.TedMainDialog;
import ted.TrayIcon;

public class TrayMessenger implements MessengerInterface 
{
	private TrayIcon tray;
	private static final int FADEOUT_TIME = 500;
	private int type = MessengerInterface.MESSENGER_TRAY;
	
	public TrayMessenger(TedMainDialog tedMain) 
	{
		this.tray = tedMain.getTrayIcon();
	}

	public void displayMessage(String title, String body) 
	{
		this.messageTray(title, body);		
	}

	public void displayError(String title, String body) 
	{
		this.messageTray(title, body);
	}

	public void displayHurray(String title, String body) 
	{
		this.messageTray(title, body);
		
	}
	
	private void messageTray(String title, String body)
	{
		tray.displayMessage(title, body, FADEOUT_TIME);
	}

	public int getType() 
	{
		return this.type;
	}

}
