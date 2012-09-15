package ted;

/****************************************************
 * IMPORTS
 ****************************************************/
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**
 * TED: Torrent Episode Downloader (2005 - 2006)
 * 
 * This is the tablemodel for the table in the mainwindow of ted
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
public class TedFeedsTableModel extends AbstractTableModel 
{
	/****************************************************
	 * GLOBAL VARIABLES
	 ****************************************************/
	private static final long serialVersionUID = -7286125312855308470L;
	private Vector<TedSerieFeed> tableData = new Vector<TedSerieFeed>();
	private String[] tableColumns = {" ", Lang.getString("TedEpisodeDialog.FeedsTable.RSSFeed"), Lang.getString("TedEpisodeDialog.FeedsTable.Type")};

	/****************************************************
	 * PUBLIC METHODS
	 ****************************************************/
	public int getRowCount() 
	{
		return tableData.size();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int col) 
	{
		// returns the values that have to be displayed in the columns of the table
		TedSerieFeed sRow = (TedSerieFeed)tableData.get(row);
		
		switch (col)
		{
			case 0:
				return ""+(row+1);
			case 1:
				return sRow.getUrl();
			case 2:
			{
				if(sRow.getSelfmade())
					return Lang.getString("TedEpisodeDialog.FeedsTable.UserDefined");
				else
					return Lang.getString("TedEpisodeDialog.FeedsTable.PreDefined");
			}
		}
		return null;
		
	}
	
	/**
	 * Add a serie to the table
	 * @param newSerie
	 */
	public int addSerie (TedSerieFeed newFeed)
	{
		tableData.add(newFeed);
		fireTableRowsInserted(tableData.size()-1,tableData.size()-1);
		return tableData.size()-1;
	}
	
	/**
	 * @param row
	 * @return Serie at specified row
	 */
	public TedSerieFeed getSerieAt(int row) 
	{
		return (TedSerieFeed)tableData.get(row);
	}

	/**
	 * Remove serie at specified row
	 * @param row
	 */
	public void removeSerieAt(int row) 
	{
		tableData.remove(row);
		fireTableRowsDeleted(row,row);
	}
	
	/**
	 * Clear the table
	 */
	public void clear()
	{
		tableData.clear();
		fireTableDataChanged();
	}
	
	
	public String getColumnName(int i) 
	{
		return tableColumns[i];
	}
	public int getColumnCount() 
	{
		return tableColumns.length;
	}

	/**
	 * @return Everything in the table
	 */
	public Vector<TedSerieFeed> getSerieFeeds() 
	{
		
		
		TedSerieFeed temp;
		
		// remove all feeds with an empty url
		for (int i = 0; i < tableData.size(); i++)
		{
			temp = (TedSerieFeed)tableData.get(i);

			if (temp.getUrl().equals("")) //$NON-NLS-1$
			{
				tableData.remove(i);
				i--;
			}
		}
		
		fireTableDataChanged();
		
		return tableData;
	}

	/**
	 * Set the series to the table
	 * @param vector Series
	 */
	public void setSeriesFeeds(Vector<TedSerieFeed> vector) 
	{
		tableData.clear();
		tableData.addAll(vector);
		fireTableDataChanged();		
	}

	/**
	 * Fire a update of the table
	 */
	public void tableUpdate() 
	{
		fireTableDataChanged();		
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int row, int col)
	{
		TedSerieFeed sRow = (TedSerieFeed)tableData.get(row);
		
		// column with feed url is editable but only for not predefined feeds.
		if (col == 1 && sRow.getSelfmade())
		{
			return true;
		}
		return false;
	}
	
	public void setValueAt(Object value, int row, int col) 
	{
		TedSerieFeed tsf = (TedSerieFeed)tableData.get(row);
		switch (col) 
		{
			case 1: //Name
				tsf.setUrl(value.toString());
				break;
		   
		}
	}
	
	public void deleteAllRows()
	{
		int rows = this.getRowCount();
		
		for(int i=0; i<rows; i++)
		{
			removeSerieAt(0);
		}
	}
	
	/**
	 * Move the feed @ row one up
	 * @param row Feed to move up
	 */
	public void moveUp(int row)
	{
		if (row > 0)
		{
			// switch the two feeds
			this.switchRows(row, row-1);
		}
	}
	/**
	 * Move the feed @ row one down
	 * @param row Feed to move down
	 */
	public void moveDown(int row)
	{
		if (row < tableData.size()-1)
		{
			this.switchRows(row, row+1);
		}
	}
	
	/**
	 * Switch two rows in the feedstable
	 * @param s source row
	 * @param t target row
	 */
	private void switchRows (int s, int t)
	{
		// switch the two feeds
		TedSerieFeed target = (TedSerieFeed)tableData.elementAt(t);
		tableData.setElementAt(tableData.elementAt(s), t);
		tableData.setElementAt(target, s);
		this.tableUpdate();
	}
	

}
