package ted.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import ted.Lang;
import ted.TedConfig;
import ted.TedSerie;
import ted.TedTableModel;
import ted.TedTablePopupMenu;
import ted.renderer.TedTableRowRenderer;
import ted.ui.editshowdialog.EditShowDialog;

/**
 * TED: Torrent Episode Downloader (2005 - 2006)
 * 
 * This is the table that holds all the shows that are in ted.
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
public class TedTable extends JTable
{

	private static final long serialVersionUID = 3101958907833506800L;
	private TedTableModel serieTableModel;
	private TedTableRowRenderer ttrr;
	private TedMainDialog tedMain;
	private TedTablePopupMenu ttPopupMenu;
	
	/**
	 * Create a table that can hold the series of ted
	 * @param main current tedmaindialog
	 * @param ttPopupMenu ted table popup menu
	 */
	public TedTable(TedMainDialog main, TedTablePopupMenu ttPopupMenu)
	{
		this.ttPopupMenu = ttPopupMenu;
		this.tedMain = main;
		
		serieTableModel = new TedTableModel();
		this.setBackground(Color.WHITE);
		this.setModel(serieTableModel);
		
		this.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		this.setEditingRow(0);
		
		//	disable horizontal lines in table
		setShowHorizontalLines(false);
        setShowVerticalLines(false);
        
		this.setRowHeight(55);
		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		ttrr = new TedTableRowRenderer();
		this.setDefaultRenderer(TedSerie.class, ttrr);
		
		this.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent evt) {
				serieTableKeyReleased(evt);
			}
		});
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				serieTableMouseClicked(evt);
			}
		});				
	}
	
	public void updateAllSeries()
	{
		Vector<TedSerie> series = this.getSeries();
		
		TedSerie serie;
		for(int i = 0; i < series.size(); ++i)
		{
			serie = series.get(i);
			serie.updateShowStatus();
		}
		
		this.sort();
	}
	
		
	/**
	 * Handles clicks on the table in the mainwindow ted
	 * @param evt MouseEvent
	 */
	private void serieTableMouseClicked(MouseEvent evt) 
	{
		// the user clicked on a serie in the table
		int row = this.getSelectedRow();
		
		// If no row in the table is selected (first time click in the table)
		// and the user would click with the right mouse button on a show
		// nothing would happen if not for this extra check.
		if (row == -1 
		 && SwingUtilities.isRightMouseButton(evt))
		{ 
			row = this.rowAtPoint(evt.getPoint());
			ListSelectionModel selectionModel = this.getSelectionModel();
			selectionModel.setSelectionInterval(row, row);
		}
		
		if (row >= 0)
		{
			TedSerie selectedserie = serieTableModel.getSerieAt(row);
			
			// else show the EpisodeDialog of the selected show
			if (evt.getClickCount() == 2)
			{	
				new EditShowDialog(tedMain, selectedserie, false);		
			}
			// or did the user click right?
			else 
			{
				boolean showBothDisabledOptions = false;
				boolean showBothAutomaticSchedulerOptions = false;
				boolean firstShowDisabled = selectedserie.isDisabled();
				boolean firstShowAutomaticEnabled = selectedserie.isUseAutoSchedule();
				// if multiple selected: check if the setting for one show is different, then show both options for enable/disable
				if (this.getSelectedRowCount() > 1)
				{
					TedSerie[] selectedShows = this.getSelectedShows();
					for (int i = 0; i < this.getSelectedRowCount(); i++)
					{
						if (selectedShows[i].isDisabled() != firstShowDisabled)
						{
							showBothDisabledOptions = true;
						}
						if (selectedShows[i].isUseAutoSchedule() != firstShowAutomaticEnabled)
						{
							showBothAutomaticSchedulerOptions = true;
						}
					}		
				}
				tedMain.checkDisabled(firstShowDisabled, showBothDisabledOptions);
				tedMain.checkAutoSchedule(firstShowAutomaticEnabled, showBothAutomaticSchedulerOptions);
				
				if (SwingUtilities.isRightMouseButton(evt))
				{			
					ttPopupMenu.checkAutoSchedule(firstShowAutomaticEnabled, showBothAutomaticSchedulerOptions);
					ttPopupMenu.checkDisabled(firstShowDisabled, showBothDisabledOptions);
					
					ttPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		}
			
		// if the user selected something, update status of buttons
		tedMain.updateButtonsAndMenu();
	}
	
	/**
	 * Handles key release on table
	 * @param evt
	 */
	private void serieTableKeyReleased(KeyEvent evt) 
	{
		int keyCode = evt.getKeyCode();
		if (keyCode == KeyEvent.VK_DELETE)
		{
			this.DeleteSelectedShows();
		}
	}
	
	
	/**
	 * Set shows in the table
	 * @param vector containing the shows
	 */
	public void setSeries(Vector<TedSerie> vector)
	{
		serieTableModel.setSeries(vector);
		this.sort();
	}

	/**
	 * @param i
	 * @return show at row i
	 */
	public TedSerie getSerieAt(int i)
	{
		return serieTableModel.getSerieAt(i);
	}
	
	/**
	 * @return the selected show or null when no show is selected
	 */
	public TedSerie getSelectedShow()
	{
		int pos = this.getSelectedRow();
		if (pos >= 0)
		{
			return this.getSerieAt(pos);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Update the table
	 */
	public void tableUpdate()
	{
		serieTableModel.tableUpdate();
	}

	/**
	 * Add a show
	 * @param newSerie
	 */
	public void addSerie(TedSerie newSerie)
	{
		serieTableModel.addSerie(newSerie);
		this.sort();
	}

	/**
	 * @return All the shows in the table
	 */
	public Vector<TedSerie> getSeries()
	{
		return serieTableModel.getSeries();
	}

	/**
	 * Update the data in the table
	 */
	public void fireTableDataChanged()
	{
		serieTableModel.fireTableDataChanged();
	}
	
	/**
	 * Delete the selected show from ted
	 */
	public void DeleteSelectedShows()
	{	
		int numSelectedShows = this.getSelectedRowCount();
		if (numSelectedShows > 0)
		{
			TedSerie[] selectedShows = this.getSelectedShows();
			int answer;
			if (numSelectedShows == 1)
			{
				// ask the user if he really wants to delete a singleshow
				answer = JOptionPane.showOptionDialog(null,
		                Lang.getString("TedMainDialog.DialogConfirmDeleteBegin") + " " + selectedShows[0].getName() + Lang.getString("TedMainDialog.DialogConfirmDeleteEnd"), //$NON-NLS-1$ //$NON-NLS-2$
		                "ted", //$NON-NLS-1$
		                JOptionPane.YES_NO_OPTION,
		                JOptionPane.QUESTION_MESSAGE, null, Lang.getYesNoLocale(), Lang.getYesNoLocale()[0]);
			}
			else
			{
				// ask the user if he really wants to delete a singleshow
				answer = JOptionPane.showOptionDialog(null,
		                "Are you sure you want to delete" + " " + numSelectedShows + " " + "shows?", //$NON-NLS-1$ //$NON-NLS-2$
		                "ted", //$NON-NLS-1$
		                JOptionPane.YES_NO_OPTION,
		                JOptionPane.QUESTION_MESSAGE, null, Lang.getYesNoLocale(), Lang.getYesNoLocale()[0]);
			
			}
			
			if (answer == JOptionPane.YES_OPTION)
			{
				this.serieTableModel.removeShows(selectedShows);
				tedMain.saveShows();
			}
		}
	}

	TedSerie[] getSelectedShows() 
	{
		TedSerie[] selectedShows = new TedSerie[this.getSelectedRowCount()];
		int [] selectedRows = this.getSelectedRows();
		
		for (int i = 0; i < this.getSelectedRowCount(); i++)
		{
			TedSerie currentShow = this.getSerieAt(selectedRows[i]);
			selectedShows[i] = currentShow;
		}
		
		return selectedShows;
	}

	/**
	 * Update all texts in the tedtable
	 */
	public void updateText()
	{
		serieTableModel.updateText();
	}
	
	/**
     * Paints empty rows too, after letting the UI delegate do
     * its painting.
     */
    public void paint(Graphics g) {
        super.paint(g);
        paintRows(g);
    }

    /**
     * Paints the backgrounds of the implied empty rows when the
     * table model is insufficient to fill all the visible area
     * available to us. We don't involve cell renderers, because
     * we have no data.
     */
    protected void paintRows(Graphics g) {
        final int rowCount = getRowCount();
        final Rectangle clip = g.getClipBounds();
        final int height = clip.y + clip.height;

        if (rowCount * rowHeight < height) {
            for (int i = rowCount; i <= height/rowHeight; ++i) {
                g.setColor(ttrr.colorForRow(i, (i == this.getSelectedRow())));
                g.fillRect(clip.x, i * rowHeight, clip.width, rowHeight);
            }
        }
        
        if (rowCount == 0)
        {
        	// display message that user has to add shows
        	// yellow box
        	g.setColor( new Color( 255,	255, 225));
            g.fillRect(clip.x, 0, clip.width, 20);
            // text
            g.setColor(Color.BLACK);
            g.setFont(new java.awt.Font("Dialog",0,13));
            g.drawString(Lang.getString("TedMainDialog.PleaseAddShows"), clip.x+10, 14);
        }
    }
    
	/**
     * Changes the behavior of a table in a JScrollPane to be more like
     * the behavior of JList, which expands to fill the available space.
     * JTable normally restricts its size to just what's needed by its
     * model.
     */
    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            JViewport parent = (JViewport) getParent();
            return (parent.getHeight() > getPreferredSize().height);
        }
        return false;
    }

	public void sort() 
	{
		if (TedConfig.getInstance().getSortType() != TedConfig.SORT_OFF)
		{
			this.serieTableModel.sortTable();
		}
	}
}
