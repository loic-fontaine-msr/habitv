package ted;

/****************************************************
 * IMPORTS
 ****************************************************/
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;


/**
 * TED: Torrent Episode Downloader (2005 - 2007)
 * 
 * This is the about dialog of ted, it shows some trivial information
 * 
 * ted License:
 * This file is part of ted. ted and all of it's parts are licensed
 * under GNU General Public License (GPL) version 2.0
 * 
 * for more details see: http://en.wikipedia.org/wiki/GNU_General_Public_License
 * 
 * @author Roel
 * @author Joost
  */

public class TedAboutDialog extends javax.swing.JDialog implements KeyListener
{
	/****************************************************
	 * GLOBAL VARIABLES
	 ****************************************************/
	private static final long serialVersionUID = -4699341387814031284L;
	private JPanel aboutPanel;
	private Image logo;
	private double version;
	private int posSecretCode = 0;
	
	/****************************************************
	 * CONSTRUCTORS
	 ****************************************************/
	/**
	 * Show the about dialog
	 * @param version Current version of ted
	 */
	public TedAboutDialog(double version)
	{
		this.version = version;
		this.initGUI();
	}

	/****************************************************
	 * LOCAL METHODS
	 ****************************************************/	
	private void initGUI()
	{
		this.setVisible(false);
		this.setSize(300, 300);	
		this.setResizable(false);
		//this.setAlwaysOnTop(true);
		this.setTitle("About");
		{
			aboutPanel = new JPanel();
			this.getContentPane().add(aboutPanel, BorderLayout.CENTER);
			Color background = new Color(63, 63, 63);
			aboutPanel.setBackground(background);
		}
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		logo = toolkit.getImage(getClass().getClassLoader().getResource("icons/logo.jpg"));
		MediaTracker mediaTracker = new MediaTracker(this);
		mediaTracker.addImage(logo, 0);
				
		this.addKeyListener(this);
		this.repaint();
		this.setVisible(true);
	}
	
	/****************************************************
	 * PUBLIC METHODS
	 ****************************************************/	
	public void paint(Graphics g)
	{
		Color background = new Color(63, 63, 63);
		g.setColor(background);
		g.fillRect(0,0,this.getWidth(),this.getHeight());
		g.drawImage(logo, 5, 30, this);
	
		g.setColor(Color.WHITE);
		g.drawString("ted v" + version, 10, 170);
		g.drawString(Lang.getString("TedAbout.Created"), 10, 190);
		g.drawString(Lang.getString("Lang.TranslatorCredits"), 10, 210);
		String s1 = Lang.getString("TedAbout.Disclaimer1");
		String s2 = Lang.getString("TedAbout.Disclaimer2");
		String s3 = Lang.getString("TedAbout.Disclaimer3");
		g.drawString(s1, 10, 250);
		g.drawString(s2, 10, 265);
		g.drawString(s3, 10, 280);
	}

	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent arg0) {
		String secretCode = "colors";
		
		if (arg0.getKeyChar() == secretCode.charAt(posSecretCode))
		{
			posSecretCode++;
		}
		
		if (posSecretCode == secretCode.length())
		{
			// For next secret project :)
		}
		
	}
}
