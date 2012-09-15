package ted.ui.addshowdialog;

import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JTextField;

import ted.TedSystemInfo;

public class SearchTextField extends JTextField
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3896098205514791562L;
	private ImageIcon searchIcon = new ImageIcon(getClass().getClassLoader().getResource("icons/AddShowDialog-search.png")); //$NON-NLS-1$
	
	public SearchTextField()
	{
		this.putClientProperty("JTextField.variant", "search");
	}
	
	public boolean isOpaque()
	{
		return false;
	}
	
	public void paint(Graphics g)
	{
		// first paint textfield
        super.paint(g);
        // then icon on top (for mac?)
		if (!TedSystemInfo.osIsMacLeopardOrBetter())
		{
			searchIcon.paintIcon(this, g, this.getWidth()-22,6);
		}
	}
}
