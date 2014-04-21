package ted.ui.messaging;

public interface MessengerInterface {
	
	public static final int MESSENGER_GROWL = 2;
	public static final int MESSENGER_TRAY = 1;
	public static final int MESSENGER_POPUP = 0;
	
	public void displayMessage(String title, String body);

	public void displayError(String title, String body);

	public void displayHurray(String title, String body);
	
	public int getType();

}
