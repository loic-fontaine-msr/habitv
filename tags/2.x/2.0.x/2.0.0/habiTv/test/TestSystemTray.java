import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TestSystemTray {

	/**
	 * @param args
	 * @throws AWTException
	 */
	public static void main(String[] args) throws AWTException {

		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			final TrayIcon trayIcon;
			Image image = Toolkit.getDefaultToolkit().getImage("test/test.gif");
			PopupMenu popupmenu = new PopupMenu();
			MenuItem item = new MenuItem("Quitter");
			ActionListener exitActionListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Fermeture");
					System.exit(0);
				}
			};
			item.addActionListener(exitActionListener);
			popupmenu.add(item);

			trayIcon = new TrayIcon(image, "tooltip", popupmenu);

			MouseListener mouseListener = new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {

				}

				@Override
				public void mousePressed(MouseEvent e) {

				}

				@Override
				public void mouseExited(MouseEvent e) {

				}

				@Override
				public void mouseEntered(MouseEvent e) {

				}

				@Override
				public void mouseClicked(MouseEvent e) {
					trayIcon.displayMessage("Action", "Downloading Zapping 20%\n Exporting les guignols 30 %", TrayIcon.MessageType.INFO);
				}
			};
			trayIcon.addMouseListener(mouseListener);
			tray.add(trayIcon);
		}
	}

}
