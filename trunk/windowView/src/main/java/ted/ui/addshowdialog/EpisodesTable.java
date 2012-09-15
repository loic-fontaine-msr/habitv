package ted.ui.addshowdialog;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JTable;

public class EpisodesTable extends JTable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7101237537991586021L;

	/**
     * Paints an alert box if no episodes were found
     */
    public void paint(Graphics g) {
        super.paint(g);
        final int rowCount = getRowCount();
        
        if (rowCount == 0)
        {
        	// display message that no episodes were found
        	final Rectangle clip = g.getClipBounds();
        	
        	// yellow box
        	g.setColor( new Color( 255,	255, 225));
            g.fillRect(clip.x, 0, clip.width, 20);
            // text
            g.setColor(Color.BLACK);
            g.setFont(new java.awt.Font("Dialog",0,10));
            g.drawString("Please add a show to ted", clip.x+40, 14);
        }
    }

}
