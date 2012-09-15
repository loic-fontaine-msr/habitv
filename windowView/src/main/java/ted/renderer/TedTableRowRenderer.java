package ted.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import ted.TedConfig;
import ted.TedSerie;

public class TedTableRowRenderer extends JPanel implements TableCellRenderer
{
	private final Font SMALL_FONT = new Font("Dialog",0,10);
	private final Font LARGE_FONT = new Font("Dialog",0,15);
	private final ImageIcon showPaused   = new ImageIcon(getClass().getClassLoader().getResource("icons/pause.png")); //$NON-NLS-1$
	private final ImageIcon showPlay	 = new ImageIcon(getClass().getClassLoader().getResource("icons/play.png")); //$NON-NLS-1$
	private final ImageIcon showStopped	 = new ImageIcon(getClass().getClassLoader().getResource("icons/stop.png")); //$NON-NLS-1$
	private final ImageIcon showActive   = new ImageIcon(getClass().getClassLoader().getResource("icons/icon-active-ted.gif")); //$NON-NLS-1$
	private final ImageIcon showDisabled    = new ImageIcon(getClass().getClassLoader().getResource("icons/icon-ted.gif")); //$NON-NLS-1$
	private final ImageIcon HDIcon    = new ImageIcon(getClass().getClassLoader().getResource("icons/icon-HD.png")); //$NON-NLS-1$

	
	JPanel currentPanel   = new JPanel();
	JLabel nameLabel      = new JLabel();
	JLabel searchforLabel = new JLabel();
	JLabel progressLabel  = new JLabel();
	JLabel iconLabel      = new JLabel();
	JLabel HDIconLabel      = new JLabel();
	JProgressBar progressBar = new JProgressBar(0, 100);
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3002065666816958045L;

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, java.lang.Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
		
		// TODO: move panel/label creation to init of renderer?? 
		currentPanel.setLayout(null);
		
		// we can assume value is a tedserie
		TedSerie serie = (TedSerie)value;
		  
		currentPanel.setBackground(colorForRow(row, isSelected));
		  
		// name
		nameLabel.setText(serie.getName());
		nameLabel.setFont(LARGE_FONT);
		nameLabel.setForeground(this.getFontColor(Color.BLACK, isSelected));
		nameLabel.setBounds(28, 3, nameLabel.getPreferredSize().width, nameLabel.getPreferredSize().height);
		currentPanel.add(nameLabel);
		// search for
		searchforLabel.setText(serie.getSearchForString());
		searchforLabel.setFont(SMALL_FONT);
		searchforLabel.setBounds(28, 23, searchforLabel.getPreferredSize().width, searchforLabel.getPreferredSize().height);
		searchforLabel.setForeground(this.getFontColor(Color.DARK_GRAY, isSelected));
		currentPanel.add(searchforLabel);
        // progress
		progressLabel.setText(serie.getStatusString());
		progressLabel.setForeground(this.getFontColor(Color.GRAY, isSelected));
		progressLabel.setFont(SMALL_FONT);
		progressLabel.setBounds(63, 38, progressLabel.getPreferredSize().width, progressLabel.getPreferredSize().height);
		currentPanel.add(progressLabel);
		// icon
		iconLabel.setIcon(this.getIconForShow(serie));
		iconLabel.setBounds(6, 4, iconLabel.getPreferredSize().width, iconLabel.getPreferredSize().height);
		currentPanel.add(iconLabel);
		// HD icon
		if (serie.isDownloadInHD())
		{
			HDIconLabel.setIcon(this.HDIcon);
			HDIconLabel.setBounds(6, 21, HDIconLabel.getPreferredSize().width, HDIconLabel.getPreferredSize().height);
			currentPanel.add(HDIconLabel);
		}
		else
		{
			HDIconLabel.setIcon(null);
		}
		// progress bar
		progressBar.setValue(serie.getProgress());
		progressBar.setStringPainted(true);
		progressBar.setString("");
		progressBar.setBounds (28, 41, 30, 7);
		currentPanel.add(progressBar);
        
		return currentPanel;
	 }
	
	/**
    * Returns the appropriate background color for the given row.
    */
	public Color colorForRow(int row, boolean isSelected) {
    	if (isSelected)
    	{
    		return TedConfig.getInstance().getSelectedRowColor();
    	}
    	if((row%2)==0)
    	{   
    		return TedConfig.getInstance().getEvenRowColor();
    	}
    	else 
    	{
    		return TedConfig.getInstance().getOddRowColor();
    	}
   }
	
/**
 * @param color Current font color
 * @param isSelected If font is selected
 * @return Font color for text
 */
private Color getFontColor(Color color, boolean isSelected) 
   {
	   	if (isSelected) {
	   		return Color.WHITE;
	   	} else {
	   		return color;
	   	}
	}
   
   /**
 * @param show Show to get icon for
 * @return Status icon for show
 */
private ImageIcon getIconForShow(TedSerie show)
   {
   	if (show.getActivity() == TedSerie.IS_PARSING)
		{
			return showActive;
		}
   		if (show.isCheck())
		{
			return showPlay;
		}
		else if (show.isDisabled())
		{
			return showDisabled;
		}
		else// if (show.getStatus() == TedSerie.STATUS_HOLD)
		{
			return showStopped;
		}
   }
}
