package ted.ui.logdialog;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ted.log.ITedLogEntry;
import ted.log.TorrentLogEntry;

public class LogTable extends JTable {

	private static final long serialVersionUID = 6580112567259541791L;
	private final LogTableModel tableModel = new LogTableModel();
	private JComponent downloadComponent;
	private final LogTableRenderer tableRenderer = new LogTableRenderer();

	public LogTable(JComponent downloadComponent){
		setFont(new Font(null,0,11));
		this.setDefaultRenderer(Object.class, tableRenderer);
		this.downloadComponent = downloadComponent;
		this.downloadComponent.setEnabled(false);
		setModel(tableModel);
		setAutoCreateColumnsFromModel(true);
		setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);
		setEditingRow(0);
		showHorizontalLines = false;
		showVerticalLines = false;
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				entrySelectionChanged();
			}});
	}

	private void entrySelectionChanged(){
		ITedLogEntry entry = getSelected();
		if(entry instanceof TorrentLogEntry){
			downloadComponent.setEnabled(true);
		}
		else{
			downloadComponent.setEnabled(false);
		}
	}

	@Override
	public LogTableModel getModel(){
		return tableModel;
	}

	public ITedLogEntry getSelected(){
		int row = getSelectedRow();
		return tableModel.getValueAt(row);

	}

}