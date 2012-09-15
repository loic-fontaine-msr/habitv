package ted.ui.editshowdialog;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.table.TableColumn;

import ted.BrowserLauncher;
import ted.Lang;
import ted.TedFeedsTableModel;
import ted.TedSerie;
import ted.TedSerieFeed;
import ted.ui.TableRenderer;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class FeedsPanel extends JPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7247946873547065663L;
	private JButton jButtonMoveFeedUp;
	private JScrollPane jScrollPane1;
	private JTable feedsTable;
	private JButton jButtonMoveFeedDown;
	private JButton jFindButton;
	private JButton jOpenButton;
	private JButton jButtonDelete;
	private JToolBar feedsToolBar;
	private TedFeedsTableModel feedsTableModel = new TedFeedsTableModel();
	MouseListener popupListener = new PopupListener();
	private FeedPopupMenu findRSSPopupMenu;
	private TedSerie serie;

	public FeedsPanel(FeedPopupMenu tpm, TedSerie serie)
	{
		this.findRSSPopupMenu = tpm;
		this.serie = serie;
		this.initUI();
	}

	private void initUI()
	{
		try 
		{
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);

			feedsToolBar = new JToolBar();
			this.add(feedsToolBar, BorderLayout.SOUTH);
			feedsToolBar.setFloatable(false);
			feedsToolBar.setBorderPainted(false);
			
			jFindButton = new JButton();
			feedsToolBar.add(jFindButton);
			jFindButton.setBounds(280, 248, 70, 21);
			jFindButton.setIcon(new ImageIcon(getClass().getClassLoader()
					.getResource("icons/Aid.png")));

			jButtonDelete = new JButton();
			feedsToolBar.add(jButtonDelete);
			jButtonDelete.setActionCommand("deletefeed");
			jButtonDelete.setIcon(new ImageIcon(getClass().getClassLoader()
				.getResource("icons/Cancel.png")));
			jButtonDelete.setBounds(96, 248, 105, 21);

			feedsToolBar.addSeparator();
			jOpenButton = new JButton();
			feedsToolBar.add(jOpenButton);
			jOpenButton.setActionCommand("openfeed");
			jOpenButton.setBounds(205, 248, 70, 21);
			jOpenButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/EditShowDialog-feeds-open.png")));

			feedsToolBar.addSeparator();

			jButtonMoveFeedDown = new JButton();
			feedsToolBar.add(jButtonMoveFeedDown);
			jButtonMoveFeedDown.setActionCommand("movefeeddown");
			jButtonMoveFeedDown.setIcon(new ImageIcon(getClass()
				.getClassLoader().getResource("icons/down.png")));
			jButtonMoveFeedDown.setBounds(384, 248, 35, 21);

			jButtonMoveFeedUp = new JButton();
			feedsToolBar.add(jButtonMoveFeedUp);
			jButtonMoveFeedUp.setActionCommand("movefeedup");
			jButtonMoveFeedUp.setIcon(new ImageIcon(getClass().getClassLoader()
				.getResource("icons/up.png")));
			jButtonMoveFeedUp.setBounds(420, 248, 35, 21);
			jButtonMoveFeedUp.addActionListener(this);

			jButtonMoveFeedDown.addActionListener(this);

			jFindButton.addMouseListener(popupListener);

			jOpenButton.addActionListener(this);

			jButtonDelete.addActionListener(this);

			jScrollPane1 = new JScrollPane();
			this.add(jScrollPane1, BorderLayout.CENTER);
			jScrollPane1.setPreferredSize(new java.awt.Dimension(453, 300));

			feedsTable = new JTable();
			jScrollPane1.setViewportView(feedsTable);
			feedsTable.setModel(feedsTableModel);
			feedsTable.setAutoCreateColumnsFromModel(true);
			feedsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
			feedsTable.setEditingRow(1);
			
			TableRenderer tr = new TableRenderer();
			feedsTable.setDefaultRenderer(Object.class, tr);
			
//			 make sure the first column is always 16 width
			TableColumn	column = feedsTable.getColumnModel().getColumn(0);
			column.setMaxWidth(25);
			column.setMinWidth(16);
			
			// and the column with the type is also smaller
			column = feedsTable.getColumnModel().getColumn(2);
			column.setMaxWidth(100);
			column.setMinWidth(100);

			/*jButtonDelete.setText(Lang
				.getString("TedEpisodeDialog.ButtonDeleteFeed"));*/
			jButtonDelete.setToolTipText(Lang
				.getString("TedEpisodeDialog.ButtonToolTipDeleteFeed"));

			jOpenButton.setText(Lang.getString("TedEpisodeDialog.ButtonOpen"));
			jOpenButton.setToolTipText(Lang
				.getString("TedEpisodeDialog.ButtonToolTipOpen"));

			/*IjFindButton.setText(Lang.getString("TedEpisodeDialog.ButtonAddFeed"));*/
			jFindButton.setToolTipText(Lang
				.getString("TedEpisodeDialog.ButtonToolTipAddFeed"));

			jButtonMoveFeedDown.setToolTipText(Lang
				.getString("TedEpisodeDialog.ButtonToolTipMoveFeedDown"));

			jButtonMoveFeedUp.setToolTipText(Lang
				.getString("TedEpisodeDialog.ButtonToolTipMoveFeedUp"));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			
		}
		
	}
	
	/**
	 * Add a new feed to the table
	 */
	public void addFeed()
	{
		TedSerieFeed newFeed = new TedSerieFeed("", true); //$NON-NLS-1$
		int row = feedsTableModel.addSerie(newFeed);
		feedsTable.setRowSelectionInterval(row,row);
		
		JViewport viewport = (JViewport)feedsTable.getParent();
        Rectangle rect = feedsTable.getCellRect(row, 0, true);
        
        // The location of the viewport relative to the table
        Point pt = viewport.getViewPosition();
        rect.setLocation(rect.x-pt.x, rect.y-pt.y);
    
        // Scroll the area into view
        viewport.scrollRectToVisible(rect);
		feedsTable.requestFocus();
		
		feedsTable.editCellAt(row, 1);
	}
	
	public void addFeed(String s)
	{
		TedSerieFeed newFeed = new TedSerieFeed(s, true);
		feedsTableModel.addSerie(newFeed);
	}
	
	/**
	 * Delete the selected feed from the table
	 */
	private void deleteSelectedFeed()
	{
		// ASK for confirmation?
		// TODO: if nothing selected -> error
		int selectedRow = feedsTable.getSelectedRow();
		if (selectedRow != -1)
		{
			feedsTableModel.removeSerieAt(selectedRow);
		}
	}
	
	/**
	 * Open the url of the selected feed in the browser of the user
	 */
	private void openRSSFeed() 
	{
		// open rss url
		try 
		{
			int selectedRow = feedsTable.getSelectedRow();
			if (selectedRow != -1)
			{
				// get selected feed
				TedSerieFeed selectedFeed = feedsTableModel.getSerieAt(selectedRow);
				// convert to url to filter out weird spacings
				URL url = new URL(selectedFeed.getFilledURL(serie.getName(), serie.getCurrentSeason(),
						serie.getCurrentEpisode()));
				BrowserLauncher.openURL(url.toString());
			}
		} 
		catch (IOException e) 
		{
			//TODO: add error message
		}
	}
	
	private void moveSelectedFeedUp()
	{
		int selectedRow = feedsTable.getSelectedRow();
		if (selectedRow > 0)
		{
			feedsTableModel.moveUp(selectedRow);
			feedsTable.setRowSelectionInterval(selectedRow-1, selectedRow-1);
		}
		
	}

	private void moveSelectedFeedDown()
	{
		int selectedRow = feedsTable.getSelectedRow();
		if (selectedRow != -1 && selectedRow < feedsTableModel.getRowCount()-1)		
		{
			feedsTableModel.moveDown(selectedRow);
			feedsTable.setRowSelectionInterval(selectedRow+1, selectedRow+1);
		}
	}
	
	public void removeAllFeeds()
	{
		for (int row = feedsTableModel.getRowCount()-1; row>=0; row--)
		{
			feedsTableModel.removeSerieAt(row);
		}
	}

	/**
	 * Stop the editing of cells in the table
	 */
	public void stopEditing()
	{
		if (feedsTable.isEditing())
		{
			feedsTable.getCellEditor().stopCellEditing();
		}
	}

	public void setValues(TedSerie serie)
	{
		this.feedsTableModel.setSeriesFeeds(serie.getFeeds());
		
	}

	public void actionPerformed(ActionEvent arg0)
	{
		String action = arg0.getActionCommand();
		this.stopEditing();
		
		if (action.equals("openfeed"))
		{
			this.openRSSFeed();
		}
		else if (action.equals("addfeed"))
		{
			this.addFeed();
		}
		else if (action.equals("deletefeed"))
		{
			this.deleteSelectedFeed();
		}
		else if (action.equals("movefeedup"))
		{
			this.moveSelectedFeedUp();
		}
		else if (action.equals("movefeeddown"))
		{
			this.moveSelectedFeedDown();
		}
		
	}	
	
	// POPUP MENU
	
	class PopupListener extends MouseAdapter {
	    public void mousePressed(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    private void maybeShowPopup(MouseEvent e) {
	        if (true) { //e.isPopupTrigger() -> left clicking now also brings up menu
	            findRSSPopupMenu.show(e.getComponent(),
	                       e.getX(), e.getY());
	        }
	    }
	}
	
	

	public boolean checkValues() 
	{	
		if (feedsTableModel.getRowCount() == 0)
		{			
			return false;
		}
		return true;
	}

	public void saveValues(TedSerie currentSerie) 
	{
		if (this.checkValues())
		{
			currentSerie.setFeeds(feedsTableModel.getSerieFeeds());
		}
	}

}
