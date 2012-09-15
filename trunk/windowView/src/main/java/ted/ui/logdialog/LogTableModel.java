package ted.ui.logdialog;

import java.util.Collections;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import ted.TedLog;
import ted.log.ITedLogEntry;


public class LogTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 4360590885492541195L;
	private final Vector<ITedLogEntry> logEntries = new Vector<ITedLogEntry>();
	private String[] tableColumns = {""};
	boolean displayAll = true;

	public LogTableModel(){
		getEntries();
	}
	public void setDisplayAll(){
		displayAll = true;
		getEntries();
	}
	public void setDisplaySimple(){
		displayAll = false;
		getEntries();
	}

	public boolean isDisplayAll(){
		return displayAll;
	}

	public void clearEntries(){
		TedLog.getInstance().clearLog();
		getEntries();
	}

	public void getEntries(){
		logEntries.clear();
		if(displayAll){
			logEntries.addAll(TedLog.getInstance().getAllEntries());
		}
		else{
			logEntries.addAll(TedLog.getInstance().getSimpleEntries());
		}
		Collections.reverse(logEntries);
		fireTableDataChanged();
	}

	public int getColumnCount() {
		return tableColumns.length;
	}

	public int getRowCount() {
		return logEntries.size();
	}

	public String getColumnName(int i)
	{
		return tableColumns[i];
	}

	public Object getValueAt(int row, int col) {
		ITedLogEntry entry = logEntries.get(row);
		return entry.toString();
	}

	public ITedLogEntry getValueAt(int row){
		try{
			return logEntries.get(row);
		}catch(Exception e){
			return null;
		}

	}

}
