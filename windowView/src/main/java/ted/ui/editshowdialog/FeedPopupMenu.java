package ted.ui.editshowdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import ted.BrowserLauncher;
import ted.Lang;

/**
 * @author Joost
 * The TedPopupMenu receives a Vector of TedPopupItems. These menu items are seperated by
 * type and shown in the popup menu. After selecting an item the action that has to be
 * done is determined by the type the menu item has.
 *
 */
public class FeedPopupMenu extends JPopupMenu implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private Vector<FeedPopupItem> allItems = new Vector<FeedPopupItem>();
	private Vector<FeedPopupItem> search   = new Vector<FeedPopupItem>();
	private Vector<FeedPopupItem> category = new Vector<FeedPopupItem>();
	private Vector<FeedPopupItem> general  = new Vector<FeedPopupItem>();
	
    private final static int IS_SEARCH_BASED = 0;
    private final static int IS_CATEGORY_BASED = 1;
    private final static int IS_GENERAL_FEED = 2;
    private final static int IS_HELP = 3;
    private final static int IS_EMPTY = 4;
    private final static int IS_SEARCH_AND_AUTO = 5;
    
	private FeedPopupItem help;

	private EditShowDialog dialog;

    /**
	 * Constructs a new TedPopupMenu
	 * @param v The vector containing the JPopupItems
	 * @param dialog2 The Episode Dialog that initialized this menu
	 */
	public FeedPopupMenu(EditShowDialog dialog2, Vector<FeedPopupItem> v)
	{
		dialog = dialog2;
		
		// make a copy determine which action has to be done later on
		this.allItems = v;
			
		// add the "add empty" item
		FeedPopupItem pi = new FeedPopupItem();
		pi.setName(Lang.getString("TedEpisodeDialog.FeedsTable.UserDefined"));
		pi.setActionCommand("empty");
		pi.setType(IS_EMPTY);
		this.addItem(pi);
		pi = new FeedPopupItem();
		pi.setName(Lang.getString("TedEpisodeDialog.FeedsTable.Automatic"));
		pi.setActionCommand("auto");
		pi.setType(IS_EMPTY);
		this.addItem(pi);
		this.addSeparator();
		
		// divide the vector in groups
		this.divideMenu(allItems);
		
		// add the groups to the menu (divided by a seperator)
		this.setMenu(search);
		this.setMenu(category);
		this.setMenu(general);
		
		// add help item		
		this.addItem(help);
	}


	/**
	 * Divides the given vector in groups based on the type of the 
	 * JPopupItem 
	 */
	private void divideMenu(Vector<FeedPopupItem> v)
	{
		FeedPopupItem item;
		int type;
		for(int i=0; i<v.size(); i++)
		{
			item = (FeedPopupItem)v.get(i);
			type = item.getType();
			
			if(type==IS_SEARCH_BASED || type == IS_SEARCH_AND_AUTO)
				search.add(item);
			else if(type==IS_CATEGORY_BASED)
				category.add(item);
			else if(type==IS_GENERAL_FEED)
				general.add(item);
			else if(type==IS_HELP)
			{
				help = item;
				item.setName(Lang.getString("TedEpisodeDialog.Help"));
			}
		}
	}
	
	/**
	 * Add the items from the vector to the menu
	 */
	private void setMenu(Vector<FeedPopupItem> v)
	{
		if(v.size()!=0)
		{
			for(int i=0; i<v.size(); i++)
			{
				FeedPopupItem pi = (FeedPopupItem)v.get(i);
				this.addItem(pi);
			}
			this.addSeparator();
		}
	}
	
	
	private void addItem(FeedPopupItem item) 
	{
		item.addActionListener(this);
		this.add(item);
		
	}

	public void actionPerformed(ActionEvent arg0) 
	{
		FeedPopupItem item;
		int type;
		String action = arg0.getActionCommand();
		
		for(int i=0; i<allItems.size(); i++)
		{
			item = (FeedPopupItem)allItems.get(i);
			type = item.getType();
			
			if(item.getName().equals(action))
			{
				if (type == IS_SEARCH_BASED || type == IS_SEARCH_AND_AUTO) {
					dialog.addFeed(item.getUrl());
				}
				else if (type == IS_GENERAL_FEED) {
					if (this.openOptionDialog(item.getUrl(), item.getWebsite(), item.getType())) {
						dialog.addKeywords(dialog.getName()); // use general feeds combined with keywords
						dialog.addFeed(item.getUrl());
					}
				}
				else if(type==IS_CATEGORY_BASED) {
					// open website and add empty feed placeholder
					this.openUrl(item.getUrl());
					dialog.addFeed();
				}
				else if(type==IS_HELP)
					this.openUrl(item.getUrl());
				
				return;
			}
		}
		
		if (action.equals("empty"))
		{
			dialog.addFeed();
		}
		else if (action.equals("auto"))
		{
			dialog.generateFeeds();
		}
	}
	
	private void openUrl(String url)
	{
		try
		{
			BrowserLauncher.openURL(url);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	private boolean openOptionDialog(String url, String website, int type)
	{
		String question;
		
		question = Lang.getString("TedEpisodeDialog.DialogFindGeneral");
		
		// show question dialog
		 Object[] options = { Lang.getString("TedGeneral.Yes"), Lang.getString("TedGeneral.No"), Lang.getString("TedGeneral.Website") };
		 int selectedValue = JOptionPane.showOptionDialog(null, 
				 	question, Lang.getString("TedGeneral.Question"),
		             JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
		             null, options, options[0]);
		 
		 if (selectedValue == 0) { // Yes
			 if (dialog.getShowName().length() == 0) {
				 JOptionPane.showMessageDialog(null, Lang.getString("TedEpisodeDialog.DialogFindError"));
				 return false;
			 }
			return true;
		} else if (selectedValue == 2) { // Go to website
			this.openUrl(website);
		}

		 // No or Go to website
		 return false;
	}
}
