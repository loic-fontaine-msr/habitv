package ted.ui.messaging;

import com.growl.Growl;

import ted.TedConfig;
import ted.TedMainDialog;
import ted.TedSystemInfo;

public class MessengerCenter 
{
	private MessengerInterface currentMessenger;
	private TedMainDialog tedMain;
	
	public MessengerCenter(TedMainDialog tedMainDialog)
	{
		this.tedMain = tedMainDialog;
		// find out what kind of messaging is supported on this system
		
		// check os and tray icon
		if (tedMain.osHasTray() && TedSystemInfo.osSupportsBalloon())
		{
			currentMessenger = new TrayMessenger(tedMain);
		}
		else if (TedSystemInfo.osIsMac())
		{
			if (this.isGrowlEnabled())
			{
				// check growl
				currentMessenger = new GrowlMessenger();
			}
			else
			{
				// nothing fancy supported
				currentMessenger = new PopupMessenger(tedMain);
			}
				
		}
		else
		{
			// nothing fancy supported
			currentMessenger = new PopupMessenger(tedMain);
		}
		
	}
	
	public void displayMessage (String title, String body)
	{
		//this.checkCurrentMessenger();
		currentMessenger.displayMessage(title, body);
	}
	
	public void displayError (String title, String body)
	{
		if (TedConfig.getInstance().isShowErrors())
		{
			//this.checkCurrentMessenger();
			currentMessenger.displayError(title, body);
		}
	}
	

	public void displayHurray(String title, String body)
	{
		if (TedConfig.getInstance().isShowHurray())
		{
			//this.checkCurrentMessenger();
			currentMessenger.displayHurray(title, body);
		}
	}
	
	private boolean isGrowlEnabled()
	{
		Growl growl = new Growl();
		return growl.isGrowlEnabled();
	}

}
