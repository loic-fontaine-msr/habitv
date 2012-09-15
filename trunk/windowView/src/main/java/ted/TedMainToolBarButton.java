package ted;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * TED: Torrent Episode Downloader (2005 - 2007)
 * 
 * This is a button for the maintoolbar, it sets the default button look and feel
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
public class TedMainToolBarButton extends JButton
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6830765930141737249L;
	private String LangTextString;
	private String LangToolTipString;
	
	public TedMainToolBarButton(String icon, String text, String tooltiptext, String actioncommand, TedMainDialog tMain)
	{
		// set icon
		this.setIcon(icon); //$NON-NLS-1$
		
		this.LangTextString = text;
		this.LangToolTipString = tooltiptext;
		this.updateText();
		
		// set actioncommand
		this.setActionCommand(actioncommand); //$NON-NLS-1$
		this.addActionListener(tMain);
		
		this.setBounds(16, 16, 16, 16);
		this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		// set looks
		this.setFont(new java.awt.Font("Dialog", //$NON-NLS-1$
			0, 10));
			
		this.setVerticalTextPosition(JButton.BOTTOM);
		this.setHorizontalTextPosition(JButton.CENTER);
		
	}

	public void setIcon(String string)
	{
		// set icon
		this.setIcon(new ImageIcon(getClass()
				.getClassLoader().getResource(string))); //$NON-NLS-1$
		
	}

	public void updateText()
	{
		this.setText(Lang.getString(this.LangTextString)); //$NON-NLS-1$
		this.setToolTipText(Lang.getString(this.LangToolTipString)); //$NON-NLS-1$
		
	}

}
