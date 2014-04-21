package ted.ui.editshowdialog;

import javax.swing.JMenuItem;

/**
 * TED: Torrent Episode Downloader (2005 - 2006)
 * 
 * This is the mainwindow of ted
 * It shows all the shows with their urls, status and more and includes menus
 * and buttons for the user to interact with ted.
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

/**
 * @author Joost
 * A TedPopupItem is a menu item for the TedPopupMenu. It's advantage above an ordinary
 * JPopupItem is that this menu item is associated with a feed so it's easier to determine
 * which action has to be done after selecting an item from the TedPopupMenu.
 *
 */
public class FeedPopupItem extends JMenuItem
{	
	private String name;
	private String url;
	private String website;
	private int type;
	
	/**
	 * @param name The displayed name of the menu item
	 * @param feed The location of the feed
	 * @param type identifier for the menu item
	 */
	public FeedPopupItem(String name, String feed, String website, int type)
	{
		this.setName(name);
		this.url = feed;
		this.website = website;
		this.type = type;
	}

	public FeedPopupItem() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.setText(name);
		this.setActionCommand(name);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getWebsite() {
		return website;
	}

	public void setWebsite(String url) {
		this.website = url;
	}
}
