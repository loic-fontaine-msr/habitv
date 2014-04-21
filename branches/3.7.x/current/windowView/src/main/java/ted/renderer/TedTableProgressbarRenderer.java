package ted.renderer;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

// This class renders a JProgressBar in a table cell.
public class TedTableProgressbarRenderer extends JProgressBar
  implements TableCellRenderer
{
  /**
	 * 
	 */
	private static final long serialVersionUID = -6218838855701261348L;

// Constructor for ProgressRenderer.
  public TedTableProgressbarRenderer(int min, int max) {
    super(min, max);
    this.setStringPainted(true);
  }

  /* Returns this JProgressBar as the renderer
     for the given table cell. */
  public Component getTableCellRendererComponent(
    JTable table, Object value, boolean isSelected,
    boolean hasFocus, int row, int column)
  {
    // Set JProgressBar's percent complete value.
    setValue(Integer.parseInt((String)value));
    return this;
  }
}