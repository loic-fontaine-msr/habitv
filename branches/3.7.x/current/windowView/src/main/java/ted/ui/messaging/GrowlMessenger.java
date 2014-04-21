package ted.ui.messaging;

import com.growl.Growl;

public class GrowlMessenger implements MessengerInterface {

	Growl growl = null;
	private static final String GROWL_HURRAY_IDENTIFIER = "Hurray message";
	private static final String GROWL_ERROR_IDENTIFIER = "Error message";
	private static final String GROWL_GENERAL_IDENTIFIER = "General message";
	private int type = MessengerInterface.MESSENGER_GROWL;
	
	public GrowlMessenger()
	{
		// register with growl
		String [] notifications = {GROWL_GENERAL_IDENTIFIER, GROWL_HURRAY_IDENTIFIER, GROWL_ERROR_IDENTIFIER};
		
		growl = new Growl("ted", notifications, notifications, true);	
	}
		
	public void displayMessage(String title, String body) 
	{
		this.messageGrowl(title, body, GROWL_GENERAL_IDENTIFIER);
	}

	public void displayError(String title, String body) 
	{
		this.messageGrowl(title, body, GROWL_ERROR_IDENTIFIER);
		
	}

	public void displayHurray(String title, String body) 
	{
		this.messageGrowl(title, body, GROWL_HURRAY_IDENTIFIER);
		
	}
	
	private void messageGrowl (String title, String body, String type)
	{
		try 
		{
			growl.notifyGrowlOf(type, title, body);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getType() {
		return this.type;
	}

}
