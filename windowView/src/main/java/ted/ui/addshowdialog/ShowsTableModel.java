package ted.ui.addshowdialog;

/****************************************************
 * IMPORTS
 ****************************************************/
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import ted.datastructures.SimpleTedSerie;

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
public class ShowsTableModel extends AbstractTableModel 
{
	/****************************************************
	 * GLOBAL VARIABLES
	 ****************************************************/
	private static final long serialVersionUID = -7286125312855308470L;
	private Vector tableData = new Vector();
	private String[] tableColumns = {""};
	
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
		SimpleTedSerie sRow = (SimpleTedSerie)tableData.get(row);
		
		switch (col)
		{
			case 0:
				return " " + sRow.getDisplayName();
		}
		return null;
		
	}
	
	/**
	 * Add a serie to the table
	 * @param newSerie
	 */
	public void addSerie (SimpleTedSerie newSerie)
	{
		tableData.add(newSerie);
		fireTableRowsInserted(tableData.size()-1,tableData.size()-1);
	}
	
	/**
	 * @param row
	 * @return Serie at specified row
	 */
	public SimpleTedSerie getSerieAt(int row) 
	{
		if (row > -1)
		{
			return (SimpleTedSerie)tableData.get(row);
		}
		else
			return null;
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
	public Vector getSeries() 
	{
		return tableData;
	}

	/**
	 * Set the series to the table
	 * @param vector Series
	 */
	public void setSeries(Vector vector) 
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

	/**
	 * Update displayed text
	 */
	public void updateText()
	{
		
	}

	public void removeSeries() {
		tableData.clear();	
	}

	
	

}
