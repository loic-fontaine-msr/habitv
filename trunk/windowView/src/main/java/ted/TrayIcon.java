package ted;

import javax.swing.ImageIcon;

public interface TrayIcon
{

	/**
	 * Set the icon of the tray
	 * @param icon
	 */
	public abstract void setIcon(ImageIcon icon);

	/**
	 * @param header Header of balloon
	 * @param message Contents of balloon
	 * @param i msecs delay
	 */
	public abstract void displayMessage(String header, String message, int i);

	public abstract void updateText();

}