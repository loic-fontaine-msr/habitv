package ted.ui.addshowdialog;

/****************************************************
 * IMPORTS
 ****************************************************/
import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ted.TedConfig;
import ted.TedSerie;
import ted.TedTableModel;
import ted.datastructures.StandardStructure;

/**
 * TED: Torrent Episode Downloader (2005 - 2006)
 * 
 * This is renderer for the table in the mainwindow of ted
 * 
 * The only thing it does is changing the background colors of odd/even rows in the table
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
public class EpisodesTableRenderer extends DefaultTableCellRenderer  
{
	/****************************************************
	 * GLOBAL VARIABLES
	 ****************************************************/
	private static final long serialVersionUID = 146021159938321504L;

	public Component getTableCellRendererComponent(JTable table, java.lang.Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
		if( !isSelected ) 
		{
			EpisodesTableModel ttmodel = (EpisodesTableModel) table.getModel();
			StandardStructure episode = ttmodel.getStandardStructureAt(row);
			// get odd/even rows a different color
			if( (row%2)==0)
			{
				setBackground(TedConfig.getInstance().getEvenRowColor());
			}
			else
			{
				setBackground(TedConfig.getInstance().getOddRowColor());
			}
			if (episode.getQuality() == 0)
			{
				// draw text lightgray
				setForeground(Color.LIGHT_GRAY);
			}
			else
			{
				// draw text black
				setForeground(Color.BLACK);
			}
		}
		else
		{
			setBackground(TedConfig.getInstance().getSelectedRowColor());
		}

		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
