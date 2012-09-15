package ted.ui.addshowdialog;

/****************************************************
 * IMPORTS
 ****************************************************/
import java.util.Vector;

import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;

import ted.Lang;
import ted.datastructures.SeasonEpisode;
import ted.datastructures.StandardStructure;

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
public class EpisodesTableModel extends AbstractTableModel 
{
	/****************************************************
	 * GLOBAL VARIABLES
	 ****************************************************/
	private static final long serialVersionUID = -7286125312855308470L;
	private Vector<StandardStructure> tableData = new Vector<StandardStructure>();
	private String[] tableColumns = {Lang.getString("TedTableModel.Episode"), Lang.getString("TedAddShowDialog.EpisodesTable.Heading.AirDate"), Lang.getString("TedAddShowDialog.EpisodesTable.Heading.Availability")};
	
	/****************************************************
	 * PUBLIC METHODS
	 ****************************************************/
	public int getRowCount() 
	{
		return tableData.size();
	}
	
	public Object getValueAt(int row, int col) 
	{
		// returns the values that have to be displayed in the columns of the table
		StandardStructure sRow = (StandardStructure)tableData.get(row);
		
		switch (col)
		{
			case 0:
				return sRow.getEpisodeChooserTitle();
			case 2:
				return sRow.getQuality()+"";
			case 1:
				return sRow.getFormattedAirDate(false);
		}
		return null;
		
	}
	
	/**
	 * Add a season episode to the table
	 * @param newSerie
	 */
	public void addSeasonEpisode (StandardStructure se)
	{
		tableData.add(se);
		fireTableRowsInserted(tableData.size()-1,tableData.size()-1);
	}
	
	/**
	 * Add a season episode to the table at a specific index
	 * @param newSerie
	 */
	public void addSeasonEpisode (StandardStructure se, int index)
	{
		tableData.add(index, se);
		fireTableRowsInserted(index, index);
	}
	
	/**
	 * @param row
	 * @return Serie at specified row
	 */
	public StandardStructure getStandardStructureAt(int row) 
	{
		if (row > -1)
		{
			return (StandardStructure)tableData.get(row);
		}
		else
			return null;
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
	public Vector<StandardStructure> getSeasonEpisodes() 
	{
		return tableData;
	}

	/**
	 * Set the series to the table
	 * @param vector Series
	 */
	public void setSeasonEpisodes(Vector<StandardStructure> vector) 
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
	
	public Class getColumnClass(int columnIndex)
	{
		// make sure we display the icon of the status correctly
		// looks like this is considered a dirty hack but who cares :D
		if (columnIndex == 2)
		{
			return JProgressBar.class;
		}
		if (columnIndex == 0)
		{
			return SeasonEpisode.class;
		}
		else
		{
			return Object.class;
		}
	}

	public void updateText()
	{
		
	}
	
	public int getMaxQuality()
	{
		int tempQuality = 0;
		StandardStructure sRow;
		for (int i = 0; i < this.tableData.size(); i++)
		{
			 sRow = (StandardStructure)tableData.get(i);
			 if (sRow!=null && sRow.getQuality() > tempQuality)
			 {
				 tempQuality = sRow.getQuality();
			 }
			
		}
		
		return tempQuality;
	}
}
