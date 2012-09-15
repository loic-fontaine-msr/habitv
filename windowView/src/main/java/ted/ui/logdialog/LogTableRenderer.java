package ted.ui.logdialog;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

import ted.TedConfig;

public class LogTableRenderer extends JTextArea implements TableCellRenderer {

	private static final long serialVersionUID = 9168163044560587586L;

	public LogTableRenderer() {
		super();
		setLineWrap(true);
		setWrapStyleWord(true);
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table,
			java.lang.Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (!isSelected) {
			// get odd/even rows a different color
			if ((row % 2) == 0) {
				setBackground(TedConfig.getInstance().getEvenRowColor());
			} else {
				setBackground(TedConfig.getInstance().getOddRowColor());
			}
		} else {
			setBackground(TedConfig.getInstance().getSelectedRowColor());
		}

		setSize(table.getWidth(),999999);

		setFont(table.getFont());
		setText((value == null) ? "" : value.toString());

		int rowHeight = (int) getPreferredSize().getHeight();
		table.setRowHeight(row, rowHeight);

		return this;
	}
}
